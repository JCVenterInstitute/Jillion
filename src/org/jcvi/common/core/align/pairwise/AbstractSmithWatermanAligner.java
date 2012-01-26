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

package org.jcvi.common.core.align.pairwise;


import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.util.MathUtil;

/**
 * @author dkatzel
 *
 *
 */
abstract class AbstractSmithWatermanAligner<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>, P extends PairwiseSequenceAlignment<R, S>> extends AbstractPairwiseAligner<R, S, A, P>{

	protected AbstractSmithWatermanAligner(Sequence<R> query, Sequence<R> subject,
			ScoringMatrix<R> matrix, float openGapPenalty,
			float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty);
	}
	/**
	 * All initial gap scores are set to {@literal 0}.
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	protected float[] getInitialGapScores(int length, float openGapPenalty,
			float extendGapPenalty) {
		return new float[length];
	}
	
	/**
	 * Since Smith-Waterman is a local alignment all 
	 * values in the initial row should terminate the alignment
	 * if the query and subject don't align at that base.
	 * 
	 * </p>
	 * {@inheritDoc}
	 * @return TracebackDirection#TERMINAL
	 */
	@Override
	protected TracebackDirection getInitialRowTracebackValue() {
		return TracebackDirection.TERMINAL;
	}
	/**
	 * Since Smith-Waterman is a local alignment all 
	 * values in the initial column should terminate the alignment
	 * if the query and subject don't align at that base.
	 * </p>
	 * {@inheritDoc}
	 * @return TracebackDirection#TERMINAL
	 */
	@Override
	protected TracebackDirection getInitialColTracebackValue() {
		return TracebackDirection.TERMINAL;
	}
	@Override
	protected BestWalkBack computeBestWalkBack(float alignmentScore,
			float horrizontalGapPenalty, float verticalGapPenalty){
			float bestScore = MathUtil.maxOf(alignmentScore, horrizontalGapPenalty, verticalGapPenalty, 0F);
			final TracebackDirection dir;
			//can't switch on float... so ugly if/else block below
			if(bestScore ==0){
				dir = TracebackDirection.TERMINAL;
			}else if (bestScore == alignmentScore){
				dir = TracebackDirection.DIAGNOL;
			}else if (bestScore == horrizontalGapPenalty){
				dir = TracebackDirection.HORIZONTAL;
			}else{
				dir = TracebackDirection.VERTICAL;
			}
			return new BestWalkBack(bestScore, dir);
	}
	@Override
	protected StartPoint updateCurrentStartPoint(float currentBestScore,
			StartPoint currentStartPoint, int i, int j) {
		if(currentBestScore > currentStartPoint.getScore()){
			return new StartPoint(i, j, currentBestScore);
		}
		return currentStartPoint;
	}
	
	
}
