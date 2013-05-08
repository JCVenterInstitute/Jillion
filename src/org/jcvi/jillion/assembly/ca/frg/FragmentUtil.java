/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ca.frg;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.TextFileVisitor;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public final class FragmentUtil {
    public static final String CR = "\n";
    private static final Pattern FRG_BASES_PATTERN = Pattern.compile("seq:\\s+");
    private static final Pattern FRG_QUALITY_PATTERN = Pattern.compile("qlt:\\s+");
    private static final Pattern FRG_VALID_RANGE_PATTERN = Pattern.compile("clr:(\\d+,\\d+)");
    
    /** ASCII code for zero. */
    public static final int ENCODING_ORIGIN = 0x30;
    
    private FragmentUtil(){
    	//can not instantiate
    }
    public static String readRestOfBlock(Scanner scanner,TextFileVisitor visitor){
        StringBuilder sb  = new StringBuilder();
        boolean done = false;
        while (scanner.hasNextLine() && !done){
            String line = scanner.nextLine();
            visitor.visitLine(line+CR);
            sb.append(line).append(CR);
            if("}".equals(line)){
                done =true;
            }
        }
        return sb.toString();
    }
    
    public static  QualitySequence parseEncodedQualitySequence(String frg){
    	 Scanner scanner = new Scanner(frg);
         scanner.findWithinHorizon(FRG_QUALITY_PATTERN, 0);
         StringBuilder encodedQualities = new StringBuilder();
         while(scanner.hasNextLine()){
             String line = scanner.nextLine();
             if(endOfMultilineField(line)){
                 break;
             }
             encodedQualities.append(line);
         }
         scanner.close();
    	QualitySequenceBuilder builder = new QualitySequenceBuilder(encodedQualities.length());
    	for(int i=0; i< encodedQualities.length(); i++){
    		builder.append(encodedQualities.charAt(i) - ENCODING_ORIGIN);
    	}
    	return builder.build();
    }
   
    public static  boolean endOfMultilineField(String line) {
        return line.contains(".");
    }
    
    public static  Range parseValidRangeFrom(String frg) {
        Matcher matcher =FRG_VALID_RANGE_PATTERN.matcher(frg);
        return parseRangeFrom(matcher);
    }
    
    public static  Range parseRangeFrom(Matcher m){
        if(m.find()){
            Range celeraClearRange= Range.parseRange(m.group(1));
            return Range.of(celeraClearRange.getBegin(), celeraClearRange.getEnd()-1);
        }
        return null;
    }
    
    public static  NucleotideSequence parseBasesFrom(String frg) {
        Scanner scanner = new Scanner(frg);
        scanner.findWithinHorizon(FRG_BASES_PATTERN, 0);
        StringBuilder bases = new StringBuilder();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(endOfMultilineField(line)){
                break;
            }
            bases.append(line);
        }
       return new NucleotideSequenceBuilder(bases.toString()).build();
    }
    
    
}
