/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileWriter;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.DefaultAceContig;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.assembly.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.CasDataStoreFactory;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.fastq.FastQQualityCodec;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.ReadWriteFileServer;
import org.jcvi.trace.sanger.phd.IndexedPhdFileDataStore;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdWriter;
import org.jcvi.util.DefaultIndexedFileRange;
import org.jcvi.util.MultipleWrapper;
import org.joda.time.DateTime;

public class Cas2Consed3 {
	private final File casFile;
	private final ReadWriteFileServer consedOutputDir;
	private final String prefix;
	Cas2Consed3(File casFile, ReadWriteFileServer consedOutputDir, String prefix){
		this.casFile=casFile;
		this.consedOutputDir = consedOutputDir;
		this.prefix = prefix;
	}
	public void convert(TrimDataStore trimDatastore,CasTrimMap trimToUntrimmedMap ,FastQQualityCodec fastqQualityCodec) throws IOException, DataStoreException{
	    File casWorkingDirectory = casFile.getParentFile();
	    File editDir =consedOutputDir.createNewDir("edit_dir");
        File phdDir =consedOutputDir.createNewDir("phd_dir");

         final AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup(casWorkingDirectory);
         
         CasDataStoreFactory referenceDataStoreFactory= new FastaCasDataStoreFactory(casWorkingDirectory,trimToUntrimmedMap,10);       
         AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(
                 referenceDataStoreFactory);
         DefaultCasGappedReferenceMap gappedReferenceMap = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, referenceIdLookup);
         ConsedDirTraceFolderCreator numberOfReadsVisitor = new ConsedDirTraceFolderCreator(casWorkingDirectory,trimToUntrimmedMap,consedOutputDir);
         CasParser.parseCas(casFile, 
                 MultipleWrapper.createMultipleWrapper(CasFileVisitor.class,
                 referenceIdLookup,referenceNucleotideDataStore,gappedReferenceMap,
                 numberOfReadsVisitor));
         
         final Map<Integer, DefaultAceContig.Builder> builders = new HashMap<Integer, DefaultAceContig.Builder>();
         
         final File phdFile = new File(phdDir, prefix+".phd.ball");
        final OutputStream phdOut = new FileOutputStream(phdFile);
         CasPhdReadVisitor visitor = new CasPhdReadVisitor(
                 casWorkingDirectory,trimToUntrimmedMap,
                 fastqQualityCodec,gappedReferenceMap.asList(),
                 trimDatastore,
                 new DateTime()
                 ) {
            
                @Override
                protected void visitAcePlacedRead(AcePlacedRead acePlacedRead, Phd phd,
                        int casReferenceId) {
                    Integer refKey = Integer.valueOf(casReferenceId);
                    if(!builders.containsKey(refKey)){
                        builders.put(refKey, new DefaultAceContig.Builder(
                                referenceIdLookup.getLookupIdFor(casReferenceId), 
                                this.orderedGappedReferences.get(casReferenceId)));
                    }
                    try {
                        PhdWriter.writePhd(phd, phdOut);
                    } catch (IOException e) {
                        throw new RuntimeException("error writing phd record " + phd.getId(),e);
                    }
                    builders.get(refKey).addRead(acePlacedRead);
                    
                }
        };
         CasParser.parseCas(casFile, visitor);
         //here we are done building
         PhdDataStore phdDataStore = new IndexedPhdFileDataStore(phdFile, 
                     new DefaultIndexedFileRange(
                             (int)numberOfReadsVisitor.getReadCounter()),
                             true);
         long numberOfContigs=0;
         long numberOfReads =0;
         File tempAce = new File(editDir, "temp.ace");
        OutputStream tempOut = new FileOutputStream(tempAce);
         for(DefaultAceContig.Builder builder : builders.values()){
             AceContig contig =builder.build();
             CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig);
             for(AceContig splitContig : ConsedUtil.split0xContig(contig, coverageMap)){
                 numberOfContigs++;
                 numberOfReads+= splitContig.getNumberOfReads();
                 AceFileWriter.writeAceFile(splitContig, phdDataStore, tempOut);
             }
         }
         IOUtil.closeAndIgnoreErrors(tempOut);
         File ace = new File(editDir, prefix+".ace.1");
         OutputStream out = new FileOutputStream(ace);
         out.write(String.format("AS %d %d%n%n", numberOfContigs, numberOfReads).getBytes());
         IOUtils.copyLarge(new FileInputStream(tempAce), out);
         IOUtil.closeAndIgnoreErrors(out);
         tempAce.delete();
	
	}
	
	
	private static class ConsedDirTraceFolderCreator extends AbstractOnePassCasFileVisitor{
	    private final CasTrimMap trimToUntrimmedMap;
	    private final File workingDir;
	    private final ReadWriteFileServer consedOutputDir;
		/**
         * @param trimToUntrimmedMap
         */
        public ConsedDirTraceFolderCreator(File workingDir,CasTrimMap trimToUntrimmedMap,ReadWriteFileServer consedOutputDir) {
            this.trimToUntrimmedMap = trimToUntrimmedMap;
            this.workingDir = workingDir;
            this.consedOutputDir = consedOutputDir;
        }

        @Override
		protected void visitMatch(CasMatch match, long readCounter) {
			// TODO Auto-generated method stub
			
		}

        @Override
        public synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
            super.visitReadFileInfo(readFileInfo);
            for(String filename :readFileInfo.getFileNames()){
                File file = getTrimmedFileFor(filename);
                ReadFileType readType = ReadFileType.getTypeFromFile(filename);
                try{
                    switch(readType){
                        case  ILLUMINA: createSymlinkFor("solexa_dir",file);
                                        break;
                        case  SFF: createSymlinkFor("sff_dir",file);
                                        break;
                        default: //no-op
                    }
                }catch(IOException e){
                    throw new IllegalStateException("error creating consed dirs",e);
                }
            }
        }
        private File getTrimmedFileFor(String pathToDataStore) {
            File dataStoreFile = new File(workingDir, pathToDataStore);
            File trimmedDataStore = trimToUntrimmedMap.getUntrimmedFileFor(dataStoreFile);
            return trimmedDataStore;
        }
        
        private void createFolderIfDoesNotYetExist(String path) throws IOException{
            if(!consedOutputDir.contains(path)){
                consedOutputDir.createNewDir(path);
            }
        }
        
        private void createSymlinkFor(String dirName, File fileToSymlink) throws IOException{
            createFolderIfDoesNotYetExist(dirName);
            consedOutputDir.createNewSymLink(
                    fileToSymlink.getCanonicalPath(), dirName+"/"+fileToSymlink.getName()); 
        }
		
 	 
  }
}
