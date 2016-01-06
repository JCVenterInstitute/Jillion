package org.jcvi.jillion.trim.trimmomatic;

import org.jcvi.jillion.core.qual.PhredQuality;
/**
 * Trims off the bases from the leading (5') edge of a read
 * that have quailty values below the given threshold.
 * 
 * @author dkatzel
 *
 * @since 5.2
 * 
 * @see TrailingQualityTrimmer
 */
public class LeadingQualityTrimmer extends AbstractEdgeQualityTrimmer {
    /**
     * Trim off the bases that are below the given threshold.
     * @param threshold the quality value threshold to use.  Should be a valid {@link PhredQuality} value.
     * 
     * @implSpec this is the same thing as using {@link PhredQuality#valueOf(int)} with
     * threshold as the parameter.
     * 
     * @throws IllegalArgumentException if threshold is an invalid {@link PhredQuality} value.
     */
    public LeadingQualityTrimmer(int threshold) {
        super(threshold, true);
    }
    /**
     * Trim off the bases that are below the given threshold.
     * @param threshold the quality value threshold to use. Can not be null.
     * 
     * @throws NullPointerException if threshold is null
     */
    public LeadingQualityTrimmer(PhredQuality threshold) {
        super(threshold, true);
    }

}
