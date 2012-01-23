package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.util.MathUtil;

public abstract class AbstractNeedlemanWunschAligner <R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> extends AbstractPairwiseAligner<R, S, A>{

	public AbstractNeedlemanWunschAligner(Sequence<R> query,
			Sequence<R> subject, ScoringMatrix<R> matrix, float openGapPenalty,
			float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty);
	}

	@Override
	protected BestWalkBack computeBestWalkBack(float alignmentScore,
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
			return new BestWalkBack(bestScore, dir);
	}
	@Override
	protected CurrentStartPoint updateCurrentStartPoint(float currentBestScore,
			CurrentStartPoint currentStartPoint, int i, int j) {
		return new CurrentStartPoint(i, j, currentBestScore);
	}

	@Override
	protected float getInitialRowScore(float[] bestPathSoFar, int rowNumber,
			float openGapPenalty, float extendGapPenalty) {
		if(rowNumber==1){
			return 0;
		}
		return openGapPenalty + (rowNumber-1)*extendGapPenalty;
	}
	
	

}
