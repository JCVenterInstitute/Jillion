/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

public class LinearGapPenalty extends AffineGapPenalty {

    /**
     * @param gapPenalty
     */
    public LinearGapPenalty(int gapPenalty) {
       super(gapPenalty,0);
    }

    
}
