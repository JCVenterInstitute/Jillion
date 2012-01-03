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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceFileWriter;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.AcePlacedReadBuilder;
import org.jcvi.common.core.assembly.ace.DefaultWholeAssemblyAceTag;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.assembly.clc.cas.AbstractCasFileVisitor;
import org.jcvi.common.core.assembly.clc.cas.AbstractOnePassCasFileVisitor;
import org.jcvi.common.core.assembly.clc.cas.CasFileInfo;
import org.jcvi.common.core.assembly.clc.cas.CasFileVisitor;
import org.jcvi.common.core.assembly.clc.cas.CasInfo;
import org.jcvi.common.core.assembly.clc.cas.CasMatch;
import org.jcvi.common.core.assembly.clc.cas.CasParser;
import org.jcvi.common.core.assembly.clc.cas.CasTrimMap;
import org.jcvi.common.core.assembly.clc.cas.CasUtil;
import org.jcvi.common.core.assembly.clc.cas.EmptyCasTrimMap;
import org.jcvi.common.core.assembly.clc.cas.ExternalTrimInfo;
import org.jcvi.common.core.assembly.clc.cas.ReadFileType;
import org.jcvi.common.core.assembly.clc.cas.UnTrimmedExtensionTrimMap;
import org.jcvi.common.core.assembly.clc.cas.UpdateConsensusAceContigBuilder;
import org.jcvi.common.core.assembly.clc.cas.consed.AbstractAcePlacedReadCasReadVisitor;
import org.jcvi.common.core.assembly.util.trim.TrimDataStore;
import org.jcvi.common.core.assembly.util.trim.TrimDataStoreUtil;
import org.jcvi.common.core.datastore.MultipleDataStoreWrapper;
import org.jcvi.common.core.io.FileUtil;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.read.trace.sanger.phd.IndexedPhdFileDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdWriter;
import org.jcvi.common.core.seq.trim.DefaultTrimFileDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.MultipleWrapper;
import org.jcvi.common.io.fileServer.DirectoryFileServer;
import org.jcvi.common.io.fileServer.ReadWriteFileServer;
import org.jcvi.common.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;
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
            ExternalTrimInfo externalTrimInfo = ExternalTrimInfo.create(trimToUntrimmedMap, trimDatastore);
            final CasInfo casInfo = CasUtil.createCasInfoBuilder(casFile)
                                .fastQQualityCodec(fastqQualityCodec)
                                .externalTrimInfo(externalTrimInfo)
                                .hasEdits(hasEdits)
                                .chromatDir(chromatDir)
                                .build();
            final Map<Integer, UpdateConsensusAceContigBuilder> builders = new HashMap<Integer, UpdateConsensusAceContigBuilder>();
            
            final File phdFile = new File(phdBallDir, "phd.ball.1");
            final OutputStream phdOut = new FileOutputStream(phdFile);
            final ConsedDirTraceFolderCreator numberOfReadsVisitor = new ConsedDirTraceFolderCreator(casWorkingDirectory,trimToUntrimmedMap,consedOutputDir);
            
            
            
              try{
                  
                  AbstractAcePlacedReadCasReadVisitor visitor = new AbstractAcePlacedReadCasReadVisitor(casInfo) {
                        @Override
                        protected void visitMatch(AcePlacedRead acePlacedRead, Phd phd,
                                int casReferenceId) {
                            Integer refKey = Integer.valueOf(casReferenceId);
                            if(!builders.containsKey(refKey)){
                                final UpdateConsensusAceContigBuilder builder = new UpdateConsensusAceContigBuilder(
                                        
                                        casInfo.getReferenceIdLookup().getLookupIdFor(casReferenceId), 
                                        getGappedReference(casReferenceId));
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
                 CasParser.parseCas(casFile, 
                         MultipleWrapper.createMultipleWrapper(CasFileVisitor.class,
                         visitor, numberOfReadsVisitor));
            }finally{
                IOUtil.closeAndIgnoreErrors(phdOut);
            }
             //here we are done building
             PhdDataStore phdDataStore = IndexedPhdFileDataStore.create(phdFile, (int)numberOfReadsVisitor.getReadCounter());
          
             long numberOfContigs=0;
             long numberOfReads =0;
             File tempAce = new File(editDir, "temp.ace");
             File consensusFile = consedOutputDir.createNewFile(prefix+ ".ace.1.consensus.fasta");
             OutputStream tempOut = new FileOutputStream(tempAce);
             PrintStream consensusOut = new PrintStream(consensusFile);
             Iterator<UpdateConsensusAceContigBuilder> builderIterator = builders.values().iterator();
             while(builderIterator.hasNext()){
                 UpdateConsensusAceContigBuilder builder = builderIterator.next();
                 builder.updateConsensus();
                 NucleotideSequence fullConsensus =builder.getConsensusBuilder().build();
                 long ungappedLength = fullConsensus.getUngappedLength();
                 long firstReadStart= fullConsensus.getLength();
                 for(AcePlacedReadBuilder readBuilder : builder.getAllPlacedReadBuilders()){
                     long start =readBuilder.getStart();
                     if(start < firstReadStart){
                         firstReadStart = start;
                     }
                 }
                 long ungappedStart = fullConsensus.getUngappedOffsetFor((int)firstReadStart);
                 //update contig id to append mapped coordinates 1- ungapped length
                 String newContigId = String.format("%s_%d_%d",builder.getContigId(),
                                     ungappedStart+1,
                                         ungappedLength);
                 builder.setContigId(newContigId);
                 for(AceContig splitContig : ConsedUtil.split0xContig(builder,true)){
                     numberOfContigs++;
                     numberOfReads+= splitContig.getNumberOfReads();
                     consensusOut.print(
                             new DefaultNucleotideSequenceFastaRecord(
                                     splitContig.getId(), 
                                     splitContig.getConsensus().asUngappedList())
                             .toFormattedString());
                     AceFileWriter.writeAceContig(splitContig, phdDataStore, tempOut);
                 }
                 builderIterator.remove();
             }
             IOUtil.closeAndIgnoreErrors(tempOut,consensusOut);
             File ace = new File(editDir, prefix+".ace.1");
             OutputStream out = new FileOutputStream(ace);
             out.write(String.format("AS %d %d%n%n", numberOfContigs, numberOfReads).getBytes());
             IOUtils.copyLarge(new FileInputStream(tempAce), out);
             
             IOUtil.deleteIgnoreError(tempAce);
             if(!makePhdBall){
                 IOUtil.deleteIgnoreError(phdFile);
             }
             else{
                 AceFileWriter.writeWholeAssemblyTag(new DefaultWholeAssemblyAceTag("phdBall", "consed",
                         new Date(DateTimeUtils.currentTimeMillis()), "../phdball_dir/"+phdFile.getName()), out);
                
             }
             IOUtil.closeAndIgnoreErrors(out);
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
	        		"Futhermore, there will also be a .trimpoints file that formatted in sfffile's tab delimmed trim format which describes the " +
	        		"trim points to convert the untrimmed basecalls into the trimmed basecalls" +
	        		"Ex: myReads.fastq -> myReads.fastq.untrimmed and myRead.fastq.trimpoints")
	        .isFlag(true)
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
	            CasTrimMap trimToUntrimmedMap;
	            if(commandLine.hasOption("has_untrimmed")){
	                TrimPointsDataStoreExtensionBuilder trimDataStoreBuilder = new TrimPointsDataStoreExtensionBuilder(casFile.getParentFile());
	                CasParser.parseOnlyMetaData(casFile, trimDataStoreBuilder);
	                trimDatastore = trimDataStoreBuilder.build();
	                trimToUntrimmedMap = new UnTrimmedExtensionTrimMap();
	            }else{
	                trimDatastore = TrimDataStoreUtil.EMPTY_DATASTORE;
	                trimToUntrimmedMap = EmptyCasTrimMap.getInstance();
	            }
	            
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
	/**
	 * Finds any files that are in the same directory and have a similar name as the input read
	 * files in the cas file.  This will find any files with the same name as 
	 * the input files plus an extra ".trimpoints" or ".trimPoints" in the file name.
	 * Ex : /path/to/data/myFile.fastq -> /path/to/data/myFile.fastq.trimpoints.
	 * @author dkatzel
	 *
	 *
	 */
	private static class TrimPointsDataStoreExtensionBuilder extends AbstractCasFileVisitor implements Builder<TrimDataStore>{

	    private final File workingDir;
	    private List<TrimDataStore> delegates = new ArrayList<TrimDataStore>();
	    
        public TrimPointsDataStoreExtensionBuilder(File workingDir) {
            this.workingDir = workingDir;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadFileInfo(CasFileInfo readFileInfo) {
            if(delegates ==null){
                throw new IllegalStateException("can only parse a single cas file once");
            }
            for(String filePath : readFileInfo.getFileNames()){
                try {
                    File file = CasUtil.getFileFor(workingDir, filePath);
                    File trimpoints = new File(file.getParentFile(), file.getName()+".trimpoints");
                    if(trimpoints.exists()){
                        delegates.add(new DefaultTrimFileDataStore(trimpoints));
                    }else{
                        //legacy cas2consed 1 named file with capital P
                        File trimPoints = new File(file.getParentFile(), file.getName()+".trimPoints");
                        if(trimPoints.exists()){
                            delegates.add(new DefaultTrimFileDataStore(trimpoints));
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("error reading input file data",e);
                }
            }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TrimDataStore build() {
        	if(delegates.isEmpty()){
        		return TrimDataStoreUtil.EMPTY_DATASTORE;
        	}
            TrimDataStore datastore= MultipleDataStoreWrapper.createMultipleDataStoreWrapper(TrimDataStore.class, delegates);
            delegates = null;
            return datastore;
        }
	    
	}
}
