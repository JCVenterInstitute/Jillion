/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment.score;

public class DefaultCasColorSpaceAlignmentScore implements CasColorSpaceAlignmentScore{

    private final CasAlignmentScore delegate;
    
    private final int colorSpaceError;

    /**
     * @param firstInsertion
     * @param insertionExtension
     * @param firstDeletion
     * @param deletionExtension
     * @param match
     * @param transition
     * @param transversion
     * @param unknown
     * @param delegate
     * @param colorSpaceError
     */
    public DefaultCasColorSpaceAlignmentScore(CasAlignmentScore delegate, int colorSpaceError) {
        this.delegate = delegate;
        this.colorSpaceError = colorSpaceError;
    }

    @Override
    public int getDeletionExtensionCost() {
        return delegate.getDeletionExtensionCost();
    }

    @Override
    public int getFirstDeletionCost() {
        return delegate.getFirstDeletionCost();
    }

    @Override
    public int getFirstInsertionCost() {
        return delegate.getFirstInsertionCost();
    }

    @Override
    public int getInsertionExtensionCost() {
        return delegate.getInsertionExtensionCost();
    }

    @Override
    public int getMatchScore() {
        return delegate.getMatchScore();
    }

    @Override
    public int getTransitionScore() {
        return delegate.getTransitionScore();
    }

    @Override
    public int getTransversionScore() {
        return delegate.getTransversionScore();
    }

    @Override
    public int getUnknownScore() {
        return delegate.getUnknownScore();
    }

    @Override
    public int getColorSpaceErrorCost() {
        return colorSpaceError;
    }

    @Override
    public String toString() {
        return "DefaultCasColorSpaceAlignmentScore [delegate=" + delegate
                + ", colorSpaceError=" + colorSpaceError + "]";
    }
    
}
