/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
/**
 * ScoringMatrix.java
 *
 * Created: Aug 10, 2009 - 8:49:20 AM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.align;

import java.io.PrintStream;
import java.nio.IntBuffer;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

/**
 * A <code>ScoringMatrix</code> is a matrix of Smith-Waterman alignment scores with built-in
 * precomputed optimal pathing information.
 *
 * @author jsitz@jcvi.org
 */
public class ScoringMatrix<G extends Glyph>
{
    /** The score to record on the terminating edges (far edges) of the matrix. */
    private static final int SCORE_TERMINATOR = 0;
    
    /** The integer mask for the score portion of the element. */
    private static final int SCORE_MASK = 0x0FFFFFFF;
    /** The integer mask for the path portion of the element. */
    private static final int PATH_MASK  = 0x70000000;
    /** The bit offset to the path portion of the element.*/
    private static final int PATH_OFFSET = 28;
    /** The bit offset the score must be shifted to rattach to the sign bit. */
    private static final int SCORE_SHIFT = 32 - ScoringMatrix.PATH_OFFSET;
    
    /** The path code for the end of an alignment. */
    public static final int PATH_TERM = 0;
    /** The path code for a horizontal (reference gap) transition. */
    public static final int PATH_HORZ = 1;
    /** The path code for a vertical (query gap) transition. */
    public static final int PATH_VERT = 2;
    /** The path code for a diagonal (sequence similarity) transition. */
    public static final int PATH_DIAG = 3;
    
    /** The scoring data, as a linear array of integers. */
    private final IntBuffer scores;
    /** The number of rows in the matrix. */
    private final int rowCount;
    /** The number of columns in the matrix. */
    private final int colCount;
    /** The score "cost" for transitioning via gap. */
    private final byte gapPenalty;
    
    /** The query sequence. */
    private final EncodedGlyphs<G> querySeq;
    /** The reference sequence. */
    private final EncodedGlyphs<G> refSeq;
    
    /** The linear index of the best score encountered in the matrix. */
    private int bestScoreIndex;
    
    /**
     * Creates a new <code>ScoringMatrix</code>.
     * 
     * @param refSequence The reference sequence.
     * @param querySequence The query sequence.
     * @param gapPenalty The penalty to apply to gap transitions within the scoring matrix.
     * Note, this value should be no greater than 0 (positive values will <em>encourage</em>
     * gap transitions).
     */
    public ScoringMatrix(EncodedGlyphs<G> refSequence, EncodedGlyphs<G> querySequence, int gapPenalty)
    {
        super();
        
        this.querySeq = refSequence;
        this.refSeq = querySequence;
        this.rowCount = (int)refSequence.getLength() + 1;
        this.colCount = (int)querySequence.getLength() + 1;
        this.gapPenalty = (byte)gapPenalty;
        this.scores = IntBuffer.allocate(this.rowCount * this.colCount);
                
        this.init();
    }
    
    public int getNumberOfRows(){
        return rowCount;
    }
    public int getNumberOfColumns(){
        return colCount;
    }
    /**
     * Initialize the matrix, setting up default values and terminating the edges of the matrix.
     */
    protected void init()
    {
        /*
         * Fill with invalid data
         */
        for(int row = 0; row < this.rowCount; row++)
        {
            for(int col = 0; col < this.colCount; col++)
            {
                this.setScore(row, col, 0);
            }
        }
        
        /*
         * Terminate the last row
         */
        for (int i = 0; i < this.colCount; i++)
        {
            this.setScore(this.rowCount-1, i, ScoringMatrix.SCORE_TERMINATOR);
            this.setPath(this.rowCount-1, i, ScoringMatrix.PATH_TERM);
        }
        
        /*
         * Terminate the last column
         */
        for (int i = 0; i < this.rowCount; i++)
        {
            this.setScore(i, this.colCount-1, ScoringMatrix.SCORE_TERMINATOR);
            this.setPath(i, this.colCount-1, ScoringMatrix.PATH_HORZ);
        }
        
    }
    
    /**
     * Calculate the linear index of an element based on the 
     * 
     * @param row The row component (on the reference axis)
     * @param col The column component (on the query axis);
     * @return The linear index of the addressed element.
     */
    protected int indexOf(int row, int col)
    {
        return (row * this.colCount) + col;
    }
    
    /**
     * Set the score for the element at the given linear index.
     * 
     * @param index The index of the element to set.
     * @param score The score to set.
     */
    protected void setScore(int index, int score)
    {
        final int path = this.getPath(index);
        final int value = (Math.max(0, score) & ScoringMatrix.SCORE_MASK) | (path << ScoringMatrix.PATH_OFFSET);
        
        this.scores.put(index, value);
    }
    
    /**
     * Sets the score for the element at the given coordinates.
     * 
     * @param indexA The coordinate on the reference axis.
     * @param indexB The coordinate on the query axis.
     * @param score The score to set.
     */
    protected void setScore(int indexA, int indexB, int score)
    {
        this.setScore(this.indexOf(indexA, indexB), score);
    }
    
    /**
     * Sets the score for the element at the given {@link Coordinate}.
     * 
     * @param coord The {@link Coordinate} for the element.
     * @param score The score to set.
     */
    protected void setScore(Coordinate coord, int score)
    {
        this.setScore(coord.y, coord.x, score);
    }
    
    /**
     * Set the path hint for the element at the given linear index.
     * 
     * @param index The index of the element to set.
     * @param path The pathing hint.
     */
    protected void setPath(int index, int path)
    {
        final int score = this.getScore(index);
        final int value = score | (path << ScoringMatrix.PATH_OFFSET);
        
        this.scores.put(index, value);
    }
    
    /**
     * Sets the path hint for the element at the given coordinates.
     * 
     * @param indexA The coordinate on the reference axis.
     * @param indexB The coordinate on the query axis.
     * @param path The pathing hint.
     */
    protected void setPath(int indexA, int indexB, int path)
    {
        this.setPath(this.indexOf(indexA, indexB), path);
    }
    
    /**
     * Sets the path hint for the element at the given {@link Coordinate}.
     * 
     * @param coord The {@link Coordinate} for the element.
     * @param path The pathing hint.
     */
    protected void setPath(Coordinate coord, int path)
    {
        this.setPath(coord.x, coord.y, path);
    }
    
    /**
     * Fetch the score for the element at the given linear index.
     * 
     * @param index The element index.
     * @return The matrix cell score.
     */
    protected int getScore(int index)
    {
        return (this.scores.get(index) & ScoringMatrix.SCORE_MASK) << ScoringMatrix.SCORE_SHIFT >> ScoringMatrix.SCORE_SHIFT;
    }
    
    /**
     * Fetch the score for the element at the given coordinates.
     * 
     * @param indexA The row coordinate (on the reference axis).
     * @param indexB The column coordinate (on the query axis).
     * @return The matrix cell score.
     */
    protected int getScore(int indexA, int indexB)
    {
        return this.getScore(this.indexOf(indexA, indexB));
    }
    
    /**
     * Fetch the score for the element at the given {@link Coordinate}.
     * 
     * @param c The {@link Coordinate} which addresses the element.
     * @return The matrix cell score.
     */
    protected int getScore(Coordinate c)
    {
        //FIXME this is backwards!
        return this.getScore(c.y, c.x);
    }
    
    /**
     * Fetch the pathing hint for the element at the given linear index.
     * 
     * @param index The linear index of the element.
     * @return The pathing hint for this cell.
     */
    protected int getPath(int index)
    {
        return (this.scores.get(index) & ScoringMatrix.PATH_MASK) >> ScoringMatrix.PATH_OFFSET;
    }
    
    /**
     * Fetch the pathing hint for the element at the given coordinates.
     * 
     * @param indexA The row index (on the reference axis)
     * @param indexB The column index (on the query axis)
     * @return The pathing hint for this cell.
     */
    protected int getPath(int indexA, int indexB)
    {
        return this.getPath(this.indexOf(indexA, indexB));
    }
    
    /**
     * Fetch the pathing hint for the element for the element at the given {@link Coordinate}.
     * 
     * @param c The {@link Coordinate} which addresses the element.
     * @return The pathing hint for this cell.
     */
    protected int getPath(Coordinate c)
    {
        return this.getPath(c.y, c.x);
    }
    
    /**
     * Evaluate the matrix, calculating scores and pathing hints for all elements.  This method
     * will do a full recalculation on the matrix, including setting the path hints and 
     * identifying the highest scoring element (which becomes the start point for the best
     * alignment).
     * 
     * @param matrix The {@link NucleotideSubstitutionMatrix} to use for calculating scores.
     */
    public void evaluate(SubstitutionMatrix<G> matrix)
    {
        this.bestScoreIndex = this.scores.capacity()-1;
        for (int row = this.rowCount - 2; row >= 0; row--)
        {
            for (int col = this.colCount - 2; col >= 0; col--)
            {
                final G queryBase = this.querySeq.get(row);
                final G refBase = this.refSeq.get(col);
                final byte score = matrix.getScore(queryBase, refBase);
                final int cellScore = this.resolve(row, col, score);
                
                /*
                 * Check (and possibly update) the best score pointer.
                 */
                if (cellScore >= this.getScore(this.bestScoreIndex))
                {
                    this.bestScoreIndex = this.indexOf(row, col);
                }
            }
        }
    }
    
    /**
     * Fetches the {@link Coordinate} of the element with the highest score.  This identifies
     * the starting location of the best alignment in the matrix.
     * 
     * @return A {@link Coordinate} whose score is no loes than any other element.
     */
    public Coordinate getBestStart()
    {
        return new Coordinate(this.bestScoreIndex % this.colCount, this.bestScoreIndex / this.colCount);
    }
    
    
    /**
     * Resolves score and pathing data for a specific element in the matrix.  In order for this 
     * to work correctly, the elements immediately below (one value higher on the reference
     * axis) and immediately to the right (one value higher on the query axis).  The actual 
     * score assigned to this element may or may not be based on the supplied score.  If a gap
     * transition will result in a higher score, then that score will be used instead.
     * 
     * @param row The row index (on the reference axis).
     * @param col The column index (on the query axis).
     * @param score The score assigned to the sequence pairing by the 
     * {@link NucleotideSubstitutionMatrix}.
     * @return The final score assigned to this element.
     */
    public int resolve(int row, int col, byte score)
    {
        final int index = this.indexOf(row, col);
        
        final int vertScore = this.gapPenalty + this.getScore(this.indexOf(row + 1, col));
        final int horzScore = this.gapPenalty + this.getScore(row, col + 1);
        final int diagScore = score + this.getScore(row + 1, col + 1);

        /*
         * Store the decisions for later resolution
         */
        int cellScore = 0;
        int pathDirection = ScoringMatrix.PATH_TERM;
        
        if (diagScore >= horzScore && diagScore >= vertScore)
        {
            cellScore = diagScore;
            pathDirection = ScoringMatrix.PATH_DIAG;
        }
        else if (horzScore >= vertScore)
        {
            cellScore = horzScore;
            pathDirection = ScoringMatrix.PATH_HORZ;
        }
        else
        {
            cellScore = vertScore;
            pathDirection = ScoringMatrix.PATH_VERT;
        }
        
        /*
         * Resolve the decisions
         */
        this.setScore(index, cellScore);
        this.setPath(index, pathDirection);
        
        /*
         * Return the score assigned to the cell.
         *    This is important as the resolution method will use it to help identify the 
         *    highest-scoring cell in the matrix.
         */
        return cellScore;
    }
    
    /**
     * Prints the current state of this scoring matrix.  For now, this just prints the scores
     * for every element.
     * 
     * @param out The {@link PrintStream} to print to.
     */
    public void printState(PrintStream out)
    {
        out.print("   | ");
        for(int col = 0; col < this.colCount; col++)
        {
            out.printf("%3d ", col);
        }
        out.println();
        out.print("---+-");
        for(int col = 0; col < this.colCount; col++)
        {
            out.printf("----", col);
        }
        out.println();
        
        for(int row = 0; row < this.rowCount; row++)
        {
            out.printf("%02d | ", row);
            for(int col = 0; col < this.colCount; col++)
            {
                out.printf("%03d ", this.getScore(row, col));
            }
            out.println();
        }
    }
}
