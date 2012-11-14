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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.DefaultScaffold;
import org.jcvi.common.core.assembly.ScaffoldBuilder;
import org.jcvi.common.core.assembly.ace.AceAssembledRead;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceFileWriter;
import org.jcvi.common.core.assembly.ace.DefaultAceContigBuilder;
import org.jcvi.common.core.assembly.ace.DefaultAceFileWriter;
import org.jcvi.common.core.assembly.ace.DefaultWholeAssemblyAceTag;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.assembly.clc.cas.AbstractCasFileVisitor;
import org.jcvi.common.core.assembly.clc.cas.CasFileInfo;
import org.jcvi.common.core.assembly.clc.cas.CasInfo;
import org.jcvi.common.core.assembly.clc.cas.CasParser;
import org.jcvi.common.core.assembly.clc.cas.CasTrimMap;
import org.jcvi.common.core.assembly.clc.cas.CasUtil;
import org.jcvi.common.core.assembly.clc.cas.EmptyCasTrimMap;
import org.jcvi.common.core.assembly.clc.cas.ExternalTrimInfo;
import org.jcvi.common.core.assembly.clc.cas.ReadFileType;
import org.jcvi.common.core.assembly.clc.cas.UnTrimmedExtensionTrimMap;
import org.jcvi.common.core.assembly.clc.cas.consed.AbstractAcePlacedReadCasReadVisitor;
import org.jcvi.common.core.assembly.scaffold.agp.AgpWriter;
import org.jcvi.common.core.assembly.util.slice.consensus.ConicConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.common.core.assembly.util.trim.TrimDataStoreUtil;
import org.jcvi.common.core.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.datastore.MultipleDataStoreWrapper;
import org.jcvi.common.core.io.FileUtil;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.read.trace.sanger.phd.IndexedPhdFileDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdWriter;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.DateUtil;
import org.jcvi.common.core.util.MapUtil;
import org.jcvi.common.io.fileServer.DirectoryFileServer;
import org.jcvi.common.io.fileServer.DirectoryFileServer.ReadWriteDirectoryFileServer;

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
	public void convert(TrimPointsDataStore trimDatastore,CasTrimMap trimToUntrimmedMap ,
			FastqQualityCodec fastqQualityCodec, 
			final boolean useConic,
			final boolean createPseduoMoleculeFasta,
			final boolean createAgp) throws IOException{
	    final File editDir =getEditDir();
	    File chromatDir = consedOutputDir.contains("chromat_dir")?
	                            getChromatDir():
	                            null;
	                        
        final File phdBallDir =getPhdBallDir();
        final File phdDir = getPhdDir();
        File logFile = consedOutputDir.createNewFile("cas2consed.log");
        PrintStream logOut = new PrintStream(logFile);
        long startTime = DateUtil.getCurrentDate().getTime();
        
        try{
            ExternalTrimInfo externalTrimInfo = ExternalTrimInfo.create(trimToUntrimmedMap, trimDatastore);
            final CasInfo casInfo = CasUtil.createCasInfoBuilder(casFile)
                                .fastQQualityCodec(fastqQualityCodec)
                                .externalTrimInfo(externalTrimInfo)
                                .hasEdits(hasEdits)
                                .chromatDir(chromatDir)
                                .build();
            final Map<Integer, DefaultAceContigBuilder> builders = new HashMap<Integer, DefaultAceContigBuilder>();
            
            final File phdFile = new File(phdBallDir, "phd.ball.1");
            final OutputStream phdOut = new FileOutputStream(phdFile);
            
            final PhdDataStore phdDataStore;
            
              try{
                  
                  AbstractAcePlacedReadCasReadVisitor visitor = new AbstractAcePlacedReadCasReadVisitor(casInfo) {
                        @Override
                        protected void visitMatch(AceAssembledRead acePlacedRead, Phd phd,
                                int casReferenceId) {
                            Integer refKey = Integer.valueOf(casReferenceId);
                            if(!builders.containsKey(refKey)){
                                final DefaultAceContigBuilder builder = new DefaultAceContigBuilder(
                                        
                                        casInfo.getReferenceIdLookup().getLookupIdFor(casReferenceId), 
                                        getGappedReference(casReferenceId));
                                if(useConic){
                                	builder.recallConsensus(new ConicConsensusCaller(PhredQuality.valueOf(30)));
                                }else{
                                	builder.recallConsensus(MostFrequentBasecallConsensusCaller.INSTANCE);
                                }
                                //
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
                        @Override
                        public void handleReadFileInfo(CasFileInfo readFileInfo) {
                            for(String filename :readFileInfo.getFileNames()){
                                File file = getUntrimmedFileFor(filename);
                                ReadFileType readType = ReadFileType.getTypeFromFile(filename);
                                try{
                                    switch(readType){
                                        case  FASTQ: createSymlinkFor("solexa_dir",file);
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
                        private File getUntrimmedFileFor(String pathToDataStore) {
                            File dataStoreFile = new File(casInfo.getCasWorkingDirectory(), pathToDataStore);
                            
                            File trimmedDataStore = casInfo.getCasTrimMap().getUntrimmedFileFor(dataStoreFile);
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
                };
                 CasParser.parseCas(casFile, visitor);
                 //here we are done building
                 phdDataStore = IndexedPhdFileDataStore.create(phdFile, (int)visitor.getReadCounter());
              
            }finally{
                IOUtil.closeAndIgnoreErrors(phdOut);
            }
             File tmpDir = File.createTempFile("temp", ".aceDir", editDir);
             //delete the temp file, then recreate it as a directory
             if(!tmpDir.delete() || !tmpDir.mkdir()){
            	 throw new IOException("Could not create temp directory: " + tmpDir.getAbsolutePath());
             }
             File ace = new File(editDir, prefix+".ace.1");
             AceFileWriter aceWriter = new DefaultAceFileWriter.Builder(ace, phdDataStore)
             							.tmpDir(tmpDir)
             							.build();
             File consensusFile = consedOutputDir.createNewFile(prefix+ ".ace.1.consensus.fasta");
             
             NucleotideSequenceFastaRecordWriter consensusOut = new NucleotideSequenceFastaRecordWriterBuilder(consensusFile)
             																.build();
             final NucleotideSequenceFastaRecordWriter pseduoMoleculeOut;
             final PrintStream agpOut;
             if(createPseduoMoleculeFasta){
            	 File pseduomoleculeFile = consedOutputDir.createNewFile(prefix+ ".ace.1.pseduomolecule.fasta");
            	 pseduoMoleculeOut = new NucleotideSequenceFastaRecordWriterBuilder(pseduomoleculeFile)
            	 								.build();
            	 
             }else{
            	 pseduoMoleculeOut=null;            	
             }
             
             if(createAgp){
            	 File agpFile = consedOutputDir.createNewFile(prefix+ ".ace.1.agp");
            	 agpOut = new PrintStream(agpFile);
             }else{
            	 agpOut=null;
             }
             Iterator<DefaultAceContigBuilder> builderIterator = builders.values().iterator();
             while(builderIterator.hasNext()){
            	 DefaultAceContigBuilder builder = builderIterator.next();                
                 builder.recallConsensusNow();
                 NucleotideSequence fullConsensus =builder.getConsensusBuilder().build();
                 long ungappedLength = fullConsensus.getUngappedLength();
                 String referenceId = builder.getContigId();                 
                 NucleotideSequenceBuilder pseduoMoleculeBuilder = new NucleotideSequenceBuilder((int)ungappedLength);
                 long previousPseduoMoleculeOffset=0;
                 ScaffoldBuilder scaffoldBuilder = DefaultScaffold.createBuilder(referenceId);
                 for(Entry<Range,AceContig> entry : ConsedUtil.split0xContig(builder,true).entrySet()){
                     AceContig splitContig = entry.getValue();                     
                     Range contigRange = entry.getKey();
                     //add split contig to current scaffold
                     scaffoldBuilder.add(splitContig.getId(), contigRange);                     
                     int numberOfUpstreamNs = (int)(contigRange.getBegin() - previousPseduoMoleculeOffset);
                	 appendNsIfNeeded(pseduoMoleculeBuilder, numberOfUpstreamNs);
                	 NucleotideSequence ungappedConsensus = new NucleotideSequenceBuilder(splitContig.getConsensusSequence())
                	 											.ungap()
                	 											.build();
                     pseduoMoleculeBuilder.append(ungappedConsensus);
                     
                     
                     
					consensusOut.write(splitContig.getId(), ungappedConsensus);
					aceWriter.write(splitContig);
                     previousPseduoMoleculeOffset = contigRange.getEnd();
                 }
                 int numberOfDownstreamNs = (int)(ungappedLength-1 - previousPseduoMoleculeOffset);
                 appendNsIfNeeded(pseduoMoleculeBuilder, numberOfDownstreamNs);
                 if(createPseduoMoleculeFasta){
                	 pseduoMoleculeOut.write(
                			 referenceId,
                			 pseduoMoleculeBuilder.build());
                 }
                 if(createAgp){
                	 AgpWriter.writeScaffold(scaffoldBuilder.build(), agpOut);
                 }
                 builderIterator.remove();
             }
             IOUtil.closeAndIgnoreErrors(consensusOut,pseduoMoleculeOut,agpOut);
            
             if(!makePhdBall){
                 IOUtil.deleteIgnoreError(phdFile);
             }
             else{
            	 aceWriter.write(new DefaultWholeAssemblyAceTag("phdBall", "consed",
                        DateUtil.getCurrentDate(), "../phdball_dir/"+phdFile.getName()));
                
             }
             IOUtil.closeAndIgnoreErrors(aceWriter);
             IOUtil.recursiveDelete(tmpDir);
             long endTime = DateUtil.getCurrentDate().getTime();
             
             logOut.printf("took %s%n", DateUtil.getElapsedTimeAsString(endTime- startTime));
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
	/**
	 * Appends the given number of N's if that value >0; otherwise do nothing.
	 * @param pseduoMoleculeBuilder
	 * @param numberOfUpstreamNs
	 */
	private void appendNsIfNeeded(NucleotideSequenceBuilder pseduoMoleculeBuilder,
			int numberOfUpstreamNs) {
		if(numberOfUpstreamNs >0){
			String ns = new String(new char[numberOfUpstreamNs]).replace('\0', 'N');
			 pseduoMoleculeBuilder.append(ns);
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
            
            options.addOption(new CommandLineOptionBuilder("use_conic", "Use conic ambiguity caller instead of most frequent")                                
            .isFlag(true)
            .build());
            
            options.addOption(new CommandLineOptionBuilder("pseduomolecule_fasta", "Create a multi fasta file of the pseduomolecules of this assembly called <prefix>.ace.1.pseduomolecule.fasta.  Each record will have the id of the reference used, along with any basecall changes from the actual assembly.  Any areas of the pseduomolecule that are not covered by this assembly will get Ns.")                                
	        .isFlag(true)    
            .build());
            options.addOption(new CommandLineOptionBuilder("agp", "Create an agp file of the scaffolds of this assembly called <prefix>.ace.1.agp. " +
            		" Each scafold will have the id of the reference used, along contig sizes and locations from the actual assembly.")                                
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
	            TrimPointsDataStore trimDatastore;
	            CasTrimMap trimToUntrimmedMap;
	            if(commandLine.hasOption("has_untrimmed")){
	                TrimPointsDataStoreExtensionBuilder trimDataStoreBuilder = new TrimPointsDataStoreExtensionBuilder(casFile.getParentFile());
	                CasParser.parseOnlyMetaData(casFile, trimDataStoreBuilder);
	                trimDatastore = trimDataStoreBuilder.build();
	                trimToUntrimmedMap = new UnTrimmedExtensionTrimMap();
	            }else{
	                trimDatastore = TrimDataStoreUtil.createEmptyTrimPointsDataStore();
	                trimToUntrimmedMap = EmptyCasTrimMap.getInstance();
	            }
	            
	            FastqQualityCodec qualityCodec=  commandLine.hasOption("useIllumina")?  
	                       FastqQualityCodec.ILLUMINA
	                    : FastqQualityCodec.SANGER;
	            
	            if(!outputDir.contains("chromat_dir")){
	                   outputDir.createNewDir("chromat_dir");
	               }
	            if(commandLine.hasOption("chromat_dir")){
	            	//need to pull out array first incase
	            	//the user updates the chromat_dir while running the program?
	            	//not sure if that matters but it can't hurt...
	            	File[] oldChromatograms = new File(commandLine.getOptionValue("chromat_dir")).listFiles();
					for(File oldChromatogram : oldChromatograms){
	            	    //if the file name is ".something" then
                        //newChromatName will be empty, this causes problems downstream
                        //so skip any files that are hidden 
	            	    // jira case [VHTNGS-205]
	            	    if(oldChromatogram.isHidden()){
	            	        continue;
	            	    }
	            	    String newChromatName = FileUtil.getBaseName(oldChromatogram);	            	    
	            	   
	            		File newChromatogram = outputDir.createNewFile("chromat_dir/"+newChromatName);
	            		InputStream in = new FileInputStream(oldChromatogram);
	            		OutputStream out = new FileOutputStream(newChromatogram);
	            		IOUtil.copy(in, out);
	            		IOUtil.closeAndIgnoreErrors(in,out);
	            	}
	            }
	            boolean makePhdBall = !commandLine.hasOption("no_phdball");
	            boolean hasEdits = commandLine.hasOption("preserve_edits");
	            boolean useConic  = commandLine.hasOption("use_conic");
	            boolean createPseduoMoleculeFasta = commandLine.hasOption("pseduomolecule_fasta");
	            boolean createAgp = commandLine.hasOption("agp");
	            Cas2Consed3 cas2consed = new Cas2Consed3(casFile, outputDir, prefix,makePhdBall,hasEdits);
	            
	            cas2consed.convert(trimDatastore, trimToUntrimmedMap, qualityCodec, useConic,createPseduoMoleculeFasta,createAgp);
	            
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
	private static class TrimPointsDataStoreExtensionBuilder extends AbstractCasFileVisitor implements Builder<TrimPointsDataStore>{

	    private final File workingDir;
	    private List<TrimPointsDataStore> delegates = new ArrayList<TrimPointsDataStore>();
	    
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
                        delegates.add(createTrimPointsDataStoreFromFile(trimpoints));
                    }else{
                        //legacy cas2consed 1 named file with capital P
                        File trimPoints = new File(file.getParentFile(), file.getName()+".trimPoints");
                        if(trimPoints.exists()){
                            delegates.add(createTrimPointsDataStoreFromFile(trimpoints));
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("error reading input file data",e);
                }
            }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TrimPointsDataStore build() {
        	if(delegates.isEmpty()){
        		return TrimDataStoreUtil.createEmptyTrimPointsDataStore();
        	}
            TrimPointsDataStore datastore= MultipleDataStoreWrapper.createMultipleDataStoreWrapper(TrimPointsDataStore.class, delegates);
            delegates = null;
            return datastore;
        }
	    
	}
	
	
		
		 /**
	     * The Trim format used by sfffile is {@code <read_id>\t<clear_left>\t<clear_right>\n}
	     * the trim points are 1-based (residue).
	     */
	    private static final Pattern TRIM_PATTERN = Pattern.compile("^(\\S+)\\s+(\\d+)\\s+(\\d+)\\s*$");
		
		public static TrimPointsDataStore createTrimPointsDataStoreFromFile(File trimpointsFile) throws IOException{
			 TextLineParser scanner = null;
			 int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(countNumberOfLines(trimpointsFile));
			 Map<String,Range> map = new HashMap<String, Range>(capacity);
		     InputStream in = new FileInputStream(trimpointsFile);   
			try {
				scanner = new TextLineParser(in);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					Matcher matcher = TRIM_PATTERN.matcher(line);
					if (matcher.matches()) {
						String id = matcher.group(1);
						Range validRange = Range.of(
								CoordinateSystem.RESIDUE_BASED,
								Integer.parseInt(matcher.group(2)),
								Integer.parseInt(matcher.group(3)));
						map.put(id, validRange);
					}
				}
			} catch (IOException e) {
				throw new IllegalStateException("error reading file", e);
			} finally {
				IOUtil.closeAndIgnoreErrors(in, scanner);
			}
			return MapDataStoreAdapter.adapt(TrimPointsDataStore.class, map);
		}
		private static long countNumberOfLines(File trimpointsFile) throws IOException {
			InputStream is = new BufferedInputStream(new FileInputStream(trimpointsFile));
		    try {
		        byte[] c = new byte[1024];
		        int count = 0;
		        int readChars = 0;
		        while ((readChars = is.read(c)) != -1) {
		            for (int i = 0; i < readChars; ++i) {
		                if (c[i] == '\n')
		                    count++;
		            }
		        }
		        return count;
		    } finally {
		        is.close();
		    }
		}
}
