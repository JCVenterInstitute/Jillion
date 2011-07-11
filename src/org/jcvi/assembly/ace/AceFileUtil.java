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

package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;
import org.jcvi.trace.sanger.phd.Phd;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author dkatzel
 *
 *
 */
public class AceFileUtil {
    public static final PhredQuality ACE_DEFAULT_HIGH_QUALITY_THRESHOLD = PhredQuality.valueOf(26);
    public static final DateTimeFormatter CHROMAT_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy");
    public static final DateTimeFormatter TAG_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("YYMMdd:HHmmss");
    
    public static String convertToAcePaddedBasecalls(NucleotideEncodedGlyphs basecalls){
        return convertToAcePaddedBasecalls(basecalls.decode(),null);
     }
     public static String convertToAcePaddedBasecalls(List<NucleotideGlyph> basecalls,List<PhredQuality> qualities){
         StringBuilder result = new StringBuilder();
         int numberOfGapsSoFar=0;
         
         for(int i=0; i< basecalls.size(); i++){
             NucleotideGlyph base = basecalls.get(i);
             if(base == NucleotideGlyph.Gap){
                 result.append("*");
                 numberOfGapsSoFar++;
             }
             else{
                 if(qualities!=null){
                     PhredQuality quality =qualities.get(i-numberOfGapsSoFar);
                     if(quality.compareTo(ACE_DEFAULT_HIGH_QUALITY_THRESHOLD)<0){
                         result.append(base.toString().toLowerCase());
                     }
                     else{
                         result.append(base);
                     }
                 }else{
                     result.append(base);
                 }
             }
         }
         
         String consedBasecalls= result.toString().replaceAll("(.{50})", "$1"+String.format("%n"));
         if(basecalls.size() %50 ==0){
             //if the last line is full, then we will have an extra %n
             //so strip it off
             return consedBasecalls.substring(0,consedBasecalls.length()-1);
         }
         return consedBasecalls;
     }

    public static String createAssembledFromRecord(AssembledFrom assembledFrom){
        return String.format("AF %s %s %d%n",
                assembledFrom.getId(),
                assembledFrom.getSequenceDirection()==SequenceDirection.FORWARD? "U":"C",
                        assembledFrom.getStartOffset());
    }
    public static String createPhdRecord(PhdInfo phdInfo){
        return String.format("DS CHROMAT_FILE: %s PHD_FILE: %s TIME: %s", 
                
                phdInfo.getTraceName(),
                phdInfo.getPhdName(),
                AceFileUtil.CHROMAT_DATE_TIME_FORMATTER.print(phdInfo.getPhdDate().getTime())
                );
                
        
    }
    
    public static String createQualityRangeRecord(NucleotideEncodedGlyphs gappedValidBases, 
            Range ungappedValidRange, SequenceDirection dir, long ungappedFullLength){
        int numberOfGaps = gappedValidBases.getNumberOfGaps();
        Range gappedValidRange =buildGappedValidRangeFor(
                ungappedValidRange,numberOfGaps,dir,ungappedFullLength);

        
       return String.format("QA %d %d %d %d",
                gappedValidRange.getLocalStart(), gappedValidRange.getLocalEnd(),
                gappedValidRange.getLocalStart(), gappedValidRange.getLocalEnd()
                );
    }
    private static Range buildGappedValidRangeFor(Range ungappedValidRange, int numberOfGaps,SequenceDirection dir, long ungappedFullLength){
       Range gappedValidRange=  Range.buildRange( 
               ungappedValidRange.getStart(),
               ungappedValidRange.getEnd()+numberOfGaps);
        
        if(dir==SequenceDirection.REVERSE){
            gappedValidRange = AssemblyUtil.reverseComplimentValidRange(gappedValidRange, ungappedFullLength+numberOfGaps);
           
        }
        return gappedValidRange.convertRange(CoordinateSystem.RESIDUE_BASED);
    }
    public static String createAcePlacedReadRecord(String readId, NucleotideEncodedGlyphs gappedValidBasecalls, 
            Range ungappedValidRange, SequenceDirection dir, Phd phd, PhdInfo phdInfo){
        final NucleotideEncodedGlyphs fullBasecalls = phd.getBasecalls();
        final List<NucleotideGlyph> phdFullBases = fullBasecalls.decode();
        
        final List<NucleotideGlyph> fullGappedValidRange;
        final List<PhredQuality> qualities;
        final EncodedGlyphs<PhredQuality> phdQualities = phd.getQualities();
        if(dir == SequenceDirection.FORWARD){
            fullGappedValidRange = AssemblyUtil.buildGappedComplimentedFullRangeBases(gappedValidBasecalls,dir,ungappedValidRange, 
                    phdFullBases);
            qualities = phdQualities.decode();
        }else{
            final List<NucleotideGlyph> complimentedFullBases = NucleotideGlyph.reverseCompliment(phdFullBases);
            Range complimentedValidRange = AssemblyUtil.reverseComplimentValidRange(
                    ungappedValidRange,
                    complimentedFullBases.size());
            //we break it up into outside of valid range and inside valid range
            //so we get the gaps in the correct places
            fullGappedValidRange = createReverseComplimentedGappedFullLengthBasecalls(
                    gappedValidBasecalls, complimentedFullBases,
                    complimentedValidRange);
            List<PhredQuality> uncomplimentedQualities = phdQualities.decode();
            qualities = new ArrayList<PhredQuality>(uncomplimentedQualities.size());
            for(int i=uncomplimentedQualities.size()-1; i>=0; i--){
                qualities.add(uncomplimentedQualities.get(i));
            }
        }
        StringBuilder readRecord = new StringBuilder();
        readRecord.append(String.format("RD %s %d 0 0%n",
                                            readId,
                                            fullGappedValidRange.size()));
        
        
        readRecord.append(String.format("%s%n%n",
                AceFileUtil.convertToAcePaddedBasecalls(fullGappedValidRange,qualities)));
        readRecord.append(String.format("%s%n",createQualityRangeRecord(
                gappedValidBasecalls,ungappedValidRange,dir, 
                fullBasecalls.getUngappedLength())));
        readRecord.append(String.format("%s%n",createPhdRecord(phdInfo)));
        return readRecord.toString();
    }
    private static List<NucleotideGlyph> createReverseComplimentedGappedFullLengthBasecalls(
            NucleotideEncodedGlyphs gappedValidBasecalls,
            final List<NucleotideGlyph> complimentedFullBases,
            Range complimentedValidRange) {
        final List<NucleotideGlyph> fullGappedValidRange;
        fullGappedValidRange=new ArrayList<NucleotideGlyph>();
        fullGappedValidRange.addAll(complimentedFullBases.subList(0, (int)complimentedValidRange.getStart()));            
        fullGappedValidRange.addAll(gappedValidBasecalls.decode());
        fullGappedValidRange.addAll(complimentedFullBases.subList(
                (int)complimentedValidRange.getEnd()+1, 
                complimentedFullBases.size()));
        return fullGappedValidRange;
    }
}
