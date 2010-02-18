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
import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.datastore.MemoryMappedContigFileDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.util.DefaultMemoryMappedFileRange;

public class ConvertFastaPosition {

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
        options.addOption(new CommandLineOptionBuilder("r", "input range(s)")
                            .longName("ranges")
                            .isRequired(true)
                            .build());
        OptionGroup gapGroup = new OptionGroup();
        
        gapGroup.addOption(new CommandLineOptionBuilder("g", "coordinate is gapped (DEFAULT)")
                            .longName("gapped")
                            .build());
        gapGroup.addOption(new CommandLineOptionBuilder("u", "coordinate is ungapped")
                                .longName("ungapped")
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
        options.addOption(CommandLineUtils.createHelpOption());
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
                
            }
            String id = commandLine.getOptionValue("id");
            final NucleotideEncodedGlyphs values;
            if(commandLine.hasOption("f")){
                File fastaFile = new File(commandLine.getOptionValue("f"));
                LargeNucleotideFastaFileDataStore datastore = new LargeNucleotideFastaFileDataStore(fastaFile);
                values= datastore.get(id).getValues();
            }else if(commandLine.hasOption("c")){
                File contigFile = new File(commandLine.getOptionValue("c"));
                MemoryMappedContigFileDataStore datastore = new MemoryMappedContigFileDataStore(contigFile);
                values= datastore.get(id).getConsensus();
            }else{
                File aceFile = new File(commandLine.getOptionValue("a"));
                MemoryMappedAceFileDataStore datastore = new MemoryMappedAceFileDataStore(aceFile, new DefaultMemoryMappedFileRange());
                AceFileParser.parseAceFile(aceFile, datastore);
                values = datastore.get(id).getConsensus();
            }
            
            
            final Range coordinateRange;
            boolean isGapped = !commandLine.hasOption("u");
            boolean isOneBased = commandLine.hasOption("o");
            boolean isSpacedBased = commandLine.hasOption("s");
          
            if(isOneBased){
                coordinateRange = Range.parseRange(commandLine.getOptionValue("r"),CoordinateSystem.RESIDUE_BASED);
            }
            else if(isSpacedBased){
                coordinateRange = Range.parseRange(commandLine.getOptionValue("r"),CoordinateSystem.SPACE_BASED);
            }else{
                coordinateRange = Range.parseRange(commandLine.getOptionValue("r"),CoordinateSystem.ZERO_BASED);
            }
            int delta = (int)(coordinateRange.getLocalStart() - coordinateRange.getStart());
            for(long coordinate = coordinateRange.getLocalStart(); coordinate <=coordinateRange.getLocalEnd(); coordinate ++){

                final int convertedCoordinate;
                if(isGapped){
                    convertedCoordinate =values.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)coordinate+delta);
                }else{
                    convertedCoordinate = values.convertUngappedValidRangeIndexToGappedValidRangeIndex((int)coordinate+delta);
                }
                System.out.printf("%d\t%d%n", coordinate,convertedCoordinate);
            }
        }catch(ParseException e){
            printHelp(options);
            System.exit(1);
        }
        

    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "convertFastaPosition -f <fasta file> -i <id of fasta record in file> -c <coordinate ranges> [OPTIONS]", 
                
                "convert ranges of fasta coordinates from gapped to ungapped or vice versa",
                options,
                "Created by Danny Katzel");
        
    }

}
