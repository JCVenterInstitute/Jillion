/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align.pairwise;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion_experimental.align.SequenceAlignment;
import org.jcvi.jillion_experimental.align.SequenceAlignmentBuilder;
/**
 * {@code AbstractPairwiseAligner} is an abstract 
 * implementation of a dynamic programming
 * pairwise alignment algorithm for any Residue
 * type.
 * 
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} used in this aligner.
 * @param <S> the {@link Sequence} type input into this aligner.
 * @param <A> the {@link SequenceAlignment} type returned by this aligner.
 * @param <P> the {@link PairwiseSequenceAlignment} type returned by this aligner.
 */
abstract class AbstractPairwiseAligner <R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>, P extends PairwiseSequenceAlignment<R, S>> {
	
	/**
	 * The matrix which stores all of our traceback
	 * values. 
	 */
	private final TraceBackMatrix traceback;
	/**
	 * The match scores of the current row computed so far
	 * and the full previous row.
	 */
	private final float[][] scoreCache;
	/**
	 * {@link BitSet} to keep track of if at our
	 * current position we are in a vertical gap.
	 * This is used to compute affine gap
	 * penalties if our scoring changes
	 * if we are already inside of a gap
	 * or not.  
	 * </p>
	 * {@link BitSet} is used
	 * to save memory over a {@literal boolean[]}
	 * since BitSets actually take up 1 bit
	 * per element vs 1 byte per element in a {@literal boolean[]}.
	 */
	private final BitSet[] inAVerticalGapCache;
	/**
	 * The final alignment produced.
	 */
	private final P alignment;
	private final ResiduePairwiseStrategy<R,S,A,P> pairwiseStrategy;
	/**
	 * The previous row in our cache.
	 */
	private static final int PREVIOUS_ROW=0;
	/**
	 * The current row in our cache.
	 */
	private static final int CURRENT_ROW=1;
	
	

	
	
	protected AbstractPairwiseAligner(Sequence<R> query, Sequence<R> subject,
			ScoringMatrix<R> matrix, float openGapPenalty, float extendGapPenalty,
			ResiduePairwiseStrategy<R,S,A,P> pairwiseStrategy){
		checkNotNull(query,subject,matrix);
		this.pairwiseStrategy = pairwiseStrategy;
		traceback = new TraceBackMatrix((int)query.getLength()+1,(int)subject.getLength()+1);
		
		scoreCache = new float[2][(int)subject.getLength()+1];
		inAVerticalGapCache = new BitSet[2];
		initializeFields(openGapPenalty, extendGapPenalty);
		byte[] seq1Bytes = convertToByteArray(query);
		byte[] seq2Bytes = convertToByteArray(subject);
		
		StartPoint currentStartPoint = populateTraceback(matrix,
				openGapPenalty, extendGapPenalty, seq1Bytes, seq2Bytes);
		//now do trace back
		alignment = traceBack(seq1Bytes, seq2Bytes, currentStartPoint);
		
	}
	private void checkNotNull(Sequence<R> query, Sequence<R> subject,
			ScoringMatrix<R> matrix) {
		if(query ==null){
			throw new NullPointerException("query sequence can not be null");
		}
		if(subject ==null){
			throw new NullPointerException("subject sequence can not be null");
		}
		if(matrix ==null){
			throw new NullPointerException("scoring matrix can not be null");
		}
		
	}
	private StartPoint populateTraceback(ScoringMatrix<R> matrix,
			float openGapPenalty, float extendGapPenalty, byte[] seq1Bytes,
			byte[] seq2Bytes) {
		int lengthOfSeq1 = seq1Bytes.length;
		int lengthOfSeq2 = seq2Bytes.length;
		
		
		
		//only need to keep array of vertical accumulated gap
		//penalties since we are populating horizontally  we can just
		//keep a float for current horizontal penalty 
		float[] verticalGapPenaltiesSoFar = new float[lengthOfSeq2+1];		
		Arrays.fill(verticalGapPenaltiesSoFar, Float.NEGATIVE_INFINITY);
		List<R> residuesByOrdinal = pairwiseStrategy.getResidueList();
		StartPoint currentStartPoint = new StartPoint();
		for(int i=1; i<=lengthOfSeq1; i++){

			
			float cumulativeHorizontalGapPenalty=Float.NEGATIVE_INFINITY;
			BitSet inAHorizontalGap = new BitSet(lengthOfSeq2+1);
			for(int j=1; j<= lengthOfSeq2; j++){
				float diagnol = scoreCache[PREVIOUS_ROW][j-1];
				float verticalGapExtensionScore = inAVerticalGapCache[PREVIOUS_ROW].get(j) 
							? scoreCache[PREVIOUS_ROW][j] + extendGapPenalty 
							: Float.NEGATIVE_INFINITY	;
				float verticalOpenGapScore = scoreCache[PREVIOUS_ROW][j] + openGapPenalty;
				if(verticalGapExtensionScore > verticalOpenGapScore){
					verticalGapPenaltiesSoFar[j] = verticalGapExtensionScore;
				}else{
					verticalGapPenaltiesSoFar[j] = verticalOpenGapScore;
				}
				
				float horizontalGapExtensionScore =  inAHorizontalGap.get(j-1) 
						? scoreCache[CURRENT_ROW][j-1]+ extendGapPenalty
						: Float.NEGATIVE_INFINITY;
				float horizontalGapOpenScore = scoreCache[CURRENT_ROW][j-1] + openGapPenalty;
				if(horizontalGapExtensionScore >= horizontalGapOpenScore){
					cumulativeHorizontalGapPenalty = horizontalGapExtensionScore;
					
				}else{
					cumulativeHorizontalGapPenalty = horizontalGapOpenScore;
				}
				
				//need to do -1s because 0 offset in matrix is filled with stops
				//and actual values start at offset 1
				float alignmentScore = diagnol + matrix.getScore(
						residuesByOrdinal.get(seq1Bytes[i-1]),
						residuesByOrdinal.get(seq2Bytes[j-1]));
				
				
				WalkBack bestWalkBack = computeBestWalkBack(alignmentScore, cumulativeHorizontalGapPenalty, verticalGapPenaltiesSoFar[j]);
				scoreCache[CURRENT_ROW][j] = bestWalkBack.getBestScore();
				//some implementations might
				//need to update the currentStartPoint even if it's not
				//the best path so far
				//so give subclasses the option to update.
				currentStartPoint = updateCurrentStartPoint(bestWalkBack.getBestScore(), currentStartPoint, i, j);
				if(currentStartPoint ==null){
					throw new NullPointerException("current start point can not be set to null");
				}
				switch(bestWalkBack.getTracebackDirection()){
					case HORIZONTAL: inAHorizontalGap.set(j,true);
									break;
					case VERTICAL : inAVerticalGapCache[CURRENT_ROW].set(j,true);
									break;
					case DIAGNOL: 	inAHorizontalGap.set(j,false);
									inAVerticalGapCache[CURRENT_ROW].set(j,false);
									break;
					default:
								break;
						
				}
				traceback.set(i,j,bestWalkBack.getTracebackDirection());
				diagnol = traceback.get(i-1,j).ordinal();
				
				//printTraceBack();
			}
			updateCaches();
		}
		return currentStartPoint;
	}
	/**
	 * Initialize the values of the initial traceback cells, scorecache and inVerticalGapCache.
	 * Some of these values are populated using returned values from
	 * {@link #getInitialColTracebackValue()}, 
	 * {@link #getInitialRowTracebackValue()}, 
	 * {@link #getInitialGapScores(int, float, float)}
	 * @param openGapPenalty
	 * @param extendGapPenalty
	 * @throws NullPointerException if any of the returned values from those
	 * method calls returns null.
	 * @throws Illegal
	 */
	private void initializeFields(float openGapPenalty, float extendGapPenalty) {
		
		initialTracebackMatrix();
		initializeVerticalGapCache();		
		initializeScoreCache(openGapPenalty, extendGapPenalty);
	}
	private void initializeScoreCache(float openGapPenalty,
			float extendGapPenalty) {
		scoreCache[PREVIOUS_ROW] = getInitialGapScores(traceback.getYLength(), openGapPenalty, extendGapPenalty );
		scoreCache[CURRENT_ROW][1] = scoreCache[PREVIOUS_ROW][1];
	}
	private void initializeVerticalGapCache() {
		inAVerticalGapCache[0] = new BitSet(traceback.getYLength());
		inAVerticalGapCache[1] = new BitSet(traceback.getYLength());
	}
	private void initialTracebackMatrix() {
		TracebackDirection initialRowDirection = getInitialRowTracebackValue();
		if(initialRowDirection ==null){
			throw new NullPointerException("initialRowDirection can not be null");
		}
		TracebackDirection initialColDirection = getInitialColTracebackValue();
		if(initialColDirection ==null){
			throw new NullPointerException("initialColDirection can not be null");
		}
		//the origin of the matrix is always terminal
		traceback.set(0,0, TracebackDirection.TERMINAL);
		//populate the first row and column using subclass values as input
		for(int i=1; i<traceback.getYLength(); i++){
			traceback.set(0,i, initialRowDirection);
			
		}
		for(int i=1; i<traceback.getXLength(); i++){
			traceback.set(i,0, initialColDirection);
		}
	}
	/**
	 * Get the {@link TracebackDirection}
	 * that should be used to represent when
	 * the first base of the query does not align to the subject.
	 * @return a {@link TracebackDirection}; can not be null.
	 */
	protected abstract TracebackDirection getInitialRowTracebackValue();
	/**
	 * Get the {@link TracebackDirection}
	 * that should be used to represent when
	 * the first base of the subject does not align to the query.
	 * @return a {@link TracebackDirection}; can not be null.
	 */
	protected abstract TracebackDirection getInitialColTracebackValue();
	/**
	 * Get the gap scores that represent when 
	 * the first base of the query does not align to the subject
	 * and vice versa.
	 * @param length the length of the gap score array to create
	 * @param openGapPenalty the penalty value for a new (open) gap.
	 * @param extendGapPenalty the penalty for extending an already open gap.
	 * @return a float array of the gap scores.
	 */
	protected abstract float[] getInitialGapScores(int length, float openGapPenalty,
			float extendGapPenalty);
	/**
	 * Replace the contents of the previous row
	 * with the contents of the  current row.  Since 
	 * we only care about the previous 1 rows
	 * we can safely forget about anything older than that.
	 * (and save memory)
	 */
	private void updateCaches() {
		for(int j=0; j< scoreCache[CURRENT_ROW].length; j++){
			scoreCache[PREVIOUS_ROW][j] = scoreCache[CURRENT_ROW][j];
			
			inAVerticalGapCache[PREVIOUS_ROW].set(j,inAVerticalGapCache[CURRENT_ROW].get(j));
		}
		
	}

	private void printTraceBack(){
		for(int i=0; i<traceback.getXLength(); i++){
			
			for(int j=0; j<traceback.getYLength(); j++){
				
				System.out.printf("%s [?] ",
							traceback.get(i,j).toString().charAt(0));
			}
			System.out.println("");
		}
		
		System.out.println(Arrays.toString(scoreCache[PREVIOUS_ROW]));
		System.out.println(Arrays.toString(scoreCache[CURRENT_ROW]));
	}

	/**
	 * Return the updated value of CurrentStartPoint
	 * if your implementation deems it necessary.
	 * @param newScore the new score in the current cell being computed.
	 * @param currentStartPoint the CurrentStartPoint which might need 
	 * to be updated.
	 * @param i the current cell row being computed.
	 * @param j the current cell column being computed.
	 * @return either a new {@link StartPoint} object
	 * or {@literal currentStartPoint} if it should not
	 * be updated; should never return null.
	 */
	protected abstract StartPoint updateCurrentStartPoint(float newScore,
			StartPoint currentStartPoint, int i, int j);
	/**
	 * Given the 3 possible input scores for the current cell,
	 * compute the {@link WalkBack} that might
	 * be used later during the backtracking phase.
	 * @param alignmentScore the score of this cell if for aligning
	 * the current two residues together.
	 * @param horizontalGapPenalty the score of this cell if the horizontal
	 * gap is used.
	 * @param verticalGapPenalty the score of this cell if the vertical
	 * gap is used.
	 * @return a new {@link WalkBack}; never null.
	 */
	protected abstract WalkBack computeBestWalkBack(float alignmentScore,
			float horizontalGapPenalty, float verticalGapPenalty);
	
	private P traceBack(byte[] seq1Bytes, byte[] seq2Bytes,
			StartPoint currentStartPoint) {
		boolean done=false;
		int x=currentStartPoint.getX();
		int y = currentStartPoint.getY();
		float score = currentStartPoint.getScore();
		SequenceAlignmentBuilder<R,S,A> alignmentBuilder = pairwiseStrategy.createSequenceAlignmentBuilder(true);
		alignmentBuilder.setAlignmentOffsets(x-1, y-1);
		R gap =  pairwiseStrategy.getGap();
		List<R> residuesByOrdinal = pairwiseStrategy.getResidueList();
		while(!done){
			
			TracebackDirection tracebackDirection = traceback.get(x,y);
			switch(tracebackDirection){
				case VERTICAL :
					alignmentBuilder.addGap(residuesByOrdinal.get(seq1Bytes[x-1]), gap);
					x--;
					break;
					
				case HORIZONTAL :
					alignmentBuilder.addGap(gap,residuesByOrdinal.get(seq2Bytes[y-1]));
					y--;
					break;
				case DIAGNOL:
					boolean isMatch = seq1Bytes[x-1] == seq2Bytes[y-1];
					if(isMatch){
						alignmentBuilder.addMatch(residuesByOrdinal.get(seq1Bytes[x-1]));
					}else{
						alignmentBuilder.addMismatch(residuesByOrdinal.get(seq1Bytes[x-1]), residuesByOrdinal.get(seq2Bytes[y-1]));
					}
					
					x--;
					y--;
					break;
				case TERMINAL:
					done = true;
					break;
				default:
					//will never happen 
					throw new IllegalStateException("invalid Traceback direction "+ tracebackDirection);
			}
		}
		return  pairwiseStrategy.wrapPairwiseAlignment(PairwiseSequenceAlignmentWrapper.wrap(alignmentBuilder.build(), score));
	}
	/**
	 * Get the completed {@link SequenceAlignment}.
	 * @return the 
	 */
	public P getPairwiseSequenceAlignment(){
		return alignment;
	}
	
	private byte[] convertToByteArray(Sequence<R> sequence) {
		ByteBuffer buf = ByteBuffer.allocate((int)sequence.getLength());
		for(R residue : sequence){
			buf.put(residue.getOrdinalAsByte());
		}
		buf.flip();
		return buf.array();
	}

	/**
	 * {@code StartPoint} is a class
	 * that points to the current location
	 * in our traceback matrix of where the local
	 * alignment should start.  This class
	 * also keeps track of the current best score
	 * so we know if we should update our pointer
	 * to point to an even better starting location.
	 * @author dkatzel
	 *
	 */
	protected static final class StartPoint{
		private final int x,y;
		private final float score;
		public StartPoint(){
			this(0,0,Float.NEGATIVE_INFINITY);
		}
		public StartPoint(int x, int y, float score) {
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
		
		public StartPoint updateIfWorseThan(int x, int y, float score){
			if(score >= this.score){
				return new StartPoint(x,y,score);
			}
			return this;
		}
		@Override
		public String toString() {
			return "CurrentStartPoint [x=" + x + ", y=" + y + ", score="
					+ score + "]";
		}
		
	}
	/**
	 * {@code WalkBack} is a wrapper around
	 * the a score and the {@link TracebackDirection}
	 * to use in the traceback for the current cell
	 * in the traceback matrix.
	 * This class is used internally by {@link #computeBestWalkBack(float, float, float)}
	 * to allow subclasses to determine the which score is the best
	 * in an implementation specific way.
	 * @author dkatzel
	 *
	 */
	protected static final class WalkBack{
		private final TracebackDirection tracebackDirection;
		private final float bestScore;
		public WalkBack(float bestScore,
				TracebackDirection tracebackDirection) {
			if(tracebackDirection ==null){
				throw new NullPointerException("traceback direction can not be null");
			}
			this.bestScore = bestScore;
			this.tracebackDirection = tracebackDirection;
		}
		public TracebackDirection getTracebackDirection() {
			return tracebackDirection;
		}
		public float getBestScore() {
			return bestScore;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Float.floatToIntBits(bestScore);
			result = prime
					* result
					+ tracebackDirection.hashCode();
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj){
				return true;
			}
			if (obj == null){
				return false;
			}
			if (getClass() != obj.getClass()){
				return false;
			}
			WalkBack other = (WalkBack) obj;
			if (Float.floatToIntBits(bestScore) != Float
					.floatToIntBits(other.bestScore)){
				return false;
			}
			if (tracebackDirection != other.tracebackDirection){
				return false;
			}
			return true;
		}
		
		
	}
	
	/**
	 * The direction to traverse to the next
	 * cell in the traceback matrix.  The traceback
	 * will start at the last cell in the alignment
	 * and walk backwards towards the beginning
	 * of the alignment.
	 * @author dkatzel
	 *
	 */
	protected enum TracebackDirection{
		/**
		 * Stop the traceback. This will end
		 * the alignment and will always
		 * be the value of the last cell
		 * visited in the matrix.
		 */
		TERMINAL,
		/**
		 * Move to the cell horizontally
		 * (left).  This will cause
		 * the alignment to add a horizontal
		 * gap to the alignment.
		 */
		HORIZONTAL,
		/**
		 * Move to the cell vertically
		 * (up).  This will cause
		 * the alignment to add a vertical
		 * gap to the alignment.
		 */
		VERTICAL,
		/**
		 * Move to the cell diagonally
		 * horizontal AND vertical
		 * (left and up).  This will cause
		 * the alignment to a match/mismatch
		 * to the alignment.
		 */
		DIAGNOL;
	}
	
	private static final class TraceBackMatrix{
		private final byte[][] matrix;
		private static final TracebackDirection[] ORDINALS = TracebackDirection.values();
		TraceBackMatrix(int x, int y){
			matrix = new byte[x][y];
		}
		
		public TracebackDirection get(int x, int y){
			return ORDINALS[matrix[x][y]];
		}
		
		public void set(int x, int y, TracebackDirection value){
			matrix[x][y] = (byte)value.ordinal();
		}
		
		public int getXLength(){
			return matrix.length;
		}
		public int getYLength(){
			return matrix[0].length;
		}
		
	}
}
