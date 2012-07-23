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

package org.jcvi.common.core.align;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.align.AlnVisitor.ConservationInfo;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;

/**
 * {@code AlnParser} is a utility class that can 
 * parse .aln alignment files like those
 * created by clustal.
 * 
 * @author dkatzel
 *
 *
 */
public final class AlnParser {

	private AlnParser(){
		//private constructor.
	}
    /**
     * 
     */
    private static final String REGEX = "^([^*\\s]+)\\s+([\\-ACGTNVHDBWMRSYK]+)";
    private static final Pattern ALIGNMENT_PATTERN = Pattern.compile(REGEX);
    private static final Pattern CONSERVATION_PATTERN = Pattern.compile("\\s+([-:\\. \\*]+)$");
    
    /**
     * Parse the given aln file and call the appropriate
     * visitXXX method call backs on the given {@link AlnVisitor}.
     * @param alnFile the aln file to parse; this file
     * can not be null and must exist. 
     * @param visitor an instance of {@link AlnVisitor};
     * can not be null.
     * @throws IOException if there is a problem
     * parsing the .aln file.
     * @throws NullPointerException if any input parameters are null.
     */
    public static void parse(File alnFile, AlnVisitor visitor) throws IOException{
        InputStream in = null;
        in = new FileInputStream(alnFile);
        try{
            parse(in, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given {@link InputStream} as an aln file and call the appropriate
     * visitXXX method call backs on the given {@link AlnVisitor}.Please note
     * that this method might leave the inputstream open and so it 
     * is the client's responsibility to close the stream when
     * done parsing.
     * @param alnStream an {@link InputStream} containing
     *  aln formatted data to be parsed; can not be null.
     * @param visitor an instance of {@link AlnVisitor};
     * can not be null.
     * @throws IOException if there is a problem
     * parsing the .aln data.
     * @throws NullPointerException if any input parameters are null.
     */
    public static void parse(InputStream alnStream, AlnVisitor visitor) throws IOException{
        TextLineParser parser = new TextLineParser(alnStream);
        boolean inGroup=false;
        visitor.visitFile();
        int numberOfBasesPerGroup=0;
        while(parser.hasNextLine()){
            String line = parser.nextLine();
            visitor.visitLine(line);
            Matcher alignmentMatcher =  ALIGNMENT_PATTERN.matcher(line);
            if(alignmentMatcher.find()){
                if(!inGroup){
                    visitor.visitBeginGroup();
                    inGroup=true;
                }
                String basecalls = alignmentMatcher.group(2);
                numberOfBasesPerGroup = basecalls.length();
                visitor.visitAlignedSegment(alignmentMatcher.group(1), basecalls);
            }else{
                Matcher conservationMatcher = CONSERVATION_PATTERN.matcher(line);
                if(conservationMatcher.find()){
                    String conservationString = conservationMatcher.group(1);
                    List<ConservationInfo> info = parseConservationInfo(conservationString,numberOfBasesPerGroup);
                    visitor.visitConservationInfo(info);
                    inGroup=false;
                    visitor.visitEndGroup();
                }                
            }
        }
        visitor.visitEndOfFile();
    }
    
    /**
     * @param conservationString
     * @param numberOfBasesPerGroup
     * @return
     */
    private static List<ConservationInfo> parseConservationInfo(
            String conservationString, int numberOfBasesPerGroup) {
        final String paddedString = createPaddedConservationString(conservationString, numberOfBasesPerGroup);
        List<ConservationInfo> result = new ArrayList<AlnVisitor.ConservationInfo>(numberOfBasesPerGroup);
        for(int i=0; i< paddedString.length(); i++){
            switch(paddedString.charAt(i)){
                case '*' :  result.add(ConservationInfo.IDENTICAL);
                            break;
                case ':' :  result.add(ConservationInfo.CONSERVED_SUBSITUTION);
                            break;
                case '.' :  result.add(ConservationInfo.SEMI_CONSERVED_SUBSITUTION);
                            break;
                default:    result.add(ConservationInfo.NOT_CONSERVED);
                            break;
            }
        }
        return result;
    }
    /**
     * Aln format uses spaces to denote not conserved regions,
     * this is hard to parse out using regular expressions if 
     * the conservation string is supposed to START with spaces.
     * By using the expected number of basecalls in this group,
     * we can create a padded string with the correct number of leading
     * spaces.
     * @param conservationString
     * @param numberOfBasesPerGroup
     * @return
     */
    private static String createPaddedConservationString(
            String conservationString, int numberOfBasesPerGroup) {
        int length = conservationString.length();
        int padding = numberOfBasesPerGroup-length;
        
        final String paddedString;
        if(padding>0){
            String format = "%"+padding+"s%s";
            paddedString= String.format(format, "",conservationString);
        }else{
            paddedString = conservationString;
        }
        return paddedString;
    }
    
}
