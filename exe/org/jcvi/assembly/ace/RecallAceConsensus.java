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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.Range;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.assembly.ace.consed.PhdDirQualityDataStore;
import org.jcvi.assembly.contig.qual.GapQualityValueStrategies;
import org.jcvi.assembly.slice.CompactedSliceMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.assembly.slice.consensus.ConicConsensusCaller;
import org.jcvi.assembly.slice.consensus.ConsensusCaller;
import org.jcvi.assembly.slice.consensus.ConsensusResult;
import org.jcvi.assembly.slice.consensus.NoAmbiguityConsensusCaller;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.common.core.seq.read.trace.TraceQualityDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.IndexedAceFileDataStore;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.util.DefaultIndexedFileRange;
import org.jcvi.util.MultipleWrapper;

/**
 * @author dkatzel
 *
 *
 */
public class RecallAceConsensus {

    private static enum RecallType{
        CONIC("conic"),
        NO_AMBIGUITY("no_ambiguity")
        
        ;
        
        private static Map<String, RecallType> map;
        static{
            RecallType[] values = values();
            map = new HashMap<String, RecallType>(values.length);
            for(RecallType value : values){
                map.put(value.type, value);
            }
        }
        private final String type;
        
        RecallType(String type){
            this.type = type;
        }
        
        public static RecallType parse(String s){
            for(Entry<String, RecallType> entry : map.entrySet()){
                String key = entry.getKey();
                if(key.equalsIgnoreCase(s)){
                    return entry.getValue();
                }
            }
            throw new IllegalArgumentException("could not parse value "+s);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String toString() {
            return type;
        }
        
    }
    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("ace","input ace file")
                            .isRequired(true)        
                            .build());
        
        options.addOption(new CommandLineOptionBuilder("o","output ace file")
        .longName("out")
        .isRequired(true)        
        .build());
        options.addOption(new CommandLineOptionBuilder("fasta","output fasta file")       
        .build());
        options.addOption(new CommandLineOptionBuilder("recall_with","consensus recall method must be either : "+ RecallType.map.keySet())
        .isRequired(true)        
        .build());
        options.addOption(CommandLineUtils.createHelpOption());
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }

        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
        
            File inputAceFile = new File(commandLine.getOptionValue("ace"));
            File consedDir = inputAceFile.getParentFile().getParentFile();
            File phdDir = ConsedUtil.getPhdDirFor(consedDir);
            File phdballDir = ConsedUtil.getPhdBallDirFor(consedDir);
            PrintWriter fastaOut = commandLine.hasOption("fasta") ?
                    new PrintWriter(new File(commandLine.getOptionValue("fasta"))):
                        null;
            
            File outputAceFile = new File(commandLine.getOptionValue("out"));
            final OutputStream out = new FileOutputStream(outputAceFile);
            PhdDataStore phdballDataStore = new PhdDirQualityDataStore(phdballDir);
            PhdDataStore phdDataStore = new PhdDirQualityDataStore(phdDir);
            PhdDataStore masterPhdDataStore= MultipleDataStoreWrapper.createMultipleDataStoreWrapper(PhdDataStore.class, phdDataStore,phdballDataStore);
            QualityDataStore qualityDataStore = TraceQualityDataStoreAdapter.adapt(masterPhdDataStore); 
            ConsensusCaller consensusCaller = createConsensusCaller(RecallType.parse(commandLine.getOptionValue("recall_with")), PhredQuality.valueOf(30));
            IndexedAceFileDataStore aceContigDataStore = new IndexedAceFileDataStore(inputAceFile,new DefaultIndexedFileRange(),false);
            AceFileVisitor headerVisitor = new AbstractAceFileVisitor() {
                
                /**
                * {@inheritDoc}
                */
                @Override
                public synchronized void visitHeader(int numberOfContigs,
                        int totalNumberOfReads) {
                    try {
                        AceFileWriter.writeAceFileHeader(numberOfContigs, totalNumberOfReads, out);
                    } catch (IOException e) {
                        throw new IllegalStateException("error writing to outputfile");
                    }
                }

                @Override
                protected void visitNewContig(String contigId, String consensus) {
                    // no-op
                    
                }
                
                @Override
                protected void visitAceRead(String readId, String validBasecalls,
                        int offset, SequenceDirection dir, Range validRange,
                        PhdInfo phdInfo, int ungappedFullLength) {
                    // no-op
                    
                }
            };
            AceFileVisitor aceVisitors = MultipleWrapper.createMultipleWrapper(AceFileVisitor.class, headerVisitor,aceContigDataStore);
            System.out.println("begin parsing");
            AceFileParser.parseAceFile(inputAceFile, aceVisitors);
            System.out.println("begin for loop");
            for(AceContig contig : aceContigDataStore){
                System.out.println(contig.getId());
                SliceMap sliceMap = CompactedSliceMap.create(contig, qualityDataStore, 
                        GapQualityValueStrategies.LOWEST_FLANKING);
                NucleotideSequence originalConsensus = contig.getConsensus();
                List<NucleotideGlyph> recalledConsensus = new ArrayList<NucleotideGlyph>((int)originalConsensus.getLength());
                for(int i=0; i<originalConsensus.getLength();i++){
                    Slice slice =sliceMap.getSlice(i);
                    ConsensusResult result =consensusCaller.callConsensus(slice);
                  
                    recalledConsensus.add(result.getConsensus());
                }
                final DefaultNucleotideSequence gappedRecalledConsensus = new DefaultNucleotideSequence(recalledConsensus);
                if(fastaOut !=null){
                    fastaOut.print(new DefaultNucleotideSequenceFastaRecord(contig.getId(), gappedRecalledConsensus.decodeUngapped()));
                }
                DefaultAceContig.Builder builder = new DefaultAceContig.Builder(contig.getId(), gappedRecalledConsensus);
                for(AcePlacedRead read : contig.getPlacedReads()){
                    builder.addRead(read);
                }
                AceFileWriter.writeAceContig(builder.build(), masterPhdDataStore, out);
            }
            IOUtil.closeAndIgnoreErrors(aceContigDataStore,fastaOut,masterPhdDataStore,out);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       

    }

    private static ConsensusCaller createConsensusCaller(RecallType recallType, PhredQuality highQualityThreshold){
        switch(recallType){
            case CONIC : return new ConicConsensusCaller(highQualityThreshold);
            case NO_AMBIGUITY : return new NoAmbiguityConsensusCaller(highQualityThreshold);
            default : throw new IllegalArgumentException("could not create consensus caller for type "+ recallType);
        }
    }
    /**
     * @param options
     */
    private static void printHelp(Options options) {
        // TODO Auto-generated method stub
        
    }

}
