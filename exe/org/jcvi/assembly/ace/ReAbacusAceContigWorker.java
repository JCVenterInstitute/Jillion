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

package org.jcvi.assembly.ace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.Command;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Ranges;
import org.jcvi.common.core.assembly.ace.AbstractAceContigBuilder;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileUtil;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.AcePlacedReadBuilder;
import org.jcvi.common.core.assembly.ace.HiLowAceContigPhdDatastore;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationParser;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationVisitor;
import org.jcvi.common.core.assembly.ace.consed.ConsensusNavigationElement;
import org.jcvi.common.core.assembly.ace.consed.ReadNavigationElement;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.assembly.util.slice.CompactedSlice;
import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordFactory;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public class ReAbacusAceContigWorker {

    private static final int DEFAULT_FLANK_LENGTH = 20;

    private static final File MUSCLE;
    static final int MUSCLE_MAX_MEM_DEFAULT = 2000;
    static{
        try {
            MUSCLE= getPathToMuscle();
        } catch (IOException e) {
            throw new RuntimeException("error reading config file",e);
        }
    }
    /**
     * Run muscle and write the gapped alignment results
     * to the given output file as a multifasta.
     * @param inputFasta
     * @param outfile
     * @param maxMem set the max memory in MB
     * that muscle should allocate while performing alignments.
     * muscle has trouble figuring out how much ram there is on some machines 
     * this can cause problems if too much RAM is needed while computing
     *  alignments if there are lots of reads 
     *  so let's hard code a value that should be "good enough for anyone"
     * @return
     * @throws IOException
     * @throws InterruptedException 
     */
    private static int muscle(File inputFasta, File outfile, int maxMem) throws IOException, InterruptedException{
        Command muscle = new Command(MUSCLE);
        muscle.setOption("-in", inputFasta.getAbsolutePath());
        muscle.setOption("-out", outfile.getAbsolutePath());
        //muscle has trouble figuring out 
        //how much ram there is on some machines
        //this can cause problems if too much RAM 
        //is needed while computing alignments if there are lots of reads
        //so let's hard code a value that should be "good enough for anyone"
        muscle.setOption("-maxmb", ""+maxMem); 
        muscle.addFlag("-refine");
        String line;
        Process process =muscle.execute();
        
        BufferedReader stdOutStream =null;
        //we will capture the stderr but only print it if there's a problem
        //muscle prints out a lot of output
        StringBuilder stdErr = new StringBuilder();
        try{
        stdOutStream= new BufferedReader
              (new InputStreamReader(process.getErrorStream()));
          while ((line = stdOutStream.readLine()) != null) {
              stdErr.append(line).append("\n");
          }
          	try{
            int returnCode= process.waitFor();
            if(returnCode !=0){
                System.err.print(stdErr.toString());
            }
            return returnCode;
          	}catch(InterruptedException e){
          		//we've been interrupted, kill process
          		//then rethrow
          		process.destroy();
          		throw e;
          	}
        }finally{
            IOUtil.closeAndIgnoreErrors(stdOutStream);
        }
    }
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("c", "contig id to find errors for (required)")
        .longName("contig")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("nav", "consed navigation file input that says where the problems are to be fixed (required)")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to partial ace output file (required)")
        .isRequired(true)
        .longName("out")
        .build());
        options.addOption(new CommandLineOptionBuilder("muscle_max_mem", "number of MBs max that muscle is allowed " +
        		"to allocate to perform abacus re-alignments default: "+MUSCLE_MAX_MEM_DEFAULT)
        .build());
        
        
        options.addOption(new CommandLineOptionBuilder("flank", "number of bases on each side of the problem regions to include in the reabacus.  " +
        		"We add flanking bases in order to improve the alignments.  " +
        		"Default flank if not specified is "+ DEFAULT_FLANK_LENGTH)
        .build());
        options.addOption(CommandLineUtils.createHelpOption());     
        
        
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        OutputStream out=null;
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            String contigId = commandLine.getOptionValue("c");
            System.out.println("working on contig" + contigId);
            File inputAceFile = new File(commandLine.getOptionValue("a"));
            if(!inputAceFile.exists()){
                throw new IllegalArgumentException("can not see ace "+inputAceFile.getAbsolutePath());
            }
            File navigationFile = new File(commandLine.getOptionValue("nav"));
            Map<String, List<Range>> abacusErrorMap = parseAbacusErrorsFrom(navigationFile);
            out = new FileOutputStream(commandLine.getOptionValue("o"));
            int numberOfFlankingBases = commandLine.hasOption("flank")? 
                    Integer.parseInt(commandLine.getOptionValue("flank"))
                    : DEFAULT_FLANK_LENGTH;
                    
            int maxMuscleMem = commandLine.hasOption("muscle_max_mem")? 
                    Integer.parseInt(commandLine.getOptionValue("muscle_max_mem"))
                    : MUSCLE_MAX_MEM_DEFAULT;     
            if(abacusErrorMap.containsKey(contigId)){
                //only reabacus if we have to
                //use hiLow phd to get lowercase/upper case right when we write out
                //the new file.
                PhdDataStore hilowPhdDataStore = HiLowAceContigPhdDatastore.create(inputAceFile, contigId);
                reabacusContig(inputAceFile, abacusErrorMap,contigId, out, numberOfFlankingBases, hilowPhdDataStore, maxMuscleMem);
            }else{
                //just stream contig?
                //use index to get file offset then just stream those bytes.
            	CopyAceContigVisitor visitor = new CopyAceContigVisitor(out,contigId);
                AceFileParser.parse(inputAceFile, visitor);
            }
        }catch(ParseException e){
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }finally{
            IOUtil.closeAndIgnoreErrors(out);
        }

    }

    /**
     * @param inputAceFile
     * @param abacusErrorMap
     * @param contigId
     * @param out
     * @param numberOfFlankingBases TODO
     * @param phdDataStore TODO
     * @throws IOException 
     */
    private static void reabacusContig(File inputAceFile,
            Map<String, List<Range>> abacusErrorMap, String contigId,
            OutputStream out, int numberOfFlankingBases, PhdDataStore phdDataStore,
            int maxMuscleMem) throws IOException {
        AbacusFixerBuilder contigFixer = new AbacusFixerBuilder(abacusErrorMap,contigId, numberOfFlankingBases, out, phdDataStore,maxMuscleMem);
        AceFileParser.parse(inputAceFile, contigFixer);
    }
    /**
     * @param navigationFile
     * @return
     * @throws IOException 
     */
    private static Map<String, List<Range>> parseAbacusErrorsFrom(
            File navigationFile) throws IOException {
        final Map<String, List<Range>> map = new HashMap<String, List<Range>>();
        ConsedNavigationVisitor visitor = new ConsedNavigationVisitor(){

            @Override
            public void visitLine(String line) {}

            @Override
            public void visitFile() {}

            @Override
            public void visitEndOfFile() {}

            @Override
            public void visitTitle(String title) {}

            @Override
            public void visitElement(ReadNavigationElement readElement) {}

            @Override
            public void visitElement(ConsensusNavigationElement consensusElement) {
               if("CA abacus error".equals(consensusElement.getComment())){
                   String contigId =consensusElement.getTargetId();
                   Range range = consensusElement.getUngappedPositionRange();
                   if(!map.containsKey(contigId)){
                       map.put(contigId, new ArrayList<Range>());
                   }
                   map.get(contigId).add(range);
               }
                
            }
            
        };
        ConsedNavigationParser.parse(navigationFile, visitor);
        return map;
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "reAbacusAceContigWorker -a <ace file> -c <contig id> -o <partial ace out file>", 
                
                "Parse an ace file and write out ungapped consensus coordinates of abacus assembly errors",
                options,
               "Created by Danny Katzel"
                  );
    }
    
    
    
    static class AbacusFixerBuilder extends AbstractAceContigBuilder{
        private final Map<String, List<Range>> abacusProblemRanges;
        private final int numberOfFlankingBases;
        private final OutputStream aceOut;
        private final PhdDataStore phdDataStore;
        private final String contigId;
        private final int maxMuscleMem;
        public AbacusFixerBuilder(Map<String, List<Range>> abacusProblemRanges, String contigId, int numberOfFlankingBases, 
                OutputStream aceOut, PhdDataStore phdDataStore,int maxMuscleMem) {
            this.abacusProblemRanges = abacusProblemRanges;
            this.numberOfFlankingBases = numberOfFlankingBases;
            this.aceOut = aceOut;
            this.phdDataStore = phdDataStore;
            this.contigId = contigId;
            this.maxMuscleMem = maxMuscleMem;
        }

        
        @Override
		public boolean shouldVisitContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
        	return this.contigId.equals(contigId) && abacusProblemRanges.containsKey(contigId);
		}


        /**
        * {@inheritDoc}
        */
        @Override
        protected void postProcess(AceContigBuilder contigBuilder) {
            String contigId = contigBuilder.getContigId();
            System.out.println(contigId);
            if(abacusProblemRanges.containsKey(contigId)){
              
                NucleotideSequenceBuilder contigConsensusBuilder = contigBuilder.getConsensusBuilder();
                NucleotideSequence consensus = contigConsensusBuilder.build();
                
                //need to merge close ranges (including flanking)
                //because if regions are too close we
                //incorrectly shift reads the while fixing the 2nd region
                //convert to gapped
                List<Range> rangesToMerge = new ArrayList<Range>();
                for(Range ungappedRange : abacusProblemRanges.get(contigId)){
                    int gappedStart=consensus.getGappedOffsetFor((int)ungappedRange.getBegin()- numberOfFlankingBases)+1;
                    int gappedEnd = consensus.getGappedOffsetFor((int)ungappedRange.getEnd()+1+numberOfFlankingBases)-1;
                   
                    Range gappedFlankingRange = Range.of(gappedStart, gappedEnd);
                    rangesToMerge.add(gappedFlankingRange);
                }
                
                List<Range> reversedSortedRanges = new ArrayList<Range>(Ranges.merge(rangesToMerge));
                Collections.reverse(reversedSortedRanges);
               
                CoverageMap<AcePlacedReadBuilder> coverageMap = CoverageMapFactory.create(contigBuilder.getAllAssembledReadBuilders());
                
                for(Range gappedAbacusProblemRange : reversedSortedRanges){
                    int gappedStart = (int)gappedAbacusProblemRange.getBegin();
                    int gappedEnd = (int)gappedAbacusProblemRange.getEnd();
                    Range ungappedProblemRange = Range.of(
                            consensus.getUngappedOffsetFor(gappedStart),
                            consensus.getUngappedOffsetFor(gappedEnd)
                            );
                    
                    System.out.println("now working on "+ ungappedProblemRange);
                  Set<String> affectedReads = new LinkedHashSet<String>();
                    for(CoverageRegion<AcePlacedReadBuilder> regions : CoverageMapUtil.getRegionsWhichIntersect( coverageMap, gappedAbacusProblemRange)){
                        for(AcePlacedReadBuilder read : regions){
                            affectedReads.add(read.getId());
                        }
                    }
                    
                    Map<String, NucleotideSequenceFastaRecord> ungappedSequences = new LinkedHashMap<String, NucleotideSequenceFastaRecord>();
                    int maxSeenLength=0;
                    for(String readId : affectedReads){
                        AcePlacedReadBuilder readBuilder =contigBuilder.getAssembledReadBuilder(readId);
                        int flankedStart = Math.max(0,gappedStart);
                        
                        int flankedEnd = Math.min((int)consensus.getLength()-1,gappedEnd);
                        
                        long start = Math.max(flankedStart,readBuilder.getBegin())-readBuilder.getBegin();                        
                        long end  = Math.min(flankedEnd,readBuilder.getEnd())-readBuilder.getBegin();
                        if(end < start){
                            //read doesn't reach abacus bug
                            //must end in flanking region
                            //so we don't care
                            continue;
                        }
                        Range affectedSequenceRange = Range.of(start, end); 
                        NucleotideSequence ungappedProblemSequence = readBuilder.getNucleotideSequenceBuilder()
                        											.copy()
									                        		.trim(affectedSequenceRange)
									                        		.ungap()
									                        		.build();
       
                        String comment = String.format("%s - %s", affectedSequenceRange.getBegin(), affectedSequenceRange.getEnd());
                        if(ungappedProblemSequence.getLength()>maxSeenLength){
                            maxSeenLength = (int)ungappedProblemSequence.getLength();
                        }
                        
                        NucleotideSequenceFastaRecord fasta = NucleotideSequenceFastaRecordFactory.create(readId, ungappedProblemSequence,comment);
                        ungappedSequences.put(readId, fasta);
                    }
                    
                    NucleotideSequenceFastaRecordWriter writer;
                    File ungappedFasta;
                    File gappedFastaFile;
                    try {
                        String tempSuffix = String.format("%s.fasta",contigId);
                        ungappedFasta = File.createTempFile("abacusFixerInput", tempSuffix);
                        writer = new NucleotideSequenceFastaRecordWriterBuilder(ungappedFasta)
                        			.build();
                        gappedFastaFile = File.createTempFile("abacusFixerMuscleOutput", tempSuffix);
                        ungappedFasta.deleteOnExit();
                        gappedFastaFile.deleteOnExit();
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                    System.out.println("writing fasta file");
                    try{
                    for(NucleotideSequenceFastaRecord fasta : ungappedSequences.values()){
                        if(fasta.getSequence().getLength() < maxSeenLength){
                            int numberOfGapsToAdd = (int)(maxSeenLength -fasta.getSequence().getLength());
                            char[] gaps = new char[numberOfGapsToAdd];
                            Arrays.fill(gaps, '-');
                            //this read is too short add leading/trailing gaps to help muscle?
                            Range range =Range.parseRange(fasta.getComment());
                            NucleotideSequenceBuilder newSequenceBuilder = new NucleotideSequenceBuilder(fasta.getSequence());
                            if(range.getBegin()==0){
                                //start of the read                                
                                newSequenceBuilder.prepend(new String(gaps));
                            }else{
                                newSequenceBuilder.append(new String(gaps));
                            }
                            writer.write(fasta.getId(), newSequenceBuilder.build(), fasta.getComment());
                        }else{
                            writer.write(fasta);
                        }
                    }
                    } catch (IOException e) {
						throw new IllegalStateException("error writing out fastas to align",e);
					}finally{
                    	IOUtil.closeAndIgnoreErrors(writer);
                    }
                    
                    try {
                        System.out.println("running muscle... for " + ungappedProblemRange);
                        int exitCode=muscle(ungappedFasta, gappedFastaFile,maxMuscleMem);
                        if(exitCode !=0){
                            throw new IllegalStateException("error with muscle call for abacus range "+ ungappedProblemRange);
                        }
                        System.out.println(exitCode);
                        NucleotideSequenceFastaDataStore gappedFastaDataStore = DefaultNucleotideSequenceFastaFileDataStore.create(gappedFastaFile);
                        int consensusSize =(int)gappedFastaDataStore.iterator().next().getSequence().getLength();
                        CompactedSlice.Builder[] sliceBuilders = new CompactedSlice.Builder[consensusSize];
                        for(int i=0; i< sliceBuilders.length; i++){
                            sliceBuilders[i]= new CompactedSlice.Builder();
                        }
                        StreamingIterator<NucleotideSequenceFastaRecord> iter = gappedFastaDataStore.iterator();
                        try{
	                		while(iter.hasNext()){
	                			NucleotideSequenceFastaRecord gappedFasta = iter.next();
	                            String id = gappedFasta.getId();
	                            NucleotideSequence gappedSequence = gappedFasta.getSequence();
	                            AcePlacedReadBuilder readBuilder =contigBuilder.getAssembledReadBuilder(id);
	                           
	                            Range sequenceRange = Range.parseRange(gappedFasta.getComment());
	                            
	                            Range newGappedRange = new Range.Builder(gappedSequence.getLength()).build();
	                            //will be 0 unless we are in the beginning of a read
	                            //which will change this number later
	                            int numberOfLeadingGaps=0;
	                            int offsetOfLastNonGappedBase =gappedSequence.getGappedOffsetFor((int)gappedSequence.getUngappedLength()-1);
	                            long numberOfTrailingGaps = gappedSequence.getLength() - 1-offsetOfLastNonGappedBase;
	                            /*
	                            int i=bases.size()-1;
	                            Nucleotide currentBase = bases.get(i);
	                            int numberOfTrailingGaps = 0;
	                            while(i>=0 && currentBase.isGap()){
	                                numberOfTrailingGaps++;
	                                i--;
	                                currentBase = bases.get(i);
	                            }     
	                            */
	                            NucleotideSequenceBuilder basesBuilder = readBuilder.getNucleotideSequenceBuilder();
	                            //remove bases to fix
	                            basesBuilder.delete(sequenceRange);
	                            
	                            long length =basesBuilder.getLength();
	                            Range.Builder fixedBasesRange = new Range.Builder(newGappedRange);
	                            if(length-1 <sequenceRange.getBegin()){
	                                //we are fixing the end of the read
	                                //trim off trailing gaps
	                                fixedBasesRange.shrinkEnd(numberOfTrailingGaps);
	                            }
	                            if(sequenceRange.getBegin()==0){
	                                //we are fixing beginning of sequence
	                                //trim off leading gaps
	                                numberOfLeadingGaps = gappedSequence.getGappedOffsetFor(0);
	                                fixedBasesRange.shrinkBegin(numberOfLeadingGaps);
	                                //need to adjust the start coordinate since it could
	                                //have been originally placed incorrectly
	                                readBuilder.setStartOffset(gappedStart+numberOfLeadingGaps);
	                            }
	                            NucleotideSequence fixedSequence = new NucleotideSequenceBuilder(gappedSequence)
	                            										.trim(fixedBasesRange.build())
	                            										.build();
	                            basesBuilder.insert((int)sequenceRange.getBegin(), fixedSequence);
	                            Iterator<Nucleotide> fixedIter = fixedSequence.iterator();
	                            int index=0;	                            
	                            while(fixedIter.hasNext() && index+numberOfLeadingGaps < sliceBuilders.length){
	                            	int sliceIndex = index+numberOfLeadingGaps;                               
	                                sliceBuilders[sliceIndex].addSliceElement(id, fixedIter.next(), 
		                                        PhredQuality.valueOf(15), readBuilder.getDirection());
	                                index++;
	                            }
	                           
	                        }
	                    }finally{
	                    	IOUtil.closeAndIgnoreErrors(iter);
	                    }
                        
                        NucleotideSequenceBuilder consensusBuilder = new NucleotideSequenceBuilder();
                        ConsensusCaller consensusCaller = MostFrequentBasecallConsensusCaller.INSTANCE;
                       
                        for(int j=0; j< sliceBuilders.length; j++){
                            Slice slice =sliceBuilders[j].build();                          
                            consensusBuilder.append(consensusCaller.callConsensus(slice).getConsensus());
                        }
                        NucleotideSequence updatedConsensus = consensusBuilder.build();
                          
                        long numberOfBasesToShift = gappedAbacusProblemRange.getLength() - updatedConsensus.getLength();
                        System.out.println(ungappedProblemRange + "(" + gappedAbacusProblemRange + ") fixed. downstream bases shifted "+ numberOfBasesToShift);
                       contigConsensusBuilder.delete(gappedAbacusProblemRange);
                        contigConsensusBuilder.insert((int)gappedAbacusProblemRange.getBegin(), updatedConsensus);
                        //update downstream offsets
                        for(AcePlacedReadBuilder readBuilder : contigBuilder.getAllAssembledReadBuilders()){
                            
                            long oldStart =readBuilder.getBegin();
                            if(oldStart>gappedAbacusProblemRange.getBegin() && 
                                    !gappedFastaDataStore.contains(readBuilder.getId())){
                                //we have a read that starts in or beyond our fix area
                                //and is not one of the reads we reabacused
                                //this therefore is a downstream read
                                //that either starts after the abacus range
                                //or is now in the abacus range
                                //because our abacus caused insertions
                                //either way shift it down
                                readBuilder.setStartOffset((int)(oldStart-numberOfBasesToShift));
                            }
                            
                        }
                        // for(Entry<String, NucleotideSequence> gappedSequence : gappedAlignmentDataStore.)
                    } catch (Exception e) {
                       throw new IllegalStateException(
                               String.format("error re-abacusing contig %s ungapped range = ",ungappedProblemRange),e);
                    }
                }
                System.out.println("done modifying contig read to be built");
            }

        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected void visitContig(AceContig contig) {
            try {
                System.out.println("writing ace contig record");
                AceFileUtil.writeAceContig(contig,phdDataStore, aceOut);
                aceOut.flush();
                System.out.println("done writing contig "+ contig.getId());
            } catch (Exception e) {
                throw new RuntimeException("error writing out contig "+contig.getId(), e);
            }
            
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitEndOfFile() {
            try {
                aceOut.close();
            } catch (IOException e) {
            	throw new RuntimeException("error closing temp ace output", e);
            }
        }
        
    }
    
    private static final File getPathToMuscle() throws IOException{
        InputStream in = GridReAbacusAce.class.getResourceAsStream("/javacommon.config");
        Properties props =new Properties();
        try{
            props.load(in);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        if(!props.containsKey("muscle")){
            throw new IllegalStateException("could not read property 'muscle'");
        }
        return new File(props.get("muscle").toString());
     }
    
    private static class CopyAceContigVisitor implements AceFileVisitor{
    	private final OutputStream out;
    	private final String contigId;
    	private boolean streamCurrentContig=false;
    	
		public CopyAceContigVisitor(OutputStream out, String contigId) {
			this.out = out;
			this.contigId = contigId;
		}
		@Override
		public void visitLine(String line) {
			if(streamCurrentContig){
				try {
					out.write(line.getBytes(IOUtil.UTF_8));
				} catch (IOException e) {
					throw new IllegalStateException("error copying contig line",e);
				}
			}
			
		}
		@Override
		public void visitFile() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitEndOfFile() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public BeginContigReturnCode visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplemented) {
			streamCurrentContig = this.contigId.equals(contigId);
			return BeginContigReturnCode.SKIP_CURRENT_CONTIG;
			
		}
		@Override
		public void visitConsensusQualities(QualitySequence ungappedConsensusQualities) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitAssembledFromLine(String readId, Direction dir,
				int gappedStartOffset) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitBaseSegment(Range gappedConsensusRange, String readId) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitReadHeader(String readId, int gappedLength) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitTraceDescriptionLine(String traceName, String phdName,
				Date date) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public EndContigReturnCode visitEndOfContig() {
			try {
				out.flush();
			} catch (IOException e) {
				throw new IllegalStateException("error flushing output buffer",e);
			}
			//this will stop parsing as soon as we are done writing the contig
			//we care about.
			return streamCurrentContig?EndContigReturnCode.STOP_PARSING: EndContigReturnCode.KEEP_PARSING;
		}
		@Override
		public void visitBeginConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitConsensusTagComment(String comment) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitConsensusTagData(String data) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitEndConsensusTag() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			// TODO Auto-generated method stub
			
		}
    	
    	
    }
}
