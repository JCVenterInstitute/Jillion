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

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.align.SequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.util.MathUtil;

/**
 * @author dkatzel
 *
 *
 */
abstract class AbstractSmithWatermanAligner<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> {

	private static final byte TERMINAL = 0;
	private static final byte HORIZONTAL = 1;
	private static final byte VERTICAL = 2;
	private static final byte DIAGNOL = 3;
	
	private final byte[][] traceback;
	private final float[][] scores;
	private final A alignment;
	
	public AbstractSmithWatermanAligner(Sequence<R> query, Sequence<R> subject, ScoringMatrix<R> matrix, float openGapPenalty, float extendGapPenalty){
		
		traceback = new byte[(int)query.getLength()+1][(int)subject.getLength()+1];
		scores = new float[(int)query.getLength()+1][(int)subject.getLength()+1];
		for(int i=0; i<traceback[0].length; i++){
			traceback[0][i]=TERMINAL;
		}
		for(int i=0; i<traceback.length; i++){
			traceback[i][0]=TERMINAL;
		}
		byte[] seq1Bytes = convertToByteArray(query);
		byte[] seq2Bytes = convertToByteArray(subject);
		
		int lengthOfSeq1 = seq1Bytes.length;
		int lengthOfSeq2 = seq2Bytes.length;
		scores[0][0]=0F;
		short[][] sizesOfHorizonalGaps = new short[lengthOfSeq1][lengthOfSeq2];
		short[][] sizesOfVerticalGaps = new short[lengthOfSeq1][lengthOfSeq2];
		for(int i=0; i< seq1Bytes.length; i++){
			Arrays.fill(sizesOfHorizonalGaps[i], (short)1);
			Arrays.fill(sizesOfVerticalGaps[i], (short)1);
		}		
		//only need to keep array of vertical accumulated gap
		//penalties since we are populating horizontally  we can just
		//keep a since float for current horizontal penalty 
		float[] verticalGapPenaltiesSoFar = new float[lengthOfSeq2];		
		Arrays.fill(verticalGapPenaltiesSoFar, Float.NEGATIVE_INFINITY);
		
		float[] bestPathSoFar = new float[lengthOfSeq2];
		Arrays.fill(bestPathSoFar, 0F);
		
		CurrentStartPoint currentStartPoint = new CurrentStartPoint();
		for(int i=0; i<lengthOfSeq1; i++){
			float horrizontalGapPenalty = Float.NEGATIVE_INFINITY;
			float diagnol = bestPathSoFar[0];
			for(int j=0; j< lengthOfSeq2; j++){
				float verticalGapExtensionScore = verticalGapPenaltiesSoFar[j] - extendGapPenalty;
				float verticalOpenGapScore = bestPathSoFar[j] - openGapPenalty;
				
				if(verticalGapExtensionScore > verticalOpenGapScore){
					verticalGapPenaltiesSoFar[j] = verticalGapExtensionScore;
					sizesOfVerticalGaps[i][j] = (short)(sizesOfVerticalGaps[i][j-1] +1);
				}else{
					verticalGapPenaltiesSoFar[j] = verticalOpenGapScore;
				}
				
				float horizontalGapExtensionScore = horrizontalGapPenalty - extendGapPenalty;
				float horizontalGapOpenScore = horrizontalGapPenalty - openGapPenalty;
				
				if(horizontalGapExtensionScore > horizontalGapOpenScore){
					horrizontalGapPenalty = horizontalGapExtensionScore;
					sizesOfHorizonalGaps[i][j] = (short)(sizesOfHorizonalGaps[i][j-1]+1);
				}else{
					horrizontalGapPenalty = horizontalGapOpenScore;
				}
				
				//need to do -1s because 0 offset in matrix is filled with stops
				//and actual values start at offset 1
				float alignmentScore = diagnol + matrix.getScore(seq1Bytes[i],seq2Bytes[j]);
				diagnol = bestPathSoFar[j];
				bestPathSoFar[j] = MathUtil.maxOf(alignmentScore, horrizontalGapPenalty, verticalGapPenaltiesSoFar[j], 0F);
				//can't switch on float... so ugly if/else block below
				if(bestPathSoFar[j] ==0){
					traceback[i+1][j+1] = TERMINAL;
				}else if (bestPathSoFar[j] == alignmentScore){
					traceback[i+1][j+1] = DIAGNOL;
				}else if (bestPathSoFar[j] == horrizontalGapPenalty){
					traceback[i+1][j+1] = HORIZONTAL;
				}else{
					traceback[i+1][j+1] = VERTICAL;
				}
				
				currentStartPoint = currentStartPoint.updateIfWorseThan(i, j, bestPathSoFar[j]);
			}
		}
		//now do trace back
		boolean done=false;
		int x=currentStartPoint.getX();
		int y = currentStartPoint.getY();
		
		SequenceAlignmentBuilder<R,S,A> alignmentBuilder = createSequenceAlignmentBuilder();
		R gap = getGap();
		while(!done){
			switch(traceback[x+1][y+1]){
			case VERTICAL :
				{
					short numberOfGaps = sizesOfVerticalGaps[x][y];
					for(int i=0; i< numberOfGaps; i++){
						alignmentBuilder.addGap(getResidueFromOrdinal(seq1Bytes[x]), gap);
						x--;
					}
				}
					break;
					
			case HORIZONTAL :
				{
					short numberOfGaps = sizesOfHorizonalGaps[x][y];
					for(int i=0; i< numberOfGaps; i++){
						alignmentBuilder.addGap(gap,getResidueFromOrdinal(seq2Bytes[y]));
						
						y--;
					}
				}
				break;
			case DIAGNOL:
				boolean isMatch = seq1Bytes[x] == seq2Bytes[y];
				if(isMatch){
					alignmentBuilder.addMatch(getResidueFromOrdinal(seq1Bytes[x]));
				}else{
					alignmentBuilder.addMismatch(getResidueFromOrdinal(seq1Bytes[x]), getResidueFromOrdinal(seq2Bytes[y]));
				}
				
				x--;
				y--;
				break;
			case TERMINAL:
				done = true;
				break;
			}
		}
		alignmentBuilder.reverse();
		alignment = alignmentBuilder.build();
		
	}

	public A getSequenceAlignment(){
		return alignment;
	}
	/**
	 * Get the {@link Residue} instance that represents a gap.
	 * @return a {@link Residue}; never null.
	 */
	protected abstract R getGap();
	
	protected abstract R getResidueFromOrdinal(int ordinal);
	
	protected abstract SequenceAlignmentBuilder<R, S,A> createSequenceAlignmentBuilder();
	
	private byte[] convertToByteArray(Sequence<R> sequence) {
		ByteBuffer buf = ByteBuffer.allocate((int)sequence.getLength());
		for(R residue : sequence){
			buf.put(residue.getOrdinalAsByte());
		}
		buf.flip();
		return buf.array();
	}
	
	/**
	 * {@code CurrentStartPoint} is a class
	 * that points to the current location
	 * in our traceback matrix of where the local
	 * alignment should start.  This class
	 * also keeps track of the current best score
	 * so we know if we should update our pointer
	 * to point to an even better starting location.
	 * @author dkatzel
	 *
	 */
	private static class CurrentStartPoint{
		private final int x,y;
		private final float score;
		public CurrentStartPoint(){
			this(0,0,Float.NEGATIVE_INFINITY);
		}
		private CurrentStartPoint(int x, int y, float score) {
			this.x = x;
			this.y = y;
			this.score = score;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public float getScore() {
			return score;
		}
		
		public CurrentStartPoint updateIfWorseThan(int x, int y, float score){
			if(score > this.score){
				return new CurrentStartPoint(x,y,score);
			}
			return this;
		}
		
	}
}
