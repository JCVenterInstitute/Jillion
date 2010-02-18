/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

public interface AlignmentMatrix<T> {

    /**
     * Fetches the name of this substitution matrix.
     * 
     * @return The given name of this matrix.
     */
    String getName();   
    
    /**
     * Calculates the score assigned to a given pairing of sequence characters.  The order of
     * the characters should not affect the score.
     * 
     * @param a The first sequence character.
     * @param b The second sequence character.
     * @return The score, as a byte, which may be either positive or negative.
     */
    byte getScore(T a, T b);

    /**
     * Fetches the default sequence character to use when the given character is not recognized
     * by the current substitution matrix.
     * 
     * @return The default sequence character.
     */
    T getDefaultCharacter();

}
