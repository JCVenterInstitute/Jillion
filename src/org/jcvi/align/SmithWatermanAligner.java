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
/**
 * OldAligner.java Created: Aug 10, 2009 - 11:16:37 AM (jsitz) Copyright 2009 J.
 * Craig Venter Institute
 */
package org.jcvi.align;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;


/**
 * A <code>SmithWatermanAligner</code> is a simple implementation of an {@link Aligner} using an
 * unoptimized <a href="http://en.wikipedia.org/wiki/Smith-Waterman_algorithm">Smith Waterman</a>
 * algorithm.
 * 
 * @author jsitz@jcvi.org
 */
public class SmithWatermanAligner implements Aligner<NucleotideGlyph>
{
    /** The default gap score. */
    private static final GapPenalty DEFAULT_GAP_PENALTY = new ConstantGapPenalty(-3);

    /** The score for a gap in the alignment. */
    private final GapPenalty gapPenalty;
    
    /** The substitution matrix to use when scoring matches. */
    private final AlignmentMatrix<NucleotideGlyph> matrix;

    /**
     * Creates a new <code>SmithWatermanAligner</code>.
     * 
     * @param matrix The {@link SubstitutionMatrix} to use for scoring.
     * @param gapScore The gapping score (penalty) to use.
     */
    public SmithWatermanAligner(AlignmentMatrix<NucleotideGlyph> matrix, GapPenalty gapPenalty)
    {
        super();
        
        this.matrix = matrix;
        this.gapPenalty = gapPenalty;
    }

    public AlignmentMatrix<NucleotideGlyph> getMatrix() {
        return matrix;
    }

    public GapPenalty getGapPenalty() {
        return gapPenalty;
    }

    /**
     * Creates a new <code>SmithWatermanAligner</code> with a default gap penalty 
     * ({@value #DEFAULT_GAP_PENALTY}).
     * 
     * @param matrix The {@link SubstitutionMatrix} to use for scoring.
     */
    public SmithWatermanAligner(SubstitutionMatrix matrix)
    {
        this(matrix, SmithWatermanAligner.DEFAULT_GAP_PENALTY);
    }

    /* (non-Javadoc)
     * @see org.jcvi.align.Aligner#alignSequence(java.lang.CharSequence)
     */
    public Alignment alignSequence(EncodedGlyphs<NucleotideGlyph> referenceSequence, EncodedGlyphs<NucleotideGlyph> querySequence)
    {
        final AlignmentFactory alignment = new AlignmentFactory();
        final ScoringMatrix<NucleotideGlyph> score = createScoringMatrixFor(
                referenceSequence, querySequence, this.gapPenalty);

        /*
         * Set the sequence lengths
         */
        alignment.setQueryLength((int)querySequence.getLength());
        alignment.setReferenceLength((int)referenceSequence.getLength());
        
        /*
         * Evaluate all of the cells.
         */
        score.evaluate(this.matrix);

        final Coordinate start = getAlignmentStartCoordinate(score);
        
        /*
         * Set the alignment start coordinates.
         *    NOTE: We offset by one because we are using 1-indexed residue-based addressing.
         */
        alignment.setQueryBegin(start.x + 1);
        alignment.setReferenceBegin(start.y + 1);
        
        /*
         * Initialize the cursor we'll use to follow the alignment path.
         */
        Coordinate cursor = start;
        
        /*
         * Initialize accumulators for the identity calculation.
         */
        int alignmentLength = 0;
        int identity = 0;

        // traverse the optimal path and build the alignment strings
        while (stillTraversing(referenceSequence, querySequence, cursor))
        {
            /*
             * Bump the alignment length counter.
             */
            alignmentLength++;
            
            /*
             * Follow the path recorded in the score matrix.
             */
            switch (score.getPath(cursor))
            {
                case DIAGNOL:
                    
                    /*
                     * Check for identity.
                     */
                    final NucleotideGlyph refBase = referenceSequence.get(cursor.y);
                    final NucleotideGlyph queryBase = querySequence.get(cursor.x);
                    if (refBase == queryBase)
                    {
                        identity++;
                    }
                    
                    cursor = cursor.translate(1, 1);

                    break;

                case HORIZONTAL:
                    
                    alignment.addAbsoluteReferenceGap(cursor.y);
                    cursor = cursor.translate(1, 0);

                    break;

                case VERTICAL:
                    
                    alignment.addAbsoluteQueryGap(cursor.x);
                    cursor = cursor.translate(0, 1);
                    
                    break;
            }
        }
        
        /*
         * Store the identity and score
         */
        alignment.setIdentity(identity / (double)alignmentLength);
        alignment.setScore(score.getScore(start.x,start.y));
        
        /*
         * Update the alignment factory with the alignment stop points
         *     NOTE: This may feel like an off-by-one error since we're using the current 
         *     location of the cursor after it's reached the end of one of the alignment
         *     sequences.  However, we're recording 1-indexed, residue-based addresses, so
         *     the current values are correct when translated backward for the previous
         *     index and then forward for the 1-based index.
         */
        alignment.setQueryEnd(cursor.x);
        alignment.setReferenceEnd(cursor.y);
        
        /*
         * Builld and return a concrete alignment.
         */
        return alignment.build();
    }

    protected boolean stillTraversing(
            EncodedGlyphs<NucleotideGlyph> referenceSequence,
            EncodedGlyphs<NucleotideGlyph> querySequence, Coordinate cursor) {
        return cursor.y < referenceSequence.getLength() && cursor.x < querySequence.getLength();
    }

    protected ScoringMatrix<NucleotideGlyph> createScoringMatrixFor(
            EncodedGlyphs<NucleotideGlyph> referenceSequence,
            EncodedGlyphs<NucleotideGlyph> querySequence,
            GapPenalty gapPenalty) {
        return new LocalScoringMatrix<NucleotideGlyph>(referenceSequence, querySequence, gapPenalty);
    }

    protected Coordinate getAlignmentStartCoordinate(final ScoringMatrix score) {
        return score.getBestStart();
    }
}
