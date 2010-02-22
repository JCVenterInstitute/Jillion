/*
 * Created on Feb 22, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.LargeNoQualitySliceMap;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class GenerateSNPMatrix {
    public static Pattern INPUT_PATTERN = Pattern.compile("(\\w+):(\\w+):(\\S+)$");
    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("ace", "input ace file")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("out", "output file")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("id", "contig id")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("pos", "comma separated list of 1-based positions")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("g", "given postions are gapped (Default to ungapped)")
                        .isFlag(true)
                        .build());
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File aceFile = new File(commandLine.getOptionValue("ace"));
            String contigId = commandLine.getOptionValue("id");
            String positions = commandLine.getOptionValue("pos");
            boolean isGapped = commandLine.hasOption("g");
            String outputFilePath = commandLine.getOptionValue("output");
            
            MemoryMappedAceFileDataStore datastore = new MemoryMappedAceFileDataStore(aceFile);
            AceFileParser.parseAceFile(aceFile, datastore);
            AceContig contig = datastore.get(contigId);
            List<Integer> coordinates = new ArrayList<Integer>();
            for(String coordinate : positions.split(",")){
                coordinates.add(Integer.parseInt(coordinate));
            }
            Map<String, Map<Integer, NucleotideGlyph>> snpMap = new HashMap<String, Map<Integer,NucleotideGlyph>>();
            CoverageMap<CoverageRegion<AcePlacedRead>> gappedCoverageMap = DefaultCoverageMap.buildCoverageMap(contig.getPlacedReads());
            NucleotideEncodedGlyphs consensus = contig.getConsensus();
            LargeNoQualitySliceMap sliceMap = new LargeNoQualitySliceMap(gappedCoverageMap);
            
            for(Integer coordinate : coordinates){
                final int gappedOffset;
                if(isGapped){
                    gappedOffset = coordinate-1;
                }
                else{
                    int ungappedOffset = coordinate-1;
                    gappedOffset =consensus.convertUngappedValidRangeIndexToGappedValidRangeIndex(ungappedOffset);
                }
                Slice slice = sliceMap.getSlice(gappedOffset);
                for(SliceElement element : slice.getSliceElements()){
                    String id = element.getName();
                    if(!snpMap.containsKey(id)){
                        snpMap.put(id, new HashMap<Integer, NucleotideGlyph>());
                    }
                    Map<Integer, NucleotideGlyph> readSNPs =snpMap.get(id);
                    readSNPs.put(coordinate, element.getBase());
                }
            }
            PrintWriter writer = new PrintWriter(new FileOutputStream(outputFilePath),true);
            writer.printf("#id\t%s%n", contigId);
            writer.printf("#loc\t%s%n", positions.replaceAll(",", "\t"));
            for(Entry<String, Map<Integer, NucleotideGlyph>> entry : snpMap.entrySet()){
                StringBuilder snps = new StringBuilder();
                Map<Integer, NucleotideGlyph> readSNPs = entry.getValue();
                for(Integer coordinate : coordinates){
                    if(readSNPs.containsKey(coordinate)){
                        snps.append(readSNPs.get(coordinate));
                    }
                    snps.append("\t");
                }
                writer.printf("%s\t%s%n", entry.getKey(),snps.toString());
            }
            writer.close();
        
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
        
       
       
    
       
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "generateSNPMatrix -ace <ace file> -id <contig id> -pos <list of positions> -out <output fasta> [OPTIONS]", 
                
                "create a SNP Matrix for all the reads at the given positions in the given ace file",
                options,
                "Created by Danny Katzel");
    }

}
