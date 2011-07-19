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
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.util.DefaultTrimFileDataStore;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.assembly.util.TrimDataStoreUtil;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceFileWriter;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedUtil;
import org.jcvi.common.core.assembly.contig.cas.AbstractDefaultCasFileLookup;
import org.jcvi.common.core.assembly.contig.cas.AbstractOnePassCasFileVisitor;
import org.jcvi.common.core.assembly.contig.cas.CasFileInfo;
import org.jcvi.common.core.assembly.contig.cas.CasFileVisitor;
import org.jcvi.common.core.assembly.contig.cas.CasMatch;
import org.jcvi.common.core.assembly.contig.cas.CasParser;
import org.jcvi.common.core.assembly.contig.cas.CasPhdReadVisitor;
import org.jcvi.common.core.assembly.contig.cas.CasTrimMap;
import org.jcvi.common.core.assembly.contig.cas.DefaultCasGappedReferenceMap;
import org.jcvi.common.core.assembly.contig.cas.DefaultReferenceCasFileLookup;
import org.jcvi.common.core.assembly.contig.cas.EmptyCasTrimMap;
import org.jcvi.common.core.assembly.contig.cas.ReadFileType;
import org.jcvi.common.core.assembly.contig.cas.UnTrimmedExtensionTrimMap;
import org.jcvi.common.core.assembly.contig.cas.UpdateConsensusAceContigBuilder;
import org.jcvi.common.core.assembly.contig.cas.CasPhdReadVisitor.TraceDetails;
import org.jcvi.common.core.assembly.contig.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.common.core.assembly.contig.cas.read.FastaCasDataStoreFactory;
import org.jcvi.common.core.assembly.contig.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.common.core.assembly.contig.cas.read.SffTrimDataStore;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.jcvi.common.core.datastore.MultipleDataStoreWrapper;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.nuc.fasta.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffParser;
import org.jcvi.common.core.seq.read.trace.sanger.phd.IndexedPhdFileDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdWriter;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.MultipleWrapper;
import org.jcvi.io.FileUtil;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.ReadWriteFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
import org.joda.time.DateTimeUtils;
import org.joda.time.Period;

public class Cas2Consed3 {
	private final File casFile;
	private final ReadWriteDirectoryFileServer consedOutputDir;
	private final String prefix;
	private final boolean makePhdBall;
	private final boolean hasEdits;
	public Cas2Consed3(File casFile, ReadWriteDirectoryFileServer consedOutputDir, 
	        String prefix, boolean makePhdBall,boolean hasEdits){
		this.casFile=casFile;
		this.consedOutputDir = consedOutputDir;
		this.prefix = prefix;
		this.makePhdBall = makePhdBall;
		this.hasEdits = hasEdits;
	}
	private File getEditDir(){
	    File rootDir = consedOutputDir.getRootDir();
        File editDir = ConsedUtil.getEditDirFor(rootDir);
	    try {
            consedOutputDir.createNewDirIfNeeded(FileUtil.createRelavitePathFrom(rootDir, editDir));
        } catch (IOException e) {
            throw new IllegalStateException("could not create path to edit dir",e);
        }
        return editDir;
	}
	private File getPhdBallDir(){
        File rootDir = consedOutputDir.getRootDir();
        File phdBallDir = ConsedUtil.getPhdBallDirFor(rootDir);
        try {
            consedOutputDir.createNewDirIfNeeded(FileUtil.createRelavitePathFrom(rootDir, phdBallDir));
        } catch (IOException e) {
            throw new IllegalStateException("could not create path to phd ball dir",e);
        }
        return phdBallDir;
    }
	private File getPhdDir(){
        File rootDir = consedOutputDir.getRootDir();
        File phdDir = ConsedUtil.getPhdDirFor(rootDir);
        try {
            consedOutputDir.createNewDirIfNeeded(FileUtil.createRelavitePathFrom(rootDir, phdDir));
        } catch (IOException e) {
            throw new IllegalStateException("could not create path to phd ball dir",e);
        }
        return phdDir;
    }
	private File getChromatDir(){
        File rootDir = consedOutputDir.getRootDir();
        File chromatDir = ConsedUtil.getChromatDirFor(rootDir);
        try {
            consedOutputDir.createNewDirIfNeeded(FileUtil.createRelavitePathFrom(rootDir, chromatDir));
        } catch (IOException e) {
            throw new IllegalStateException("could not create path to chromat dir",e);
        }
        return chromatDir;
    }
	public void convert(TrimDataStore trimDatastore,CasTrimMap trimToUntrimmedMap ,FastQQualityCodec fastqQualityCodec) throws IOException{
	    final File casWorkingDirectory = casFile.getParentFile();
	    final File editDir =getEditDir();
	    File chromatDir = consedOutputDir.contains("chromat_dir")?
	                            getChromatDir():
	                            null;
	                        
        final File phdBallDir =getPhdBallDir();
        final File phdDir = getPhdDir();
        File logFile = consedOutputDir.createNewFile("cas2consed.log");
        PrintStream logOut = new PrintStream(logFile);
        long startTime = DateTimeUtils.currentTimeMillis();
        try{
             final AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup(casWorkingDirectory);
   
             AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(
                     new FastaCasDataStoreFactory(casWorkingDirectory,trimToUntrimmedMap,10));
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
             
             final File phdFile = new File(phdBallDir, "phd.ball.1");
            final OutputStream phdOut = new FileOutputStream(phdFile);
            try{
                TraceDetails traceDetails = new TraceDetails.Builder(fastqQualityCodec)
                                                        .chromatDir(chromatDir)
                                                        .hasEdits(hasEdits)
                                                        .build();
                 CasPhdReadVisitor visitor = new CasPhdReadVisitor(
                         casWorkingDirectory,trimToUntrimmedMap,
                         gappedReferenceMap.asList(),
                         multiTrimDataStore,
                         traceDetails
                         ) {
                    
                        @Override
                        protected void visitAcePlacedRead(AcePlacedRead acePlacedRead, Phd phd,
                                int casReferenceId) {
                            Integer refKey = Integer.valueOf(casReferenceId);
                            if(!builders.containsKey(refKey)){
                                final UpdateConsensusAceContigBuilder builder = new UpdateConsensusAceContigBuilder(
                                        referenceIdLookup.getLookupIdFor(casReferenceId), 
                                        getGappedReference(casReferenceId));
                                builder.adjustContigIdToReflectCoordinates(CoordinateSystem.RESIDUE_BASED);
                                builders.put(refKey, builder);
                            }
                            try {
                                if(!makePhdBall){
                                    final File phdFile = new File(phdDir, phd.getId()+".phd.1");
                                    final OutputStream singlePhdOut = new FileOutputStream(phdFile); 
                                    PhdWriter.writePhd(phd, singlePhdOut);
                                    IOUtil.closeAndIgnoreErrors(singlePhdOut);
                                }
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
                 
                 for(AceContig splitContig : ConsedUtil.split0xContig(contig,DefaultCoverageMap.buildCoverageMap(contig), true)){
                     numberOfContigs++;
                     numberOfReads+= splitContig.getNumberOfReads();
                     consensusOut.print(
                             new DefaultNucleotideSequenceFastaRecord(
                                     splitContig.getId(), 
                                     splitContig.getConsensus().decodeUngapped())
                             .toFormattedString());
                     AceFileWriter.writeAceContig(splitContig, phdDataStore, tempOut);
                 }
             }
             IOUtil.closeAndIgnoreErrors(tempOut,consensusOut);
             File ace = new File(editDir, prefix+".ace.1");
             OutputStream out = new FileOutputStream(ace);
             out.write(String.format("AS %d %d%n%n", numberOfContigs, numberOfReads).getBytes());
             IOUtils.copyLarge(new FileInputStream(tempAce), out);
             IOUtil.closeAndIgnoreErrors(out);
             IOUtil.deleteIgnoreError(tempAce);
             if(!makePhdBall){
                 IOUtil.deleteIgnoreError(phdFile);
             }
             else{
                 consedOutputDir.createNewSymLink("../phdball_dir/"+phdFile.getName(), 
                                 "edit_dir/phd.ball");
             }
             
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
        public ConsedDirTraceFolderCreator(File workingDir,CasTrimMap trimToUntrimmedMap,
                ReadWriteFileServer consedOutputDir) {
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
	        
	        options.addOption(new CommandLineOptionBuilder("no_phdball", "do not make a phd.ball. instead, make individual phd.1 files for each read (not recommended)")                                
                            .isFlag(true)
                            .build());
	        options.addOption(new CommandLineOptionBuilder("preserve_edits", "The sanger fasta data has been edited so that it is different than the chromatograms.  " +
	        		"This also requires untrimmed .qual and .pos files.")                                
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
	            	    //if the file name is ".something" then
                        //newChromatName will be empty, this causes problems downstream
                        //so skip any files that are hidden 
	            	    // jira case [VHTNGS-205]
	            	    if(oldChromatogram.isHidden()){
	            	        continue;
	            	    }
	            	    String newChromatName = FilenameUtils.getBaseName(oldChromatogram.getName());	            	    
	            	   
	            		File newChromatogram = outputDir.createNewFile("chromat_dir/"+newChromatName);
	            		InputStream in = new FileInputStream(oldChromatogram);
	            		OutputStream out = new FileOutputStream(newChromatogram);
	            		IOUtils.copy(in, out);
	            		IOUtil.closeAndIgnoreErrors(in,out);
	            	}
	            }
	            boolean makePhdBall = !commandLine.hasOption("no_phdball");
	            boolean hasEdits = commandLine.hasOption("preserve_edits");
	            
	            Cas2Consed3 cas2consed = new Cas2Consed3(casFile, outputDir, prefix,makePhdBall,hasEdits);
	            
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
