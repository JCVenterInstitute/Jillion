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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.ace.consed.PhdDirQualityDataStore;
import org.jcvi.assembly.contig.qual.GapQualityValueStrategies;
import org.jcvi.assembly.slice.DefaultSliceMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.assembly.slice.consensus.ConsensusCaller;
import org.jcvi.assembly.slice.consensus.ConsensusResult;
import org.jcvi.assembly.slice.consensus.NoAmbiguityConsensusCaller;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DefaultAceFileDataStore;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.fastX.fasta.seq.DefaultNucleotideEncodedSequenceFastaRecord;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.trace.TraceQualityDataStoreAdapter;

/**
 * @author dkatzel
 *
 *
 */
public class RecallAceConsensus {

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
            File phdDir = new File(inputAceFile.getParentFile().getParentFile(),"phd_dir");
            PrintWriter fastaOut = new PrintWriter(new File(commandLine.getOptionValue("fasta")));
            
            File outputAceFile = new File(commandLine.getOptionValue("out"));
            
            PhdDirQualityDataStore phdDataStore = new PhdDirQualityDataStore(phdDir);
            QualityDataStore qualityDataStore = TraceQualityDataStoreAdapter.adapt(phdDataStore); 
            ConsensusCaller consensusCaller = new NoAmbiguityConsensusCaller(PhredQuality.valueOf(30));
            AceContigDataStore aceContigDataStore = new DefaultAceFileDataStore(inputAceFile);
            Map<String, AceContig> recalledContigs = new LinkedHashMap<String, AceContig>();
            for(AceContig contig : aceContigDataStore){
                SliceMap sliceMap = DefaultSliceMap.create(contig, qualityDataStore, GapQualityValueStrategies.LOWEST_FLANKING);
                NucleotideEncodedGlyphs originalConsensus = contig.getConsensus();
                List<NucleotideGlyph> consensusList = originalConsensus.decode();
                List<NucleotideGlyph> recalledConsensus = new ArrayList<NucleotideGlyph>(consensusList.size());
                for(int i=0; i<consensusList.size();i++){
                    Slice slice =sliceMap.getSlice(i);
                    ConsensusResult result =consensusCaller.callConsensus(slice);
                    if(result.getConsensus() != consensusList.get(i)){
                        int nonGapOffset =AssemblyUtil.getLeftFlankingNonGapIndex(originalConsensus, i);
                        int ungappedResidueBased = originalConsensus.convertGappedValidRangeIndexToUngappedValidRangeIndex(nonGapOffset)+1;
                        System.out.printf("%d\t %s -> %s%n",ungappedResidueBased,consensusList.get(i),result.getConsensus());
                    }
                    recalledConsensus.add(result.getConsensus());
                }
                final DefaultNucleotideEncodedGlyphs gappedRecalledConsensus = new DefaultNucleotideEncodedGlyphs(recalledConsensus);
                
                fastaOut.print(new DefaultNucleotideEncodedSequenceFastaRecord(contig.getId(), gappedRecalledConsensus.decodeUngapped()));
                DefaultAceContig.Builder builder = new DefaultAceContig.Builder(contig.getId(), gappedRecalledConsensus);
                for(AcePlacedRead read : contig.getPlacedReads()){
                    builder.addRead(read);
                }
                recalledContigs.put(contig.getId(), builder.build());
            }
            AceAssembly<AceContig> assembly = new DefaultAceAssembly<AceContig>(new SimpleDataStore<AceContig>(recalledContigs), phdDataStore);
            OutputStream out = new FileOutputStream(outputAceFile);
            AceFileWriter.writeAceFile(assembly, out);
            aceContigDataStore.close();
            fastaOut.close();
            out.close();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       

    }

    /**
     * @param options
     */
    private static void printHelp(Options options) {
        // TODO Auto-generated method stub
        
    }

}
