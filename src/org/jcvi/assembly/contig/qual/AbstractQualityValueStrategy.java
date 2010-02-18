/*
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractQualityValueStrategy implements QualityValueStrategy {

    @Override
    public PhredQuality getQualityFor(PlacedRead placedRead,
            EncodedGlyphs<PhredQuality> fullQualities,
            int gappedReadIndex) {
        if(fullQualities ==null){
            throw new NullPointerException("null qualities for "+placedRead);
        }
        if(!AssemblyUtil.isAGap(placedRead, gappedReadIndex)){
            return getQualityForNonGapBase(placedRead, fullQualities, gappedReadIndex);
        }
        int leftFlankingNonGapIndex = AssemblyUtil.getLeftFlankingNonGapIndex(placedRead,gappedReadIndex-1);
        int rightFlankingNonGapIndex = AssemblyUtil.getRightFlankingNonGapIndex(placedRead,gappedReadIndex+1);
        
        final PhredQuality qualityOfGap = getQualityValueForGap(leftFlankingNonGapIndex, rightFlankingNonGapIndex, placedRead, fullQualities,gappedReadIndex);
        
        return qualityOfGap;
    }
    protected abstract PhredQuality getQualityValueIfReadStartsWithGap();
    protected abstract PhredQuality getQualityValueIfReadEndsWithGap();
    
    protected abstract PhredQuality computeQualityValueForGap(int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality);
    
    private PhredQuality getQualityValueForGap(int leftFlankingNonGapIndex,
            int rightFlankingNonGapIndex, PlacedRead placedRead,
            EncodedGlyphs<PhredQuality> fullQualities,int indexOfGap) {
        if(AssemblyUtil.beforeStartOfRead(leftFlankingNonGapIndex)){
            return getQualityValueIfReadStartsWithGap();
        }
        if(AssemblyUtil.afterEndOfRead(rightFlankingNonGapIndex, placedRead)){
            return getQualityValueIfReadEndsWithGap();
        }
        PhredQuality leftFlankingQuality = getQualityForNonGapBase(placedRead, fullQualities, leftFlankingNonGapIndex);
        PhredQuality rightFlankingQuality = getQualityForNonGapBase(placedRead, fullQualities, rightFlankingNonGapIndex);
        int ithGapToCompute = indexOfGap - leftFlankingNonGapIndex-1;
        final int numberOfGapsBetweenFlanks = rightFlankingNonGapIndex-leftFlankingNonGapIndex-1;
        return computeQualityValueForGap(numberOfGapsBetweenFlanks, ithGapToCompute, leftFlankingQuality, rightFlankingQuality);
    }
    

    protected PhredQuality getQualityForNonGapBase(PlacedRead placedRead, EncodedGlyphs<PhredQuality> fullQualities,
            int gappedReadIndexForNonGapBase) {
        try{
        int ungappedFullRangeIndex = AssemblyUtil.convertToUngappedFullRangeIndex(placedRead, (int)fullQualities.getLength(),gappedReadIndexForNonGapBase);
        
            return fullQualities.get(ungappedFullRangeIndex);
        }
        catch(ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("could not get quality for read " + placedRead +" at gapped index " +gappedReadIndexForNonGapBase,e);
        }
    }
    
   
}
