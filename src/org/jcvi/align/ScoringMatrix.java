/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
