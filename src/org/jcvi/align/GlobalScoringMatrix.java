/*
 * Created on Nov 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

import java.io.PrintStream;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.util.ArrayIterable;

public class GlobalScoringMatrix<G extends Glyph> implements ScoringMatrix<G>{

    /** The score to record on the terminating edges (far edges) of the matrix. */
    private static final int SCORE_TERMINATOR = 0;
    
    /** The integer mask for the score portion of the element. */
    private static final int SCORE_MASK = 0x0FFFFFFF;
    /** The integer mask for the path portion of the element. */
    private static final int PATH_MASK  = 0x70000000;
    /** The bit offset to the path portion of the element.*/
    private static final int PATH_OFFSET = 28;

    
    /** The path code for the end of an alignment. */
    public static final int PATH_TERM = 0;
    /** The path code for a horizontal (reference gap) transition. */
    public static final int PATH_HORZ = 1;
    /** The path code for a vertical (query gap) transition. */
    public static final int PATH_VERT = 2;
    /** The path code for a diagonal (sequence similarity) transition. */
    public static final int PATH_DIAG = 3;
    
    /** The scoring data, as a linear array of integers. */
    private final ScoringMatrixElement[] scores;
    /** The number of rows in the matrix. */
    private final int rowCount;
    /** The number of columns in the matrix. */
    private final int colCount;
    /** The score "cost" for transitioning via gap. */
    private final GapPenalty gapPenalty;
    
    /** The query sequence. */
    private final EncodedGlyphs<G> refSeq;
    /** The reference sequence. */
    private final EncodedGlyphs<G> querySeq;
    
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
    public GlobalScoringMatrix(EncodedGlyphs<G> refSequence, EncodedGlyphs<G> querySequence, GapPenalty gapPenalty)
    {
        
        this.refSeq = refSequence;
        this.querySeq = querySequence;
        this.rowCount = (int)refSequence.getLength();
        this.colCount = (int)querySequence.getLength();
        this.gapPenalty = gapPenalty;
        this.scores = new ScoringMatrixElement[this.rowCount * this.colCount];
                
        this.init(this.rowCount, this.colCount,this.gapPenalty);
    }
    @Override
    public int getNumberOfRows(){
        return rowCount;
    }
    @Override
    public int getNumberOfColumns(){
        return colCount;
    }
    /**
     * Initialize the matrix, setting up default values and terminating the edges of the matrix.
     */
    protected void init(int rowCount, int colCount,GapPenalty gapPenalty)
    {
        /*
         * Fill with invalid data
         */
        
        
        int penalty = gapPenalty.getNextGapPenalty();
        this.setScore(0, 0, 0, Path.STOP);
        /*
         * Terminate the last row
         */
        for (int i = 1; i < colCount; i++)
        {
            this.setScore(0, i, i*penalty, Path.STOP);
        }
        
        /*
         * Terminate the last column
         */
        for (int i = 0; i < rowCount; i++)
        {
            this.setScore(i, 0, i*penalty, Path.STOP);
        }
        gapPenalty.reset();
        
        printState(System.out);
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
    protected void setScore(int index, int score, Path path)
    {
       // final int path = this.getPath(index);
       // final int value = (Math.max(0, score) & LocalScoringMatrix.SCORE_MASK) | (path << LocalScoringMatrix.PATH_OFFSET);
        
        this.scores[index]= createScoringMatrixElement(path,score);
    }
    
    protected ScoringMatrixElement createScoringMatrixElement(Path path, int score){
        return new DefaultScoringMatrixElement(path, score);
    }
    
    /**
     * Sets the score for the element at the given coordinates.
     * 
     * @param indexA The coordinate on the reference axis.
     * @param indexB The coordinate on the query axis.
     * @param score The score to set.
     */
    protected void setScore(int indexA, int indexB, int score, Path path)
    {
        this.setScore(this.indexOf(indexA, indexB), score,path);
    }

    
    /**
     * Fetch the score for the element at the given linear index.
     * 
     * @param index The element index.
     * @return The matrix cell score.
     */
    protected int getScore(int index)
    {
        final ScoringMatrixElement scoringMatrixElement = this.scores[index];
        return scoringMatrixElement==null?Integer.MIN_VALUE:scoringMatrixElement.getScore();
    }
    
    /**
     * Fetch the score for the element at the given coordinates.
     * 
     * @param indexA The row coordinate (on the reference axis).
     * @param indexB The column coordinate (on the query axis).
     * @return The matrix cell score.
     */
    @Override
    public int getScore(int indexA, int indexB)
    {
        return this.getScore(this.indexOf(indexA, indexB));
    }
    
    /**
     * Fetch the score for the element at the given {@link Coordinate}.
     * 
     * @param c The {@link Coordinate} which addresses the element.
     * @return The matrix cell score.
     */
    public int getScore(Coordinate c)
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
    protected Path getPath(int index)
    {
        return this.scores[index].getDirection();
    }
    
    /**
     * Fetch the pathing hint for the element at the given coordinates.
     * 
     * @param indexA The row index (on the reference axis)
     * @param indexB The column index (on the query axis)
     * @return The pathing hint for this cell.
     */
    protected Path getPath(int indexA, int indexB)
    {
        return this.getPath(this.indexOf(indexA, indexB));
    }
    
    /**
     * Fetch the pathing hint for the element for the element at the given {@link Coordinate}.
     * 
     * @param c The {@link Coordinate} which addresses the element.
     * @return The pathing hint for this cell.
     */
    @Override
    public Path getPath(Coordinate c)
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
    @Override
    public void evaluate(AlignmentMatrix<G> matrix)
    {

       for(int i=1; i< getNumberOfRows(); i++){
           for(int j=1; j<getNumberOfColumns(); j++){
               final G queryBase = this.refSeq.get(i);
               final G refBase = this.querySeq.get(j);
               final int score = matrix.getScore(queryBase, refBase);
               this.resolve(i, j, score);
               this.printState(System.out);
           }
       }
       
    }

    protected Iterable<Integer> createRowIterable(int rowCount) {
        Integer[] array = new Integer[rowCount-1];
        for (int i=0; i<this.rowCount - 1; i++){
            array[i] = rowCount-2-i;
        }
        return new ArrayIterable(array);
    }
    protected Iterable<Integer> createColumnIterable(int colCount) {
        Integer[] array = new Integer[colCount-1];
        for (int i=0; i<this.colCount - 1; i++){
            array[i] = colCount-2-i;
        }
        return new ArrayIterable(array);
    }
    protected int getInitialBestScoreIndex(ScoringMatrixElement[] scores) {
        return 0;
    }
    protected boolean isNewBestScore(final int cellScore, final int bestScore) {
        return cellScore >= bestScore;
    }
    
    /**
     * Fetches the {@link Coordinate} of the element with the highest score.  This identifies
     * the starting location of the best alignment in the matrix.
     * 
     * @return A {@link Coordinate} whose score is no loes than any other element.
     */
    @Override
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
    public int resolve(int row, int col, int score)
    {
        final int index = this.indexOf(row, col);
        int currentGapPenalty = gapPenalty.getNextGapPenalty();
        final int unpenalizedVertScore = this.getScore(this.indexOf(row - 1, col));
        final int vertScore = currentGapPenalty + unpenalizedVertScore;
        final int unpenalizedHorzScore = this.getScore(row, col - 1);
        final int horzScore = currentGapPenalty + unpenalizedHorzScore;
        final int diagScore = score + this.getScore(row - 1, col - 1);

        /*
         * Store the decisions for later resolution
         */
        int cellScore = 0;
        Path path = Path.STOP;
        
        if (isNewBestScore(diagScore, horzScore) && isNewBestScore(diagScore, vertScore))
        {
            cellScore = diagScore;
            path = Path.DIAGNOL;
            gapPenalty.reset();
        }
        else if (isNewBestScore(horzScore, vertScore))
        {
            cellScore = horzScore;
            path = Path.HORIZONTAL;
        }
        else
        {
            cellScore = vertScore;
            path = Path.VERTICAL;
        }
        
        /*
         * Resolve the decisions
         */
        this.setScore(index, cellScore, path);
    //    this.printState(System.out);
      //  System.out.println(cellScore);
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
            out.printf("%3s ", querySeq.get(col));
        }
        out.println();
        out.print("---+-");
        for(int col = 0; col < this.colCount; col++)
        {
            out.printf("----");
        }
        out.println();
        
        for(int row = 0; row < this.rowCount; row++)
        {
            out.printf("%2s | ", refSeq.get(row));
            for(int col = 0; col < this.colCount; col++)
            {
                out.printf("%03d ", this.getScore(row, col));
            }
            out.println();
        }
    }
}
