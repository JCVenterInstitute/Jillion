/*
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.glyph.phredQuality.PhredQuality;

public class LowestFlankingQualityValueStrategy extends
        AbstractQualityValueStrategy {

    private final PhredQuality LOWEST_QUALITY = PhredQuality.valueOf((byte)1);
    

    @Override
    protected PhredQuality getQualityValueIfReadEndsWithGap() {
        return LOWEST_QUALITY;
    }

    @Override
    protected PhredQuality getQualityValueIfReadStartsWithGap() {
        return LOWEST_QUALITY;
    }

    @Override
    protected PhredQuality computeQualityValueForGap(
            int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality) {
        if(leftFlankingQuality.compareTo(rightFlankingQuality)<0){
            return leftFlankingQuality;
        }
        return rightFlankingQuality;
    }

}
