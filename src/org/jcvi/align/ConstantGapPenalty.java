/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;
/**
 * {@code ConstantGapPenalty} always has the same
 * gap penalty score regardless of the gap size.
 * @author dkatzel
 *
 *
 */
public class ConstantGapPenalty implements GapPenalty {

    private final int gapPenalty;
    
    /**
     * @param gapPenalty
     */
    public ConstantGapPenalty(int gapPenalty) {
        this.gapPenalty = gapPenalty;
    }

    @Override
    public int getNextGapPenalty() {
        return gapPenalty;
    }

    @Override
    public void reset() {

    }

}
