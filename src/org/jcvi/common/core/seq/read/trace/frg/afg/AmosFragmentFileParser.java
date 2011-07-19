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

package org.jcvi.common.core.seq.read.trace.frg.afg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.common.core.seq.read.trace.frg.FragmentUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
public final class AmosFragmentFileParser {
    private static final String BEGIN_READ = "{RED";
    
    private static Pattern INDEX_PATTERN = Pattern.compile("iid:(\\d+)");
    private static Pattern ID_PATTERN = Pattern.compile("eid:(\\S+)");
    private static final Pattern VECTOR_CLEAR_RANGE_PATTERN = Pattern.compile("vcr:(\\d+,\\d+)");
    private static final Pattern QUALITY_CLEAR_RANGE_PATTERN = Pattern.compile("qcr:(\\d+,\\d+)");
    
    public static void parse(File afgFile, AmosFragmentVisitor visitor) throws FileNotFoundException{
        FileInputStream in = new FileInputStream(afgFile);
        try{
            parse(in, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    
    public static void parse(InputStream in, AmosFragmentVisitor visitor){
        Scanner scanner = new Scanner(in).useDelimiter( FragmentUtil.CR);
        try{
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                visitor.visitLine(line+FragmentUtil.CR);
                if(!line.startsWith("#")){
                    String block =  FragmentUtil.readRestOfBlock(scanner , visitor);               
                    if(line.startsWith(BEGIN_READ)){
                        visitRead(block, visitor);
                    }
                    
                }
            }
        }finally{
            visitor.visitEndOfFile();
        }
        
    }
    /**
     * @param block
     * @param visitor
     */
    private static void visitRead(String block, AmosFragmentVisitor visitor) {
        int index = parseIndexFrom(block);
        String id = parseIdFrom(block);
        if(visitor.visitRead(index, id)){
            NucleotideSequence bases = FragmentUtil.parseBasesFrom(block);
            visitor.visitBasecalls(bases);
            List<PhredQuality> qualities = FragmentUtil.parseEncodedQualitiesFrom(block);
            visitor.visitQualities(qualities);
            Range validRange =  FragmentUtil.parseValidRangeFrom(block);
            Range vectorClearRange = parseVectorClearRangeFrom(block);
            Range qualityClearRange = parseQualityClearRangeFrom(block);
            visitor.visitClearRange(validRange);
            visitor.visitVectorRange(vectorClearRange);
            visitor.visitQualityRange(qualityClearRange);
        }
        
        
    }
    private static Range parseVectorClearRangeFrom(String frg) {
        Matcher matcher =VECTOR_CLEAR_RANGE_PATTERN.matcher(frg);
        return FragmentUtil.parseRangeFrom(matcher);
    }
    private static Range parseQualityClearRangeFrom(String frg) {
        Matcher matcher =QUALITY_CLEAR_RANGE_PATTERN.matcher(frg);
        return FragmentUtil.parseRangeFrom(matcher);
    }
    private static int parseIndexFrom(String frg) {
        Matcher indexMatcher = INDEX_PATTERN.matcher(frg);
        if(!indexMatcher.find()){
            throw new IllegalStateException("could not parse the index from "+ frg);
        }
        return Integer.parseInt(indexMatcher.group(1));
       
    }
    
    private static String parseIdFrom(String frg) {
        
        Matcher matcher = ID_PATTERN.matcher(frg);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }
}
