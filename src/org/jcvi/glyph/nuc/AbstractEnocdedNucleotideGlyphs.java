/*
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;

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

    private boolean isAGap(int gappedValidRangeIndex) {
        return getGapIndexes().contains(Integer.valueOf(gappedValidRangeIndex));
    }

    @Override
    public long getUngappedLength(){
        return getLength() - getGapIndexes().size();
    }

    private int computeNumberOfInclusiveGapsInGappedValidRangeUntil(int gappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapIndexes()){
            if(gapIndex.intValue() <=gappedValidRangeIndex){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }
    private int computeNumberOfInclusiveGapsInUngappedValidRangeUntil(int ungappedValidRangeIndex) {
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
