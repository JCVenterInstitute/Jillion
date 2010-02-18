/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;
/**
 * {@code AffineGapPenalty} are length dependent.
 * @author dkatzel
 *
 *
 */
public class AffineGapPenalty implements GapPenalty {

    private final int openingPenalty;
    private final int extensionPenalty;
    private int sizeOfGap=0;
    /**
     * @param openingPenalty
     * @param extension
     */
    public AffineGapPenalty(int openingPenalty, int extensionPenalty) {
        this.openingPenalty = openingPenalty;
        this.extensionPenalty = extensionPenalty;
    }

    @Override
    public int getNextGapPenalty() {
        int result =openingPenalty + sizeOfGap*extensionPenalty;
        sizeOfGap++;
        return result;
    }

    @Override
    public void reset() {
        sizeOfGap=0;
        
    }

}
