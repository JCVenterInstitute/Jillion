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

package org.jcvi.assembly.cas.var;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jcvi.assembly.cas.var.Variation.Type;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.glyph.nuc.NucleotideGlyph;
/**
 * @author dkatzel
 *
 *
 */
public class Compare2VarFiles {

    private static final String DEFAULT_OUTPUT_FILE_SUFFIX = "variationDifferences.xls";
    private static final Pattern FIRST_WORD_OF_CONTIG = Pattern.compile("^(\\S+)");
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("o", 
                            String.format("output excel file (DEFAULT : <file1>_<file2>.%s )",DEFAULT_OUTPUT_FILE_SUFFIX))
        
                            .build());
        options.addOption(CommandLineUtils.createHelpOption());
        if(args.length <2){
            printHelp(options);
            System.exit(1);
        }
        File file1  = new File(args[args.length-2]);
        File file2  = new File(args[args.length-1]);
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, Arrays.copyOf(args, args.length-2));
        
            final File outputFile;
            if(commandLine.hasOption("o")){
                outputFile = new File(commandLine.getOptionValue("o"));
            }else{
                outputFile = new File(String.format("%s.%s.%s",file1.getName(),file2.getName(),DEFAULT_OUTPUT_FILE_SUFFIX));
            }
            
        VariationLog log1 = new DefaultVariationLogFile(file1);
        VariationLog log2 = new DefaultVariationLogFile(file2);
        
        Set<String> log1Ids =log1.getContigIds();
        Set<String> log2Ids =log2.getContigIds();
        HSSFWorkbook workbook = new HSSFWorkbook();
        for(String contigId : log1Ids){
            
            Map<Long, Variation[]> variations = new TreeMap<Long, Variation[]>();
            if(log2Ids.contains(contigId)){                
                Map<Long, Variation> log2VariationMap = log2.getVariationsFor(contigId);
                Map<Long, Variation> log1VariationMap = log1.getVariationsFor(contigId);
                
                for(Entry<Long, Variation> log1VariationEntry: log1.getVariationsFor(contigId).entrySet()){
                    long coordinate = log1VariationEntry.getKey();
                    Variation log1Variation = log1VariationEntry.getValue();
                    if(log2VariationMap.containsKey(coordinate)){                        
                        Variation log2Variation =log2VariationMap.get(coordinate);
                        if(log2Variation.getType() != log1Variation.getType() || log2Variation.getConsensusBase() != log1Variation.getConsensusBase()){
                            variations.put(coordinate, new Variation[]{log1Variation, log2Variation});
                        }
                    }else if(log1Variation.getType() == Type.DIFFERENCE){
                        Variation noChangeVariation = new DefaultVariation.Builder(
                                log1Variation.getCoordinate(),
                                Type.NO_CHANGE,
                                log1Variation.getReferenceBase(), 
                                Arrays.asList(log1Variation.getReferenceBase()))
                                    .build();
                        variations.put(coordinate, new Variation[]{log1Variation, noChangeVariation});
                    }
                }
                for(Entry<Long, Variation> log2VariationEntry: log2.getVariationsFor(contigId).entrySet()){
                    long coordinate = log2VariationEntry.getKey();
                    Variation log2Variation = log2VariationEntry.getValue();
                    if(log1VariationMap.containsKey(coordinate)){                        
                        Variation log1Variation =log1VariationMap.get(coordinate);
                        if(log2Variation.getType() != log1Variation.getType() || log2Variation.getConsensusBase() != log1Variation.getConsensusBase()){
                            variations.put(coordinate, new Variation[]{log1Variation, log2Variation});
                        }
                    }else if(log2Variation.getType() == Type.DIFFERENCE){
                        Variation noChangeVariation = new DefaultVariation.Builder(
                                log2Variation.getCoordinate(),
                                Type.NO_CHANGE,
                                log2Variation.getReferenceBase(), 
                                Arrays.asList(log2Variation.getReferenceBase()))
                                    .build();
                        variations.put(coordinate, new Variation[]{noChangeVariation, log2Variation});
                    }
                }
                if(!variations.isEmpty()){
                    Matcher matcher = FIRST_WORD_OF_CONTIG.matcher(contigId);
                    if(!matcher.find()){
                        throw new IllegalStateException("could not turn "+contigId + " into a worksheet name");
                    }
                    HSSFSheet sheet =workbook.createSheet(matcher.group(1));
                    HSSFRow header= sheet.createRow(0);
                    header.createCell(0).setCellValue(new HSSFRichTextString("coordinate"));
                    header.createCell(1).setCellValue(new HSSFRichTextString("file1 type"));
                    header.createCell(2).setCellValue(new HSSFRichTextString("file1 reference -> consensus"));
                    header.createCell(3).setCellValue(new HSSFRichTextString("file1 histogram counts"));
                    
                    header.createCell(4).setCellValue(new HSSFRichTextString("file2 type"));
                    header.createCell(5).setCellValue(new HSSFRichTextString("file2 reference -> consensus"));
                    header.createCell(6).setCellValue(new HSSFRichTextString("file2 histogram counts"));
                    
                    int rowCount=1;
                    for(Entry<Long, Variation[]> entry : variations.entrySet()){
                        Variation[] array = entry.getValue();
                        HSSFRow row =sheet.createRow(rowCount);
                        final Variation variation1 = array[0];
                        final Variation variation2 = array[1];
                        row.createCell(0).setCellValue(variation1.getCoordinate());
                        row.createCell(1).setCellValue(new HSSFRichTextString(variation1.getType().toString()));
                        row.createCell(2).setCellValue(new HSSFRichTextString(
                                String.format("%s -> %s",
                                        variation1.getReferenceBase().toString(),
                                        variation1.getConsensusBase().toString())));
                        row.createCell(3).setCellValue(new HSSFRichTextString(createHistogramCountsFor(variation1.getHistogram())));

                        row.createCell(4).setCellValue(new HSSFRichTextString(variation2.getType().toString()));
                        row.createCell(5).setCellValue(new HSSFRichTextString(
                                String.format("%s -> %s",
                                        variation2.getReferenceBase().toString(),
                                        variation2.getConsensusBase().toString())));
                        row.createCell(6).setCellValue(new HSSFRichTextString(createHistogramCountsFor(variation2.getHistogram())));
                       
                        rowCount++;
                    }
                }
            }
        }
        FileOutputStream out = new FileOutputStream(outputFile);
        workbook.write(out);
        IOUtils.closeQuietly(out);
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
    }

    private static String createHistogramCountsFor(Map<List<NucleotideGlyph>, Integer> histogram){
        StringBuilder variationList = new StringBuilder();
        SortedSet<List<NucleotideGlyph>> keys = new TreeSet<List<NucleotideGlyph>>(
                new Comparator<List<NucleotideGlyph>>() {

                    @Override
                    public int compare(List<NucleotideGlyph> o1,
                            List<NucleotideGlyph> o2) {
                        return NucleotideGlyph.convertToString(o1).compareTo(NucleotideGlyph.convertToString(o2));
                    }
                    
                }
                );
        keys.addAll(histogram.keySet());
        for(Entry<List<NucleotideGlyph>, Integer> entry : histogram.entrySet()){
            variationList.append(String.format(" %s: %d", NucleotideGlyph.convertToString(entry.getKey()), entry.getValue()));
        }
        
        return variationList.toString();
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "compare2VarFiles [OPTIONS] <var file 1> <var file 2>", 
                
                "Compare 2 CLC variation log files",
                options,
                "Created by Danny Katzel");
    }
}
