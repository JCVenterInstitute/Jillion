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
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.assembly.cas.var.Variation.Type;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class VariationLogFileParser {

    private static final Pattern CONTIG_PATTERN = Pattern.compile("^(\\S+).*:\\s*$");
    private static final Pattern VARIATION_PATTERN = Pattern.compile("^\\s+(\\d+)\\s+(\\w+)\\s+(\\S)\\s+->\\s+(\\S+)(.+)$");
    
    private static final String CR = "\n";
    public static void parseVariationFile(File variationLogFile, VariationLogFileVisitor visitor) throws FileNotFoundException{
        Scanner scanner = new Scanner(variationLogFile).useDelimiter(CR);
        try{
            visitor.visitFile();
            boolean readVariationsForCurrentContig=true;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                visitor.visitLine(line +CR);
                Matcher contigMatcher = CONTIG_PATTERN.matcher(line);
                
                if(contigMatcher.find()){
                    String contigId = contigMatcher.group(1);
                    System.out.println("contig id = "+ contigId);
                    readVariationsForCurrentContig =visitor.visitContig(contigId);
                }else if(readVariationsForCurrentContig){
                    Matcher varMatcher = VARIATION_PATTERN.matcher(line);
                    if(varMatcher.find()){
                        long coordinate = Long.parseLong(varMatcher.group(1));
                        Type type = Type.getType(varMatcher.group(2));
                        NucleotideGlyph ref = NucleotideGlyph.getGlyphFor(varMatcher.group(3));
                        List<NucleotideGlyph> consensus = NucleotideGlyph.getGlyphsFor(varMatcher.group(4));
                        DefaultVariation.Builder variationBuilder = new DefaultVariation.Builder(coordinate, type,ref,consensus);
                        final String group = varMatcher.group(5);
                        Scanner histogramScanner = new Scanner(group);
                        if(group.startsWith("AT   AAT: 39  -: 3")){
                            System.out.println("here");
                        }
                        while(histogramScanner.hasNext()){
                            List<NucleotideGlyph> bases = NucleotideGlyph.getGlyphsFor(histogramScanner.next().replaceAll(":",""));
                            int count = histogramScanner.nextInt();
                            variationBuilder.addHistogramRecord(bases,count);
                        }
                        visitor.visitVariation(variationBuilder.build());
                    }
                }
            }
            visitor.visitEndOfFile();
        }finally{
            scanner.close();
        }
    }
}
