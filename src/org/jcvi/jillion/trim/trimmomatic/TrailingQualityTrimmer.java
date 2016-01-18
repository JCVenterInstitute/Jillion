package org.jcvi.jillion.trim.trimmomatic;

import org.jcvi.jillion.core.qual.PhredQuality;
/**
 * Trims off the bases from the trailing (3') edge of a read
 * that have quailty values below the given threshold.
 * 
 * @author dkatzel
 *
 * @since 5.2
 * 
 * @see LeadingQualityTrimmer
 */
public class TrailingQualityTrimmer extends AbstractEdgeQualityTrimmer {
   
    /**
     * Trim off the bases that are below the given threshold.
     * @param threshold the quality value threshold to use. Can not be null.
     * 
     * @throws NullPointerException if threshold is null
     */
    public TrailingQualityTrimmer(PhredQuality threshold) {
        super(threshold, false);
    }

}
