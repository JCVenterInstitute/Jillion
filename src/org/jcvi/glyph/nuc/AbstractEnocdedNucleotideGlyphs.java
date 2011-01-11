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
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyUtil;

public abstract class AbstractEnocdedNucleotideGlyphs implements NucleotideEncodedGlyphs{

    @Override
    public int convertGappedValidRangeIndexToUngappedValidRangeIndex(
            int gappedValidRangeIndex) {
        if(isAGap(gappedValidRangeIndex)){
            //we are given a gap
            //which we can't convert into an ungapped index
            throw new IllegalArgumentException(gappedValidRangeIndex + " is a gap");
        }
        int numberOfGaps = computeNumberOfInclusiveGapsInGappedValidRangeUntil(gappedValidRangeIndex);
        return gappedValidRangeIndex-numberOfGaps;
    }

    @Override
    public Range convertGappedValidRangeToUngappedValidRange(
            Range gappedValidRange) {
       return Range.buildRange(
               convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       AssemblyUtil.getLeftFlankingNonGapIndex(this,(int)gappedValidRange.getStart())),
               convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       AssemblyUtil.getLeftFlankingNonGapIndex(this, (int)gappedValidRange.getEnd()))
                
        );
    }

    @Override
    public Range convertUngappedValidRangeToGappedValidRange(
            Range ungappedValidRange) {
        return  Range.buildRange(
                convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedValidRange.getStart()),
                convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedValidRange.getEnd()));
                
    }

    private boolean isAGap(int gappedValidRangeIndex) {
        return getGapIndexes().contains(Integer.valueOf(gappedValidRangeIndex));
    }

    @Override
    public long getUngappedLength(){
        return getLength() - getNumberOfGaps();
    }
    @Override
    public int computeNumberOfInclusiveGapsInGappedValidRangeUntil(int gappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapIndexes()){
            if(gapIndex.intValue() <=gappedValidRangeIndex){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }
    @Override
    public int computeNumberOfInclusiveGapsInUngappedValidRangeUntil(int ungappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapIndexes()){
            //need to account for extra length due to gaps being added to ungapped index
            if(gapIndex.intValue() <=ungappedValidRangeIndex + numberOfGaps){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }

    @Override
    public int convertUngappedValidRangeIndexToGappedValidRangeIndex(
            int ungappedValidRangeIndex) {
        int numberOfGaps = computeNumberOfInclusiveGapsInUngappedValidRangeUntil(ungappedValidRangeIndex);
        return ungappedValidRangeIndex+numberOfGaps;
    }
    
    @Override
    public List<NucleotideGlyph> decode(Range range) {
        if(range==null){
            return decode();
        }
        List<NucleotideGlyph> result = new ArrayList<NucleotideGlyph>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

    @Override
    public List<NucleotideGlyph> decodeUngapped() {
        List<NucleotideGlyph> withoutGaps = decode();
        final List<Integer> gapIndexes = getGapIndexes();
        for(int i= gapIndexes.size()-1; i>=0; i--){
            withoutGaps.remove(gapIndexes.get(i).intValue());
        }
        return withoutGaps;
    }
    
    
}
