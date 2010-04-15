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
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

public class AssemblyUtil {

    public static <R extends PlacedRead> List<NucleotideGlyph> buildGappedComplimentedFullRangeBases(R placedRead, List<NucleotideGlyph> ungappedUncomplimentedFullRangeBases){
        List<NucleotideGlyph> fullRangeComplimented;
        if(placedRead.getSequenceDirection().equals(SequenceDirection.REVERSE)){
            fullRangeComplimented = NucleotideGlyph.reverseCompliment(ungappedUncomplimentedFullRangeBases);
        }
        else{
            fullRangeComplimented = ungappedUncomplimentedFullRangeBases;
        }
        Range validRange = placedRead.getValidRange();
        List<NucleotideGlyph> gappedComplimentedFullRange = new ArrayList<NucleotideGlyph>();
        for(int i=0; i< validRange.getStart(); i++ ){
            gappedComplimentedFullRange.add(fullRangeComplimented.get(i));
        }
        gappedComplimentedFullRange.addAll(placedRead.getEncodedGlyphs().decode());
        for(int i=(int)validRange.getEnd()+1; i< fullRangeComplimented.size(); i++){
            gappedComplimentedFullRange.add(fullRangeComplimented.get(i));
        }
        return gappedComplimentedFullRange;
    }
    
    public static Range reverseComplimentValidRange(Range validRange, long fullLength){
        if(fullLength < validRange.size()){
            throw new IllegalArgumentException("valid range larger than fullLength");
        }
        long newStart = fullLength - validRange.getEnd()-1;
        long newEnd = fullLength - validRange.getStart()-1;
        return Range.buildRange(newStart, newEnd).convertRange(validRange.getRangeCoordinateSystem());
    }
    
    public static <R extends PlacedRead> int convertToUngappedFullRangeIndex(R placedRead, int fullLength,int gappedIndex) {
        Range validRange = placedRead.getValidRange();
        return convertToUngappedFullRangeIndex(placedRead, fullLength,
                gappedIndex, validRange);
    }



    public static <R extends PlacedRead> int convertToUngappedFullRangeIndex(R placedRead,
            int fullLength, int gappedIndex, Range validRange) {
        int ungappedValidRangeIndex = convertToUngappedValidRangeIndex(placedRead, gappedIndex);        
        if(placedRead.getSequenceDirection() == SequenceDirection.REVERSE){
            validRange = Range.buildRange(fullLength - placedRead.getValidRange().getEnd(), 
                                                    fullLength - placedRead.getValidRange().getStart());
            int distanceFromLeft=  ungappedValidRangeIndex + (int)validRange.getStart();
            return fullLength - distanceFromLeft;
            
        }        
        int distanceFromLeft=  ungappedValidRangeIndex + (int)validRange.getStart();
        
        return distanceFromLeft;
    }



    public static int convertToUngappedValidRangeIndex(PlacedRead placedRead, int gappedIndex) {
       return placedRead.getEncodedGlyphs().convertGappedValidRangeIndexToUngappedValidRangeIndex(gappedIndex);
    }
    
    public static boolean afterEndOfRead(int rightFlankingNonGapIndex,
            NucleotideEncodedGlyphs placedRead) {
        return rightFlankingNonGapIndex> placedRead.getLength()-1;
    }

    public static boolean isAGap(NucleotideEncodedGlyphs glyphs, int gappedReadIndex) {
        return glyphs.isGap(gappedReadIndex);
    }

    public static int getLeftFlankingNonGapIndex(NucleotideEncodedGlyphs placedRead, int gappedReadIndex) {
        if(beforeStartOfRead(gappedReadIndex)){
            return gappedReadIndex;
        }
        if(isAGap(placedRead, gappedReadIndex)){
            return getLeftFlankingNonGapIndex(placedRead,gappedReadIndex-1);
        }
        
        return gappedReadIndex;
    }
    public static boolean beforeStartOfRead(int gappedReadIndex) {
        return gappedReadIndex<0;
    }
    

    public static int getRightFlankingNonGapIndex(NucleotideEncodedGlyphs placedRead, int gappedReadIndex) {
        if(afterEndOfRead(gappedReadIndex, placedRead)){
            return gappedReadIndex;
        }
        if(isAGap(placedRead, gappedReadIndex)){
            return getRightFlankingNonGapIndex(placedRead,gappedReadIndex+1);
        }
        return gappedReadIndex;
    }
}
