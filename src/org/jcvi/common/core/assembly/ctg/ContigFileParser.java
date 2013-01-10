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
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ctg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.internal.io.TextLineParser;

public final class ContigFileParser  {
    private static final Pattern NEW_CONTIG_PATTERN = Pattern.compile("##(\\S+).+");
    private static final Pattern NEW_READ_PATTERN = Pattern.compile("#(\\S+)\\((-?\\d+)\\)\\s+\\[(.*)\\].+\\{(-?\\d+) (-?\\d+)\\}.+");
    /**
     * Parse the given contig file and call the 
     * appropriate visitXXX methods on the given {@link ContigFileVisitor}.
     * @param contigFile {@link File} containing contig data.
     * @param visitor the ContigFileVistor to visit when parsing.
     * @throws FileNotFoundException if the contigFile does not exist.
     * @throws NullPointerException if visitor is {@code null}.
     */
    public static void parse(File contigFile, ContigFileVisitor visitor) throws FileNotFoundException{
        InputStream in = new FileInputStream(contigFile);
        try{
            parse(in,visitor);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given {@link InputStream} containing contig file data
     * and call the appropriate visitXXX methods on the given {@link ContigFileVisitor}.
     * @param inputStream the {@link InputStream} of the contig file.
     * @param visitor the ContigFileVistor to visit when parsing.
     * @throws NullPointerException if either parameter are {@code null}.
     */
    public static void parse(InputStream inputStream, ContigFileVisitor visitor){
        if(inputStream ==null){
            throw new NullPointerException("inputStream can not be null");
        }
        if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }

    	TextLineParser parser;
		try {
			parser = new TextLineParser(new BufferedInputStream(inputStream));
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new IllegalStateException("error reading file");
			
		}
       
        visitor.visitFile();
        boolean inConsensus =true;
        while(parser.hasNextLine()){
           String lineWithCR;
		try {
			lineWithCR = parser.nextLine();
		} catch (IOException e) {
			throw new IllegalStateException("error parsing contig",e);
		}
           visitor.visitLine(lineWithCR);
           String line = lineWithCR.endsWith("\n")? lineWithCR.substring(0,lineWithCR.length()-1) : lineWithCR;
           Matcher newContigMatcher =NEW_CONTIG_PATTERN.matcher(line);
           if(newContigMatcher.find()){
               inConsensus=true;
               handleNewContig(newContigMatcher, visitor);
           }
           else{
               Matcher newSequenceMatcher =NEW_READ_PATTERN.matcher(line);
               if(newSequenceMatcher.find()){
                   inConsensus=false;
                   fireVisitNewRead(newSequenceMatcher, visitor);
               }
               else{
                   if(inConsensus){
                       visitor.visitConsensusBasecallsLine(line);
                   }else{
                       visitor.visitReadBasecallsLine(line);
                   }
               }
           }
        }
        visitor.visitEndOfFile();
   }

 
    private static void handleNewContig(Matcher newContigMatcher,
            ContigFileVisitor visitor) {
        final String contigId = parseContigId(newContigMatcher);
           visitor.visitNewContig(contigId);
    }

    private static String parseContigId(Matcher newContigMatcher) {
        return newContigMatcher.group(1);
    }

    private static void fireVisitNewRead(Matcher newSequenceMatcher,  ContigFileVisitor visitor) {
           String seqId = newSequenceMatcher.group(1);
           int offset = Integer.parseInt(newSequenceMatcher.group(2));
           Direction dir= parseComplimentedFlag(newSequenceMatcher)?Direction.REVERSE: Direction.FORWARD;
           Range validRange = parseValidRange(newSequenceMatcher, dir);
           
           visitor.visitNewRead(seqId, offset, validRange, dir);
    }

    private static boolean parseComplimentedFlag(Matcher newSequenceMatcher) {
        return !newSequenceMatcher.group(3).isEmpty();
    }

    private static Range parseValidRange(Matcher newSequenceMatcher,
            Direction dir) {
            int left = Integer.parseInt(newSequenceMatcher.group(4));
           int right = Integer.parseInt(newSequenceMatcher.group(5));
           Range validRange;
           if(dir == Direction.REVERSE){
               validRange = Range.of(CoordinateSystem.RESIDUE_BASED,right, left);
           }
           else{
               validRange = Range.of(CoordinateSystem.RESIDUE_BASED,left, right);
           }
        return validRange;
    }
    
    private ContigFileParser(){}
    

}
