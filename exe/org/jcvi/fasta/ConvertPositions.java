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
/*
 * Created on Feb 16, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.contig.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.contig.ctg.IndexedContigFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.nuc.LargeNucleotideFastaFileDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class ConvertPositions {

    /**
     * @param args
     * @throws DataStoreException 
     * @throws IOException 
     */
    public static void main(String[] args) throws DataStoreException, IOException {
        Options options= new Options();
        OptionGroup sourceGroup = new OptionGroup();
        sourceGroup.setRequired(true);
        
        sourceGroup.addOption(new CommandLineOptionBuilder("f", "path to fasta file")
                            .longName("fasta")
                            .build());
        sourceGroup.addOption(new CommandLineOptionBuilder("a", "path to ace file")
                            .longName("ace")
                            .build());
        sourceGroup.addOption(new CommandLineOptionBuilder("c", "path to contig file")
                        .longName("contig")
                        .build());
        options.addOptionGroup(sourceGroup);
        
        options.addOption(new CommandLineOptionBuilder("i", "id of fasta record to examine")
                            .longName("id")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("r", "input range(s) comma separated list of ranges to convert accept format x-y to mean the range [x,y] inclusive")
                            .longName("ranges")
                            .isRequired(true)
                            .build());
        OptionGroup gapGroup = new OptionGroup();
        
        gapGroup.addOption(new CommandLineOptionBuilder("g", "coordinate is gapped (DEFAULT)")
                            .longName("gapped")
                            .isFlag(true)
                            .build());
        gapGroup.addOption(new CommandLineOptionBuilder("u", "coordinate is ungapped")
                                .longName("ungapped")
                                .isFlag(true)
                                .build());
        
        options.addOptionGroup(gapGroup);
        
        OptionGroup coordinateSystemGroup = new OptionGroup();
        coordinateSystemGroup.addOption(
                new CommandLineOptionBuilder("z", "coordinate system is ZERO based (DEFAULT)")
                            .longName("zeroBased")
                            .build());
        coordinateSystemGroup.addOption(
                new CommandLineOptionBuilder("o", "coordinate system is ONE based")
                            .longName("oneBased")
                            .build());
        coordinateSystemGroup.addOption(
                new CommandLineOptionBuilder("s", "coordinate system is SPACED based")
                            .longName("oneBased")
                            .build());
        
        options.addOptionGroup(coordinateSystemGroup);
        options.addOption(CommandLineUtils.createHelpOption());
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
                
            }
            String id = commandLine.getOptionValue("id");
            final NucleotideSequence values;
            if(commandLine.hasOption("f")){
                File fastaFile = new File(commandLine.getOptionValue("f"));
                LargeNucleotideFastaFileDataStore datastore = new LargeNucleotideFastaFileDataStore(fastaFile);
                values= datastore.get(id).getSequence();
            }else if(commandLine.hasOption("c")){
                File contigFile = new File(commandLine.getOptionValue("c"));
                IndexedContigFileDataStore datastore = new IndexedContigFileDataStore(contigFile);
                values= datastore.get(id).getConsensus();
            }else{
                File aceFile = new File(commandLine.getOptionValue("a"));
                AceContigDataStore datastore =IndexedAceFileDataStore.create(aceFile);
                values = datastore.get(id).getConsensus();
            }
            
            
           
            boolean isGapped = !commandLine.hasOption("u");
            boolean isOneBased = commandLine.hasOption("o");
            boolean isSpacedBased = commandLine.hasOption("s");
            for(String range : commandLine.getOptionValue("r").split(",")){
                final Range coordinateRange;
                if(isOneBased){
                    coordinateRange = Range.parseRange(range,CoordinateSystem.RESIDUE_BASED);
                }
                else if(isSpacedBased){
                    coordinateRange = Range.parseRange(range,CoordinateSystem.SPACE_BASED);
                }else{
                    coordinateRange = Range.parseRange(range,CoordinateSystem.ZERO_BASED);
                }
                int delta = (int)(coordinateRange.getLocalStart() - coordinateRange.getStart());
                for(long coordinate = coordinateRange.getLocalStart(); coordinate <=coordinateRange.getLocalEnd(); coordinate ++){
    
                    final int convertedCoordinate;
                    int deltaCoordinate = (int)coordinate+delta;
                    if(isGapped){
                        deltaCoordinate = AssemblyUtil.getLeftFlankingNonGapIndex(values, deltaCoordinate);
                        convertedCoordinate =values.getUngappedOffsetFor(deltaCoordinate);
                    }else{
                        convertedCoordinate = values.getGappedOffsetFor(deltaCoordinate);
                    }
                    System.out.printf("%d\t%d%n", coordinate,convertedCoordinate+1);
                }
            }
        }catch(ParseException e){
            printHelp(options);
            System.exit(1);
        }
        

    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "convertPositions -i <id of record in file> -r <coordinate ranges> [OPTIONS]", 
                
                "convert ranges of fasta or consensus coordinates from gapped to ungapped or vice versa. "+
                "Either way, the coordinates are converted into 1-based positions.",
                options,
                "Created by Danny Katzel");
        
    }

}
