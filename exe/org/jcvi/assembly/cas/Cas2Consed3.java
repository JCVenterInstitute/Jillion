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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
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
import org.jcvi.assembly.cas.read.SffTrimDataStore;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.util.DefaultTrimFileDataStore;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.assembly.util.TrimDataStoreUtil;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideEncodedSequenceFastaRecord;
import org.jcvi.fastX.fastq.FastQQualityCodec;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.ReadWriteFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;
import org.jcvi.trace.sanger.phd.IndexedPhdFileDataStore;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.jcvi.trace.sanger.phd.PhdWriter;
import org.jcvi.util.DefaultIndexedFileRange;
import org.jcvi.util.MultipleWrapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;

public class Cas2Consed3 {
	private final File casFile;
	private final ReadWriteFileServer consedOutputDir;
	private final String prefix;
	public Cas2Consed3(File casFile, ReadWriteFileServer consedOutputDir, String prefix){
		this.casFile=casFile;
		this.consedOutputDir = consedOutputDir;
		this.prefix = prefix;
	}
	public void convert(TrimDataStore trimDatastore,CasTrimMap trimToUntrimmedMap ,FastQQualityCodec fastqQualityCodec) throws IOException{
	    final File casWorkingDirectory = casFile.getParentFile();
	    final File editDir =consedOutputDir.createNewDirIfNeeded("edit_dir");
	    File chromatDir = consedOutputDir.contains("chromat_dir")?
	                        consedOutputDir.getFile("chromat_dir"):
	                            null;
        File phdDir =consedOutputDir.createNewDirIfNeeded("phd_dir");
        File logFile = consedOutputDir.createNewFile("cas2consed.log");
        PrintStream logOut = new PrintStream(logFile);
        long startTime = DateTimeUtils.currentTimeMillis();
        try{
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
             final SffTrimDataStore sffTrimDatastore = new SffTrimDataStore();
             CasFileVisitor sffTrimDataStoreVisitor  =new AbstractOnePassCasFileVisitor() {
                
                @Override
                protected void visitMatch(CasMatch match, long readCounter) {
                    //no-op
                }
    
                @Override
                public synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
                    super.visitReadFileInfo(readFileInfo);
                    for(String readFilename : readFileInfo.getFileNames()){
                            String extension =FilenameUtils.getExtension(readFilename);
                            if("sff".equals(extension)){
                                try {
                                    SffParser.parseSFF(new File(casWorkingDirectory,readFilename), sffTrimDatastore);
                                } catch (Exception e) {
                                    throw new IllegalStateException("error trying to read sff file " + readFilename,e);
                                } 
                            }
                        }
                    }
                
            };
            CasParser.parseOnlyMetaData(casFile, sffTrimDataStoreVisitor);
            TrimDataStore multiTrimDataStore =MultipleDataStoreWrapper.createMultipleDataStoreWrapper(
                    TrimDataStore.class, trimDatastore, sffTrimDatastore);
            
             final Map<Integer, DefaultAceContig.Builder> builders = new HashMap<Integer, DefaultAceContig.Builder>();
             
             final File phdFile = new File(phdDir, prefix+".phd.ball");
            final OutputStream phdOut = new FileOutputStream(phdFile);
            try{
                 CasPhdReadVisitor visitor = new CasPhdReadVisitor(
                         casWorkingDirectory,trimToUntrimmedMap,
                         fastqQualityCodec,gappedReferenceMap.asList(),
                         multiTrimDataStore,
                         new DateTime(),
                         chromatDir
                         ) {
                    
                        @Override
                        protected void visitAcePlacedRead(AcePlacedRead acePlacedRead, Phd phd,
                                int casReferenceId) {
                            Integer refKey = Integer.valueOf(casReferenceId);
                            if(!builders.containsKey(refKey)){
                                builders.put(refKey, new UpdateConsensusAceContigBuilder(
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
            }finally{
                IOUtil.closeAndIgnoreErrors(phdOut);
            }
             //here we are done building
             PhdDataStore phdDataStore = new IndexedPhdFileDataStore(phdFile, 
                         new DefaultIndexedFileRange(
                                 (int)numberOfReadsVisitor.getReadCounter()),
                                 true);
             long numberOfContigs=0;
             long numberOfReads =0;
             File tempAce = new File(editDir, "temp.ace");
             File consensusFile = consedOutputDir.createNewFile(prefix+ ".ace.1.consensus.fasta");
             OutputStream tempOut = new FileOutputStream(tempAce);
             PrintStream consensusOut = new PrintStream(consensusFile);
             for(DefaultAceContig.Builder builder : builders.values()){
                 AceContig contig =builder.build();
                 CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig);
                 for(AceContig splitContig : ConsedUtil.split0xContig(contig, coverageMap)){
                     numberOfContigs++;
                     numberOfReads+= splitContig.getNumberOfReads();
                     consensusOut.print(
                             new DefaultNucleotideEncodedSequenceFastaRecord(
                                     splitContig.getId(), 
                                     splitContig.getConsensus().decodeUngapped())
                             .toFormattedString());
                     AceFileWriter.writeAceFile(splitContig, phdDataStore, tempOut);
                 }
             }
             IOUtil.closeAndIgnoreErrors(tempOut,consensusOut);
             File ace = new File(editDir, prefix+".ace.1");
             OutputStream out = new FileOutputStream(ace);
             out.write(String.format("AS %d %d%n", numberOfContigs, numberOfReads).getBytes());
             IOUtils.copyLarge(new FileInputStream(tempAce), out);
             IOUtil.closeAndIgnoreErrors(out);
             tempAce.delete();
             consedOutputDir.createNewSymLink("../phd_dir/"+phdFile.getName(), 
                             "edit_dir/phd.ball");
             long endTime = DateTimeUtils.currentTimeMillis();
             
             logOut.printf("took %s%n",new Period(endTime- startTime));
        }catch(IOException e){
            e.printStackTrace(logOut);
            throw e;
        }catch(Throwable t){
            t.printStackTrace(logOut);
            throw new RuntimeException(t);
        }finally{
            IOUtil.closeAndIgnoreErrors(logOut);
        }
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
			//no-op
			
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
	 public static final String DEFAULT_PREFIX = "cas2consed";
	 
	public static void main(String[] args) throws IOException{
		 Options options = new Options();
	        options.addOption(new CommandLineOptionBuilder("cas", "cas file")
	                            .isRequired(true)
	                            .build());
	        
	        options.addOption(new CommandLineOptionBuilder("o", "output directory")
	                            .longName("outputDir")
	                            .isRequired(true)
	                            .build());
	        options.addOption(new CommandLineOptionBuilder("prefix", "file prefix for all generated files ( default "+DEFAULT_PREFIX +" )")                                
	                                .build());
	        options.addOption(new CommandLineOptionBuilder("has_untrimmed", "some of the input files given to CLC were actually trimmed versions of the files. " +
	        		"The full length untrimmed versions are in the same directory with the same name except they have an additional '.untrimmed' extension. " +
	        		"Ex: myReads.fastq -> myReads.fastq.untrimmed")
	        .isFlag(true)
            .build());
	        options.addOption(new CommandLineOptionBuilder("trim", "trim file in sfffile's tab delimmed trim format")                                
	                                                        .build());
	        options.addOption(new CommandLineOptionBuilder("chromat_dir", "directory of chromatograms to be converted into phd "+
	                "(it is assumed the read data for these chromatograms are in a fasta file(s) which the .cas file knows about")                                
	                        .build());

	        options.addOption(new CommandLineOptionBuilder("useIllumina", "any FASTQ files in this assembly are encoded in Illumina 1.3+ format (default is Sanger)")                                
	                            .isFlag(true)
	                            .build());
	        
	        if(CommandLineUtils.helpRequested(args)){
	        	printHelp(options);
	        	System.exit(0);
	        }

	        try {
				CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
				
				File casFile = new File(commandLine.getOptionValue("cas"));
	            ReadWriteDirectoryFileServer outputDir = 
	                    DirectoryFileServer.createReadWriteDirectoryFileServer(commandLine.getOptionValue("o"));
	            
	            String prefix = commandLine.hasOption("prefix")? commandLine.getOptionValue("prefix"): DEFAULT_PREFIX;
	            TrimDataStore trimDatastore;
	            if(commandLine.hasOption("trim")){
	                List<TrimDataStore> dataStores = new ArrayList<TrimDataStore>();
	                final String trimFiles = commandLine.getOptionValue("trim");
	                for(String trimFile : trimFiles.split(",")){
	                    dataStores.add( new DefaultTrimFileDataStore(new File(trimFile)));
	                }
	                trimDatastore = MultipleDataStoreWrapper.createMultipleDataStoreWrapper(TrimDataStore.class, dataStores);
	            }else{
	                trimDatastore = TrimDataStoreUtil.EMPTY_DATASTORE;
	            }
	            CasTrimMap trimToUntrimmedMap = commandLine.hasOption("has_untrimmed")?
	                                new UnTrimmedExtensionTrimMap():
	                                    EmptyCasTrimMap.getInstance();
	                                
	            FastQQualityCodec qualityCodec=  commandLine.hasOption("useIllumina")?  
	                       FastQQualityCodec.ILLUMINA
	                    : FastQQualityCodec.SANGER;
	            
	            if(!outputDir.contains("chromat_dir")){
	                   outputDir.createNewDir("chromat_dir");
	               }
	            if(commandLine.hasOption("chromat_dir")){
	            	for(File oldChromatogram : new File(commandLine.getOptionValue("chromat_dir")).listFiles()){
	            	    String newChromatName = FilenameUtils.getBaseName(oldChromatogram.getName());
	            		File newChromatogram = outputDir.createNewFile("chromat_dir/"+newChromatName);
	            		InputStream in = new FileInputStream(oldChromatogram);
	            		OutputStream out = new FileOutputStream(newChromatogram);
	            		IOUtils.copy(in, out);
	            		IOUtil.closeAndIgnoreErrors(in,out);
	            	}
	            }
	            Cas2Consed3 cas2consed = new Cas2Consed3(casFile, outputDir, prefix);
	            
	            cas2consed.convert(trimDatastore, trimToUntrimmedMap, qualityCodec);
	            
	        } catch (ParseException e) {
				e.printStackTrace();
				printHelp(options);
	        	System.exit(1);
			}
	        
	}
	
	private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "cas2consed -cas <cas file> -o <output dir> [OPTIONS]", 
                
                "convert a clc .cas assembly file into a consed package. " +
                "Please note, any 0x regions will be split into multiple " +
                "contigs because consed can not handle 0x regions.  Also, " +
                "the consensus will be recalled to 'most frequent basecall' " +
                "which may sometimes differ from the .cas 'consensus' which " +
                "is actually just the reference.",
                options,
                "Created by Danny Katzel");
    }
}
