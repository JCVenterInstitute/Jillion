/*
 * Created on Nov 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;


import org.jcvi.glyph.Glyph;

public interface ScoringMatrix<G extends Glyph> {

    /**
     * Evaluate the matrix, calculating scores and pathing hints for all elements.  This method
     * will do a full recalculation on the matrix, including setting the path hints and 
     * identifying the highest scoring element (which becomes the start point for the best
     * alignment).
     * 
     * @param matrix The {@link NucleotideSubstitutionMatrix} to use for calculating scores.
     */
    void evaluate(AlignmentMatrix<G> matrix);
    /**
     * Fetches the {@link Coordinate} of the element with the highest score.  This identifies
     * the starting location of the best alignment in the matrix.
     * 
     * @return A {@link Coordinate} whose score is no loes than any other element.
     */
    public Coordinate getBestStart();
    
    /**
     * Fetch the pathing hint for the element for the element at the given {@link Coordinate}.
     * 
     * @param c The {@link Coordinate} which addresses the element.
     * @return The pathing hint for this cell.
     */
    public Path getPath(Coordinate c);
    
    /**
     * Fetch the score for the element at the given {@link Coordinate}.
     * 
     * @param c The {@link Coordinate} which addresses the element.
     * @return The matrix cell score.
     */
    public int getScore(int x, int y);
    
    public int getNumberOfRows();
    
    public int getNumberOfColumns();

}