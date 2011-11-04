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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.apache.commons.io.IOUtils;
import org.jcvi.common.command.Command;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.ace.AbstractAceContigBuilder;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigBuilder;
import org.jcvi.common.core.assembly.contig.ace.AceFileParser;
import org.jcvi.common.core.assembly.contig.ace.AceFileWriter;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedReadBuilder;
import org.jcvi.common.core.assembly.contig.ace.HiLowAceContigPhdDatastore;
import org.jcvi.common.core.assembly.contig.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedNavigationParser;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedNavigationVisitor;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsensusNavigationElement;
import org.jcvi.common.core.assembly.contig.ace.consed.ReadNavigationElement;
import org.jcvi.common.core.assembly.contig.slice.CompactedSlice;
import org.jcvi.common.core.assembly.contig.slice.Slice;
import org.jcvi.common.core.assembly.contig.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.assembly.contig.slice.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;

/**
 * @author dkatzel
 *
 *
 */
public class ReAbacusAceContigWorker {

    private static final int DEFAULT_FLANK_LENGTH = 20;

    private static final File MUSCLE;
    
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
     * @return
     * @throws IOException
     */
    private static int muscle(File inputFasta, File outfile) throws IOException{
        Command muscle = new Command(MUSCLE);
        muscle.setOption("-in", inputFasta.getAbsolutePath());
        muscle.setOption("-out", outfile.getAbsolutePath()); 
        muscle.addFlag("-refine");
        String line;
        Process process =muscle.execute();
        
        BufferedReader stdOutStream =null;
        try{
        stdOutStream= new BufferedReader
              (new InputStreamReader(process.getErrorStream()));
          while ((line = stdOutStream.readLine()) != null) {
             // System.out.println(line);
          }
          
          try {
            return process.waitFor();
          }catch (InterruptedException e) {
              throw new IOException("interrupted", e);
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
            if(abacusErrorMap.containsKey(contigId)){
                //only reabacus if we have to
                //use hiLow phd to get lowercase/upper case right when we write out
                //the new file.
                PhdDataStore hilowPhdDataStore = HiLowAceContigPhdDatastore.create(inputAceFile, contigId);
                reabacusContig(inputAceFile, abacusErrorMap,contigId, out, numberOfFlankingBases, hilowPhdDataStore);
            }else{
                //just stream contig?
                //use index to get file offset then just stream those bytes.
                IndexedFileRange contigOffsets = new DefaultIndexedFileRange();
                //populate offsets
                IndexedAceFileDataStore.create(inputAceFile, contigOffsets);
                Range fileRange =contigOffsets.getRangeFor(contigId);
                if(fileRange ==null){
                    throw new NullPointerException(String.format("could not find file range for contig %s", contigId));
                }
                InputStream inputStream = IOUtil.createInputStreamFromFile(inputAceFile,fileRange);
                IOUtils.copy(inputStream, out);
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
            OutputStream out, int numberOfFlankingBases, PhdDataStore phdDataStore) throws IOException {
        AbacusFixerBuilder contigFixer = new AbacusFixerBuilder(abacusErrorMap,contigId, numberOfFlankingBases, out, phdDataStore);
        AceFileParser.parseAceFile(inputAceFile, contigFixer);
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
        public AbacusFixerBuilder(Map<String, List<Range>> abacusProblemRanges, String contigId, int numberOfFlankingBases, 
                OutputStream aceOut, PhdDataStore phdDataStore) {
            this.abacusProblemRanges = abacusProblemRanges;
            this.numberOfFlankingBases = numberOfFlankingBases;
            this.aceOut = aceOut;
            this.phdDataStore = phdDataStore;
            this.contigId = contigId;
        }

        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized boolean visitContigHeader(String contigId,
                int numberOfBases, int numberOfReads, int numberOfBaseSegments,
                boolean reverseComplimented) {
           if(this.contigId.equals(contigId) && abacusProblemRanges.containsKey(contigId)){
            return super.visitContigHeader(contigId, numberOfBases, numberOfReads,
                    numberOfBaseSegments, reverseComplimented);
           }
           return false;
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
                    int gappedStart=consensus.getGappedOffsetFor((int)ungappedRange.getStart()- numberOfFlankingBases)+1;
                    int gappedEnd = consensus.getGappedOffsetFor((int)ungappedRange.getEnd()+1+numberOfFlankingBases)-1;
                   
                    Range gappedFlankingRange = Range.buildRange(gappedStart, gappedEnd);
                    rangesToMerge.add(gappedFlankingRange);
                }
                
                List<Range> reversedSortedRanges = new ArrayList<Range>(Range.mergeRanges(rangesToMerge));
                Collections.reverse(reversedSortedRanges);
               
                CoverageMap<CoverageRegion<AcePlacedReadBuilder>> coverageMap = DefaultCoverageMap.buildCoverageMap(contigBuilder.getAllPlacedReadBuilders());
                
                for(Range gappedAbacusProblemRange : reversedSortedRanges){
                    int gappedStart = (int)gappedAbacusProblemRange.getStart();
                    int gappedEnd = (int)gappedAbacusProblemRange.getEnd();
                    Range ungappedProblemRange = Range.buildRange(
                            consensus.getUngappedOffsetFor(gappedStart),
                            consensus.getUngappedOffsetFor(gappedEnd)
                            );
                    
                    System.out.println("now working on "+ ungappedProblemRange);
                  //  System.out.println("gapped abacus problem range = "+ gappedAbacusProblemRange);
                   // System.out.println(Nucleotides.asString(consensus.asList(gappedAbacusProblemRange)));
                    Set<String> affectedReads = new LinkedHashSet<String>();
                    for(CoverageRegion<AcePlacedReadBuilder> regions : coverageMap.getRegionsWhichIntersect(gappedAbacusProblemRange)){
                        for(AcePlacedReadBuilder read : regions){
                            affectedReads.add(read.getId());
                        }
                    }
                    
                 //   System.out.println("affected reads : ");
                    Map<String, NucleotideSequenceFastaRecord> ungappedSequences = new LinkedHashMap<String, NucleotideSequenceFastaRecord>();
                    int maxSeenLength=0;
                    for(String readId : affectedReads){
                        AcePlacedReadBuilder readBuilder =contigBuilder.getPlacedReadBuilder(readId);
                        int flankedStart = Math.max(0,gappedStart);
                        
                        int flankedEnd = Math.min((int)consensus.getLength()-1,gappedEnd);
                        
                      //  NucleotideSequence sequence =readBuilder.getCurrentNucleotideSequence();
                        long start = Math.max(flankedStart,readBuilder.getStart())-readBuilder.getStart();                        
                        long end  = Math.min(flankedEnd,readBuilder.getEnd())-readBuilder.getStart();
                        if(end < start){
                            //read doesn't reach abacus bug
                            //must end in flanking region
                            //so we don't care
                            continue;
                        }
                       // long gappedLength = Math.min(readBuilder.getLength() - gappedReadStartOffset, gappedAbacusProblemRange.getLength());
                        Range affectedSequenceRange = Range.buildRange(start, end); 
                     //  System.out.printf("%s\t%s\t", readId, affectedSequenceRange);
                        List<Nucleotide> gappedProblemSequence = readBuilder.getBasesBuilder().asList(affectedSequenceRange);
                      //  System.out.println(Nucleotides.asString(gappedProblemSequence));
                        String coment = String.format("%s - %s", affectedSequenceRange.getStart(), affectedSequenceRange.getEnd());
                        List<Nucleotide> ungappedProblemSequenceRange = Nucleotides.ungap(gappedProblemSequence);
                        if(ungappedProblemSequenceRange.size()>maxSeenLength){
                            maxSeenLength = ungappedProblemSequenceRange.size();
                        }
                        
                        NucleotideSequenceFastaRecord fasta = new DefaultNucleotideSequenceFastaRecord(readId, coment,ungappedProblemSequenceRange);
                        ungappedSequences.put(readId, fasta);
                    }
                    
                    PrintWriter writer;
                    File ungappedFasta;
                    File gappedFastaFile;
                    try {
                        String tempSuffix = String.format("%s.fasta",contigId);
                        ungappedFasta = File.createTempFile("abacusFixerInput", tempSuffix);
                        writer = new PrintWriter(ungappedFasta);
                        gappedFastaFile = File.createTempFile("abacusFixerMuscleOutput", tempSuffix);
                        ungappedFasta.deleteOnExit();
                        gappedFastaFile.deleteOnExit();
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                    System.out.println("writing fasta file");
                    for(NucleotideSequenceFastaRecord fasta : ungappedSequences.values()){
                        if(fasta.getSequence().getLength() < maxSeenLength){
                            int numberOfGapsToAdd = (int)(maxSeenLength -fasta.getSequence().getLength());
                            char[] gaps = new char[numberOfGapsToAdd];
                            Arrays.fill(gaps, '-');
                            //this read is too short add leading/trailing gaps to help muscle?
                            Range range =Range.parseRange(fasta.getComment());
                            NucleotideSequenceBuilder newSequenceBuilder = new NucleotideSequenceBuilder(fasta.getSequence());
                            if(range.getStart()==0){
                                //start of the read                                
                                newSequenceBuilder.prepend(new String(gaps));
                            }else{
                                newSequenceBuilder.append(new String(gaps));
                            }
                            writer.print(new DefaultNucleotideSequenceFastaRecord(fasta.getId(), fasta.getComment(),newSequenceBuilder.asList()));
                        }else{
                            writer.print(fasta.toString());
                        }
                    }
                    writer.close();
                    
                    
                    try {
                        System.out.println("running muscle... for " + ungappedProblemRange);
                        int exitCode=muscle(ungappedFasta, gappedFastaFile);
                        if(exitCode !=0){
                            throw new IllegalStateException("error with muscle call for abacus range "+ ungappedProblemRange);
                        }
                        System.out.println(exitCode);
                        NucleotideFastaDataStore gappedFastaDataStore = new DefaultNucleotideFastaFileDataStore(gappedFastaFile);
                        int consensusSize =(int)gappedFastaDataStore.iterator().next().getSequence().getLength();
                        CompactedSlice.Builder[] sliceBuilders = new CompactedSlice.Builder[consensusSize];
                        for(int i=0; i< sliceBuilders.length; i++){
                            sliceBuilders[i]= new CompactedSlice.Builder();
                        }
                        for(NucleotideSequenceFastaRecord gappedFasta :gappedFastaDataStore){
                            String id = gappedFasta.getId();
                            NucleotideSequence gappedSequence = gappedFasta.getSequence();
                            List<Nucleotide> bases = gappedSequence.asList();
                            AcePlacedReadBuilder readBuilder =contigBuilder.getPlacedReadBuilder(id);
                           
                            Range sequenceRange = Range.parseRange(gappedFasta.getComment());
                            
                            Range newGappedRange = Range.buildRange(0,bases.size()-1);
                            //will be 0 unless we are in the beginning of a read
                            //which will change this number later
                            int numberOfLeadingGaps=0;
                            
                            int i=bases.size()-1;
                            Nucleotide currentBase = bases.get(i);
                            int numberOfTrailingGaps = 0;
                            while(i>=0 && currentBase.isGap()){
                                numberOfTrailingGaps++;
                                i--;
                                currentBase = bases.get(i);
                            }     
                            NucleotideSequenceBuilder basesBuilder = readBuilder.getBasesBuilder();
                            //remove bases to fix
                            basesBuilder.delete(sequenceRange);
                            
                            long length =basesBuilder.getLength();
                            Range fixedBasesRange = newGappedRange.copy();
                            if(length-1 <sequenceRange.getStart()){
                                //we are fixing the end of the read
                                //trim off trailing gaps
                                fixedBasesRange = fixedBasesRange.shrink(0, numberOfTrailingGaps);
                            }
                            if(sequenceRange.getStart()==0){
                                //we are fixing beginning of sequence
                                //trim off leading gaps
                                numberOfLeadingGaps = gappedSequence.getGappedOffsetFor(0);
                                fixedBasesRange=fixedBasesRange.shrink(numberOfLeadingGaps, 0);
                                //need to adjust the start coordinate since it could
                                //have been originally placed incorrectly
                                readBuilder.setStartOffset(gappedStart+numberOfLeadingGaps);
                            }
                            List<Nucleotide> rangeOfBasesThatContributeToConsensus = gappedSequence.asList(fixedBasesRange);
                            basesBuilder.insert((int)sequenceRange.getStart(), rangeOfBasesThatContributeToConsensus);
                        
                             for(int index=0; index<rangeOfBasesThatContributeToConsensus.size() && index+numberOfLeadingGaps < sliceBuilders.length; index++){
                                int sliceIndex = index+numberOfLeadingGaps;                               
                                sliceBuilders[sliceIndex].addSliceElement(id, rangeOfBasesThatContributeToConsensus.get(index), 
                                        PhredQuality.valueOf(15), readBuilder.getDirection());
                            }
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
                        contigConsensusBuilder.insert((int)gappedAbacusProblemRange.getStart(), updatedConsensus);
                        //update downstream offsets
                        for(AcePlacedReadBuilder readBuilder : contigBuilder.getAllPlacedReadBuilders()){
                            
                            long oldStart =readBuilder.getStart();
                            if(oldStart>gappedAbacusProblemRange.getStart() && 
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
                       throw new IllegalStateException(e);
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
                AceFileWriter.writeAceContig(contig,phdDataStore, aceOut);
                aceOut.flush();
                System.out.println("done writing contig "+ contig.getId());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DataStoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                // TODO Auto-generated catch block
                e.printStackTrace();
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
}
