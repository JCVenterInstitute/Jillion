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
abstract class AbstractSmithWatermanAligner<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> extends AbstractPairwiseAligner<R, S, A>{

	protected AbstractSmithWatermanAligner(Sequence<R> query, Sequence<R> subject,
			ScoringMatrix<R> matrix, float openGapPenalty,
			float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty);
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
	protected CurrentStartPoint updateCurrentStartPoint(float currentBestScore,
			CurrentStartPoint currentStartPoint, int i, int j) {
		if(currentBestScore > currentStartPoint.getScore()){
			return new CurrentStartPoint(i, j, currentBestScore);
		}
		return currentStartPoint;
	}
	@Override
	protected float getInitialRowScore(float[] bestPathSoFar, int rowNumber, float openGapPenalty,
			float extendGapPenalty) {
		return bestPathSoFar[0];
	}
	
	
}
