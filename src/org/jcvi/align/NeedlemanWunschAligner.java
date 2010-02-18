/*
 * Created on Nov 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * A <code>NeedlemanWunschAligner</code> is a simple implementation of an {@link Aligner} using an
 * unoptimized <a href="http://en.wikipedia.org/wiki/Needleman_wunsch">Needleman-Wunsch</a>
 * algorithm.
 * @author dkatzel
 *
 *
 */
public class NeedlemanWunschAligner extends SmithWatermanAligner{

    public NeedlemanWunschAligner(AlignmentMatrix<NucleotideGlyph> matrix, GapPenalty gapPenalty) {
        super(matrix, gapPenalty);
    }

    @Override
    protected Coordinate getAlignmentStartCoordinate(ScoringMatrix score) {
       int bestRow=0;
       int bestScore =score.getScore(0,0);
       for(int i=1; i<score.getNumberOfColumns(); i++){
           int currentScore = score.getScore(0,i);
           if(currentScore > bestScore){
               bestRow=i;
               bestScore = currentScore;
           }
       }
        return new Coordinate(0,bestRow);
    }

    @Override
    protected ScoringMatrix<NucleotideGlyph> createScoringMatrixFor(
            EncodedGlyphs<NucleotideGlyph> referenceSequence,
            EncodedGlyphs<NucleotideGlyph> querySequence, GapPenalty gapPenalty) {
        System.out.println("making global matrix");
        return new GlobalScoringMatrix<NucleotideGlyph>(referenceSequence, querySequence, gapPenalty);
    }

    @Override
    protected boolean stillTraversing(
            EncodedGlyphs<NucleotideGlyph> referenceSequence,
            EncodedGlyphs<NucleotideGlyph> querySequence, Coordinate cursor) {
        
        return cursor.y >0 && cursor.x >0;
    }
    @Override
    public Alignment alignSequence(EncodedGlyphs<NucleotideGlyph> referenceSequence, EncodedGlyphs<NucleotideGlyph> querySequence){
        
        Coordinate start = new Coordinate((int)querySequence.getLength()-1, (int)referenceSequence.getLength()-1);
        
        AlignmentFactory alignment = new AlignmentFactory();
        alignment.setQueryLength((int)querySequence.getLength());
        alignment.setReferenceLength((int)referenceSequence.getLength());
        
        int alignmentLength = 0;
        int identity = 0;
        final ScoringMatrix<NucleotideGlyph> score =new GlobalScoringMatrix<NucleotideGlyph>(
                referenceSequence, querySequence, this.getGapPenalty());

        /*
         * Evaluate all of the cells.
         */
        score.evaluate(this.getMatrix());
        Coordinate cursor = start;
        Coordinate previousCursor = cursor;
        while(stillTraversing(referenceSequence, querySequence, cursor)){
            alignmentLength++;
            previousCursor = cursor;
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
                    
                    cursor = cursor.translate(-1, -1);

                    break;

                case HORIZONTAL:
                    
                    alignment.addAbsoluteReferenceGap(cursor.y);
                    cursor = cursor.translate(-1, 0);

                    break;

                case VERTICAL:
                    
                    alignment.addAbsoluteQueryGap(cursor.x);
                    cursor = cursor.translate(0, -1);
                    
                    break;
            }
            
        }
        
        /*
         * Store the identity and score
         */
        alignment.setIdentity(identity / (double)alignmentLength);
        alignment.setScore(score.getScore(start.x,start.y));
        
        
        alignment.setQueryEnd(start.x+1);
        alignment.setReferenceEnd(start.y+1);
        alignment.setQueryBegin(previousCursor.x);
        alignment.setReferenceBegin(previousCursor.y);
        
        /*
         * Builld and return a concrete alignment.
         */
        return alignment.build();
    }
}
