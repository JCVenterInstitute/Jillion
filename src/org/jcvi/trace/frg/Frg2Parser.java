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
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.DefaultQualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Distance;
import org.jcvi.sequence.MateOrientation;

public class Frg2Parser {
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;

    private static final Pattern ACC_ID_PATTERN = Pattern.compile("acc:(\\S+)");
    private static final Pattern LKG_FRG_ID_PATTERN = Pattern.compile("frg:(\\d+)");
    private static final Pattern ACTION_PATTERN = Pattern.compile("act:([A|M|I|D])\\s+");
    
    private static final Pattern LIB_ORIENTATION_PATTERN = Pattern.compile("ori:\\s*?(\\w)");
    private static final Pattern LIB_DST_PATTERN = Pattern.compile("mea:\\s*?(\\S+)\\s*?.*?std:\\s*?(\\S+)");
    
    private static final Pattern SOURCE_PATTERN = Pattern.compile("src:\\s+");
   private static final Pattern FRG_VECTOR_CLEAR_RANGE_PATTERN = Pattern.compile("clv:(\\d+,\\d+)");
    private static final Pattern FRG_LIB_PATTERN = Pattern.compile("lib:(\\S+)");

    private static final String BEGIN_LIB = "{LIB";
    private static final String BEGIN_FRG = "{FRG";
    private static final String BEGIN_LKG = "{LKG";
    
    public void parse(InputStream in, Frg2Visitor visitor){
        
        Scanner scanner = new Scanner(in).useDelimiter( FragmentUtil.CR);
        try{
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                visitor.visitLine(line+FragmentUtil.CR);
                if(!line.startsWith("#")){
                    String block =  FragmentUtil.readRestOfBlock(scanner , visitor);               
                    if(line.startsWith(BEGIN_LIB)){
                        parseLibraryFrom(block, visitor);
                    }
                    else if(line.startsWith(BEGIN_FRG)){
                        parseFragmentFrom(block,visitor);
                    }
                    else if(line.startsWith(BEGIN_LKG)){
                        parseLinkFrom(block,visitor);
                    }
                }
            }
            visitor.visitEndOfFile();
        }
        finally{
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }
    
    private void parseLinkFrom(String lkg, Frg2Visitor visitor) {
        FrgVisitorAction action = parseAction(lkg);
        Scanner scanner = new Scanner(lkg);
        List<String> fragIds = new ArrayList<String>();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher matcher = LKG_FRG_ID_PATTERN.matcher(line);
            if(matcher.find()){
                fragIds.add(matcher.group(1));
            }
        }
        visitor.visitLink(action, fragIds);
        
    }
    private FrgVisitorAction parseAction(String message){
        Matcher matcher = ACTION_PATTERN.matcher(message);
        if(matcher.find()){
            return FrgVisitorAction.parseAction(matcher.group(1).charAt(0));
        }
        throw new IllegalStateException("Could not find a Fragment Action in "+ message);
    }
    private void parseLibraryFrom(String libraryRecord, Frg2Visitor visitor) {
        String id = parseIdFrom(libraryRecord);
        MateOrientation orientation = parseMateOrientationFrom(libraryRecord);
        Distance distance = createDistanceFrom(libraryRecord);
        FrgVisitorAction action = parseAction(libraryRecord);
        visitor.visitLibrary(action, id, orientation, distance);
    }
    private MateOrientation parseMateOrientationFrom(String libraryRecord) {
        MateOrientation orientation;
        Matcher orientationMatcher = LIB_ORIENTATION_PATTERN.matcher(libraryRecord);
        if(orientationMatcher.find()){
            orientation = MateOrientation.parseMateOrientation(orientationMatcher.group(1));
        }
        else{
            orientation=MateOrientation.UNORIENTED;
        }
        return orientation;
    }

    private Distance createDistanceFrom(String libraryRecord) {
        Matcher distanceMatcher = LIB_DST_PATTERN.matcher(libraryRecord);
        if(distanceMatcher.find()){
            return Distance.buildDistance(Float.parseFloat(distanceMatcher.group(1)), 
                    Float.parseFloat(distanceMatcher.group(2)));
        }
        return null;
    }

    private void parseFragmentFrom(String frg,Frg2Visitor visitor) {
        String id = parseIdFrom(frg);
        FrgVisitorAction action = parseAction(frg);
        if(action == FrgVisitorAction.DELETE){
            //delete doesn't contain most of the info we need
            visitor.visitFragment(action, id,null,null,null,null,null,null);
        }
        else{
            NucleotideEncodedGlyphs bases = FragmentUtil.parseBasesFrom(frg);
            QualityEncodedGlyphs qualities = parseEncodedQualitiesFrom(frg);
            Range validRange =  FragmentUtil.parseValidRangeFrom(frg);
            Range vectorClearRange = parseVectorClearRangeFrom(frg);
            if(vectorClearRange == null && validRange !=null){
                vectorClearRange = validRange;
            }
            String libraryId = parseLibraryIdFrom(frg);
            
            String source = parseSourceFrom(frg);
            visitor.visitFragment(action, id, 
                    libraryId, bases, 
                    qualities, validRange, 
                    vectorClearRange,
                    source);
        }
    }
    private String parseSourceFrom(String frg) {
        Scanner scanner = new Scanner(frg);
        scanner.findWithinHorizon(SOURCE_PATTERN, 0);
        StringBuilder bases = new StringBuilder();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(FragmentUtil.endOfMultilineField(line)){
                break;
            }
            bases.append(line+FragmentUtil.CR);
        }
        return bases.toString();
    }

    private String parseLibraryIdFrom(String frg) {
        Matcher libraryMatcher = FRG_LIB_PATTERN.matcher(frg);
        if(libraryMatcher.find()){
            return libraryMatcher.group(1);
        }
        return null;
    }

  
    private Range parseVectorClearRangeFrom(String frg) {
        Matcher matcher =FRG_VECTOR_CLEAR_RANGE_PATTERN.matcher(frg);
        return FragmentUtil.parseRangeFrom(matcher);
    }
    
   
    private QualityEncodedGlyphs parseEncodedQualitiesFrom(String frg) {
       
        return  new DefaultQualityEncodedGlyphs(RUN_LENGTH_CODEC,
                FragmentUtil.parseEncodedQualitiesFrom(frg));        
    }
    
    protected String parseIdFrom(String frg) {
       
        Matcher matcher = ACC_ID_PATTERN.matcher(frg);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

}
