/*
 * Created on Dec 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.glyph.phredQuality.PhredQuality;

public class ZeroGapQualityValueStrategy extends AbstractQualityValueStrategy{

    @Override
    protected PhredQuality computeQualityValueForGap(
            int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality) {
        return PhredQuality.valueOf(0);
    }

    @Override
    protected PhredQuality getQualityValueIfReadEndsWithGap() {
        return PhredQuality.valueOf(0);
    }

    @Override
    protected PhredQuality getQualityValueIfReadStartsWithGap() {
        return PhredQuality.valueOf(0);
    }

}
