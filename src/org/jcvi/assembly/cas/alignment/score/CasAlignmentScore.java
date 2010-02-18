/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment.score;

public interface CasAlignmentScore {

    int getFirstInsertionCost();
    
    int getInsertionExtensionCost();
    
    int getFirstDeletionCost();
    
    int getDeletionExtensionCost();
    
    int getMatchScore();
    /**
     * A <=> G or C <=> T.
     * @return
     */
    int getTransitionScore();
    /**
     * score of other differences that aren't transitions.
     * @return
     */
    int getTransversionScore();
    
    int getUnknownScore();
}
