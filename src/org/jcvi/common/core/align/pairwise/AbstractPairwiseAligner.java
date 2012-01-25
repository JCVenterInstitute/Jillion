package org.jcvi.common.core.align.pairwise;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.align.SequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
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
 * @param <A> the {@link PairwiseSequenceAlignment} type returned by this aligner.
 */
abstract class AbstractPairwiseAligner <R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> {
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
	/**
	 * The previous row in our cache.
	 */
	private static final int PREVIOUS_ROW=0;
	/**
	 * The current row in our cache.
	 */
	private static final int CURRENT_ROW=1;
	
	
	/**
	 * The matrix which stores all of our traceback
	 * values. 
	 */
	private final byte[][] traceback;
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
	private final PairwiseSequenceAlignment<R, S> alignment;
	
	protected AbstractPairwiseAligner(Sequence<R> query, Sequence<R> subject, ScoringMatrix<R> matrix, float openGapPenalty, float extendGapPenalty){
		
		traceback = new byte[(int)query.getLength()+1][(int)subject.getLength()+1];
		
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
		
		StartPoint currentStartPoint = new StartPoint();
		for(int i=1; i<=lengthOfSeq1; i++){

			
			float cumulativeHorizontalGapPenalty=Float.NEGATIVE_INFINITY;
			BitSet inAHorizontalGap = new BitSet(lengthOfSeq2+1);
			for(int j=1; j<= lengthOfSeq2; j++){
				float diagnol = scoreCache[PREVIOUS_ROW][j-1];
				float verticalGapExtensionScore = inAVerticalGapCache[PREVIOUS_ROW].get(j)? 
							scoreCache[PREVIOUS_ROW][j] + extendGapPenalty 
							: Float.NEGATIVE_INFINITY	;
				float verticalOpenGapScore = scoreCache[PREVIOUS_ROW][j] + openGapPenalty;
				if(verticalGapExtensionScore > verticalOpenGapScore){
					verticalGapPenaltiesSoFar[j] = verticalGapExtensionScore;
				}else{
					verticalGapPenaltiesSoFar[j] = verticalOpenGapScore;
				}
				
				float horizontalGapExtensionScore =  inAHorizontalGap.get(j-1) ?
						scoreCache[CURRENT_ROW][j-1]+ extendGapPenalty
						: Float.NEGATIVE_INFINITY;
				float horizontalGapOpenScore = scoreCache[CURRENT_ROW][j-1] + openGapPenalty;
				if(horizontalGapExtensionScore >= horizontalGapOpenScore){
					cumulativeHorizontalGapPenalty = horizontalGapExtensionScore;
					
				}else{
					cumulativeHorizontalGapPenalty = horizontalGapOpenScore;
				}
				
				//need to do -1s because 0 offset in matrix is filled with stops
				//and actual values start at offset 1
				float alignmentScore = diagnol + matrix.getScore(seq1Bytes[i-1],seq2Bytes[j-1]);
				
				
				BestWalkBack bestWalkBack = computeBestWalkBack(alignmentScore, cumulativeHorizontalGapPenalty, verticalGapPenaltiesSoFar[j]);
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
				traceback[i][j]= (byte)bestWalkBack.getTracebackDirection().ordinal();
				diagnol = traceback[i-1][j];
				
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
		scoreCache[PREVIOUS_ROW] = getInitialGapScores(traceback[0].length, openGapPenalty, extendGapPenalty );
		scoreCache[CURRENT_ROW][1] = scoreCache[PREVIOUS_ROW][1];
	}
	private void initializeVerticalGapCache() {
		inAVerticalGapCache[0] = new BitSet(traceback[0].length);
		inAVerticalGapCache[1] = new BitSet(traceback[0].length);
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
		traceback[0][0] = (byte)TracebackDirection.TERMINAL.ordinal();
		//populate the first row and column using subclass values as input
		for(int i=1; i<traceback[0].length; i++){
			traceback[0][i]=(byte)initialRowDirection.ordinal();
		}
		for(int i=1; i<traceback.length; i++){
			traceback[i][0]=(byte)initialColDirection.ordinal();
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
	 * @param length
	 * @param openGapPenalty
	 * @param extendGapPenalty
	 * @return
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
		for(int i=0; i<traceback.length; i++){
			
			for(int j=0; j<traceback[0].length; j++){
				
				System.out.printf("%s [?] ",
							TracebackDirection.values()[traceback[i][j]].toString().charAt(0));
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

	protected abstract BestWalkBack computeBestWalkBack(float alignmentScore,
			float horrizontalGapPenalty, float verticalGapPenalty);

	private PairwiseSequenceAlignment<R, S> traceBack(byte[] seq1Bytes, byte[] seq2Bytes,
			StartPoint currentStartPoint) {
		boolean done=false;
		int x=currentStartPoint.getX();
		int y = currentStartPoint.getY();
		float score = currentStartPoint.getScore();
		SequenceAlignmentBuilder<R,S,A> alignmentBuilder = createSequenceAlignmentBuilder(true);
		alignmentBuilder.setAlignmentOffsets(x-1, y-1);
		R gap = getGap();
		while(!done){
			
			switch(TracebackDirection.values()[traceback[x][y]]){
				case VERTICAL :
					alignmentBuilder.addGap(getResidueFromOrdinal(seq1Bytes[x-1]), gap);
					x--;
					break;
					
				case HORIZONTAL :
					alignmentBuilder.addGap(gap,getResidueFromOrdinal(seq2Bytes[y-1]));
					y--;
					break;
				case DIAGNOL:
					boolean isMatch = seq1Bytes[x-1] == seq2Bytes[y-1];
					if(isMatch){
						alignmentBuilder.addMatch(getResidueFromOrdinal(seq1Bytes[x-1]));
					}else{
						alignmentBuilder.addMismatch(getResidueFromOrdinal(seq1Bytes[x-1]), getResidueFromOrdinal(seq2Bytes[y-1]));
					}
					
					x--;
					y--;
					break;
				case TERMINAL:
					done = true;
					break;
			}
		}
		return PairwiseSequenceAlignmentWrapper.wrap(alignmentBuilder.build(), score);
	}
	/**
	 * Get the completed {@link SequenceAlignment}.
	 * @return the 
	 */
	public PairwiseSequenceAlignment<R, S> getSequenceAlignment(){
		return alignment;
	}
	/**
	 * Get the {@link Residue} instance that represents a gap.
	 * @return a {@link Residue}; never null.
	 */
	protected abstract R getGap();
	/**
	 * Get the {@link Residue} that corresponds
	 * to the given ordinal value.  
	 * @param ordinal the oridinal value to get a {@link Residue}
	 * for.
	 * @return a {@link Residue}; never nulll.
	 */
	protected abstract R getResidueFromOrdinal(int ordinal);
	/**
	 * Create a new instance of the type of
	 * {@link SequenceAlignmentBuilder} required by this implementation.
	 * @param builtFromTraceback is this alignment going to be built via
	 * a traceback method.  Currently always set to {@code true}.
	 * @return a new {@link SequenceAlignmentBuilder} that can be built
	 * via a traceback if specified.
	 */
	protected abstract SequenceAlignmentBuilder<R, S,A> createSequenceAlignmentBuilder(boolean builtFromTraceback);

	
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
	 * {@code BestWalkBack} is a wrapper around
	 * the a best score and the {@link TracebackDirection}
	 * to use in the traceback for the current cell
	 * in the traceback matrix.
	 * This class is used internally by {@link AbstractPairwiseAligner#computeBestWalkBack(float, float, float)}
	 * to allow subclasses to determine the which score is the best
	 * in an implementation specific way.
	 * @author dkatzel
	 *
	 */
	protected static final class BestWalkBack{
		private final TracebackDirection tracebackDirection;
		private final float bestScore;
		public BestWalkBack(float bestScore,
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
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BestWalkBack other = (BestWalkBack) obj;
			if (Float.floatToIntBits(bestScore) != Float
					.floatToIntBits(other.bestScore))
				return false;
			if (tracebackDirection != other.tracebackDirection)
				return false;
			return true;
		}
		
		
	}
}
