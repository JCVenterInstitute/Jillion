package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.util.MathUtil;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;
/**
 * {@code AbstractNeedlemanWunschAligner} 
 * implements methods in {@link AbstractPairwiseAligner} 
 * with  Needleman-Wunsch (with Gotoh improvements) specific implementations.
 * 
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} used in this aligner.
 * @param <S> the {@link Sequence} type input into this aligner.
 * @param <A> the {@link SequenceAlignment} type returned by this aligner.
 * @param <P> the {@link PairwiseSequenceAlignment} type returned by this aligner.
 */
abstract class AbstractNeedlemanWunschAligner <R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>, P extends PairwiseSequenceAlignment<R, S>> extends AbstractPairwiseAligner<R, S, A, P>{

	protected AbstractNeedlemanWunschAligner(Sequence<R> query,
			Sequence<R> subject, ScoringMatrix<R> matrix, float openGapPenalty,
			float extendGapPenalty,ResiduePairwiseStrategy<R,S,A,P> pairwiseStrategy) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty, pairwiseStrategy);
	}
	/**
	 * The initial gap scores of NeedlemanWunsch are the values
	 * returned by the affine gap penalty.
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	protected float[] getInitialGapScores(int length, float openGapPenalty,
			float extendGapPenalty) {
		float[] array = new float[length];
		
		array[0]=0;
		array[1]=openGapPenalty;
		float currentScore=openGapPenalty;
		for(int i=2; i< length; i++){
			currentScore +=extendGapPenalty;
			array[i] = currentScore;
		}
		return array;
	}

	/**
	 * Since Needleman-Wunsch is a global alignment
	 * we need to keep gapping until we get to the beginning
	 * of the sequence so keep tracing horizontally.
	 * @return TracebackDirection#HORIZONTAL
	 * {@inheritDoc}
	 */
	@Override
	protected TracebackDirection getInitialRowTracebackValue() {
		return TracebackDirection.HORIZONTAL;
	}
	/**
	 * Since Needleman-Wunsch is a global alignment
	 * we need to keep gapping until we get to the beginning
	 * of the sequence so keep tracing horizontally.
	 * @return TracebackDirection#VERTICAL
	 * {@inheritDoc}
	 */
	@Override
	protected TracebackDirection getInitialColTracebackValue() {
		return TracebackDirection.VERTICAL;
	}
	/**
	 * Returns a {@link WalkBack} using the max of the 3 input values.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	protected WalkBack computeBestWalkBack(float alignmentScore,
			float horrizontalGapPenalty, float verticalGapPenalty){
			float bestScore = MathUtil.maxOf(alignmentScore, horrizontalGapPenalty, verticalGapPenalty);
			final TracebackDirection dir;
			//can't switch on float... so ugly if/else block below
			if (bestScore == alignmentScore){
				dir = TracebackDirection.DIAGNOL;
			}else if (bestScore == horrizontalGapPenalty){
				dir = TracebackDirection.HORIZONTAL;
			}else{
				dir = TracebackDirection.VERTICAL;
			}
			return new WalkBack(bestScore, dir);
	}
	/**
	 * Always update the CurrentStartPointer to the given values.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	protected StartPoint updateCurrentStartPoint(float currentBestScore,
			StartPoint currentStartPoint, int i, int j) {
		return new StartPoint(i, j, currentBestScore);
	}
	
	

}
