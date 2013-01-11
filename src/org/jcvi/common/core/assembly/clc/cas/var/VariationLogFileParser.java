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

package org.jcvi.common.core.assembly.clc.cas.var;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.assembly.clc.cas.var.Variation.Type;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * {@code VariationLogFileParser} is a parser for 
 * CLC variations log file that is produced by the 
 * {@code find_variations} program.
 * @author dkatzel
 *
 *
 */
public final class VariationLogFileParser {

    private static final Pattern CONTIG_PATTERN = Pattern.compile("^(\\S+).*:\\s*$");
    private static final Pattern VARIATION_PATTERN = Pattern.compile("^\\s+(\\d+)\\s+(\\w+)\\s+(\\S)\\s+->\\s+(\\S+)(.+)$");
    
    private static final String CR = "\n";
    
    private VariationLogFileParser(){
    	//private constructor.
    }
    /**
     * Parse the given CLC varition file and call the appropriate
     * visitXXX methods on the given {@link VariationLogFileVisitor}.
     * @param variationLogFile the variation log vile to parse; can not be null
     * and must exist.
     * @param visitor the {@link VariationLogFileVisitor} implementation
     * to call visitXXX methods on; can not be null.
     * @throws IOException if there is a problem parsing the file or if the 
     * file does not exist.
     */
    public static void parse(File variationLogFile, VariationLogFileVisitor visitor) throws IOException{
        Scanner scanner = new Scanner(variationLogFile, "UTF-8").useDelimiter(CR);
        try{
            visitor.visitFile();
            boolean readVariationsForCurrentContig=true;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                visitor.visitLine(line +CR);
                Matcher contigMatcher = CONTIG_PATTERN.matcher(line);
                
                if(contigMatcher.find()){
                    String contigId = contigMatcher.group(1);
                    readVariationsForCurrentContig =visitor.visitReference(contigId);
                }else if(readVariationsForCurrentContig){
                    Matcher varMatcher = VARIATION_PATTERN.matcher(line);
                    if(varMatcher.find()){
                        long coordinate = Long.parseLong(varMatcher.group(1));
                        Type type = Type.getType(varMatcher.group(2));
                        Nucleotide ref = Nucleotide.parse(varMatcher.group(3));
                        DefaultVariation.Builder variationBuilder = new DefaultVariation.Builder(coordinate, type,ref,varMatcher.group(4));
                        final String group = varMatcher.group(5);
                        Scanner histogramScanner = new Scanner(group);
                        while(histogramScanner.hasNext()){
                            String bases = histogramScanner.next().replaceAll(":","");
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
