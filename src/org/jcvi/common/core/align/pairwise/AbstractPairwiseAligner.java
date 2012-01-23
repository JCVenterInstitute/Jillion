package org.jcvi.common.core.align.pairwise;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.align.SequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;

public abstract class AbstractPairwiseAligner <R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> {
	protected enum TracebackDirection{
		TERMINAL,
		HORIZONTAL,
		VERTICAL,
		DIAGNOL;
	}
	
	private final byte[][] traceback;
	private final float[][] scores;
	private final A alignment;
	
	public AbstractPairwiseAligner(Sequence<R> query, Sequence<R> subject, ScoringMatrix<R> matrix, float openGapPenalty, float extendGapPenalty){
		
		traceback = new byte[(int)query.getLength()+1][(int)subject.getLength()+1];
		scores = new float[(int)query.getLength()+1][(int)subject.getLength()+1];
		for(int i=0; i<traceback[0].length; i++){
			traceback[0][i]=(byte)TracebackDirection.TERMINAL.ordinal();
		}
		for(int i=0; i<traceback.length; i++){
			traceback[i][0]=(byte)TracebackDirection.TERMINAL.ordinal();
		}
		byte[] seq1Bytes = convertToByteArray(query);
		byte[] seq2Bytes = convertToByteArray(subject);
		
		int lengthOfSeq1 = seq1Bytes.length;
		int lengthOfSeq2 = seq2Bytes.length;
		short[][] sizesOfHorizonalGaps = new short[lengthOfSeq1+1][lengthOfSeq2+1];
		short[][] sizesOfVerticalGaps = new short[lengthOfSeq1+1][lengthOfSeq2+1];
		for(int i=0; i< seq1Bytes.length; i++){
			Arrays.fill(sizesOfHorizonalGaps[i], (short)0);	
			Arrays.fill(sizesOfVerticalGaps[i], (short)0);	
		}		
		for(int i=0; i< seq2Bytes.length; i++){
			Arrays.fill(sizesOfHorizonalGaps[0], (short)0);	
			Arrays.fill(sizesOfVerticalGaps[0], (short)0);	
		}
		//only need to keep array of vertical accumulated gap
		//penalties since we are populating horizontally  we can just
		//keep a float for current horizontal penalty 
		float[] verticalGapPenaltiesSoFar = new float[lengthOfSeq2+1];		
		Arrays.fill(verticalGapPenaltiesSoFar, Float.NEGATIVE_INFINITY);
		
		float[] bestPathSoFar = new float[lengthOfSeq2+1];
		Arrays.fill(bestPathSoFar, 0F);
		
		CurrentStartPoint currentStartPoint = new CurrentStartPoint();
		for(int i=1; i<=lengthOfSeq1; i++){

			
			float cumulativeHorizontalGapPenalty=Float.NEGATIVE_INFINITY;
			for(int j=1; j<= lengthOfSeq2; j++){
				float diagnol = scores[i-1][j-1];
				float verticalGapExtensionScore = sizesOfVerticalGaps[i-1][j] >0 ? 
							scores[i-1][j] + extendGapPenalty 
							: Float.NEGATIVE_INFINITY	;
				float verticalOpenGapScore = scores[i-1][j] + openGapPenalty;
				if(verticalGapExtensionScore > verticalOpenGapScore){
					verticalGapPenaltiesSoFar[j] = verticalGapExtensionScore;
				}else{
					verticalGapPenaltiesSoFar[j] = verticalOpenGapScore;
				}
				
				float horizontalGapExtensionScore =  sizesOfHorizonalGaps[i][j-1] >0 ?
						scores[i][j-1]+ extendGapPenalty
						: Float.NEGATIVE_INFINITY;
				float horizontalGapOpenScore = scores[i][j-1] + openGapPenalty;
				if(horizontalGapExtensionScore >= horizontalGapOpenScore){
					cumulativeHorizontalGapPenalty = horizontalGapExtensionScore;
					
				}else{
					cumulativeHorizontalGapPenalty = horizontalGapOpenScore;
				}
				
				//need to do -1s because 0 offset in matrix is filled with stops
				//and actual values start at offset 1
				float alignmentScore = diagnol + matrix.getScore(seq1Bytes[i-1],seq2Bytes[j-1]);
				
				
				BestWalkBack bestWalkBack = computeBestWalkBack(alignmentScore, cumulativeHorizontalGapPenalty, verticalGapPenaltiesSoFar[j]);
				scores[i][j] = bestWalkBack.getBestScore();
				if( bestPathSoFar[j] <= bestWalkBack.getBestScore()){
					bestPathSoFar[j] = bestWalkBack.getBestScore();
					currentStartPoint = updateCurrentStartPoint(bestPathSoFar[j], currentStartPoint, i, j);
				}
				switch(bestWalkBack.getTracebackDirection()){
					case HORIZONTAL: sizesOfHorizonalGaps[i][j] = (short)(sizesOfHorizonalGaps[i][j-1]+1);
									break;
					case VERTICAL : sizesOfVerticalGaps[i][j] = (short)(sizesOfVerticalGaps[i-1][j] +1);
									break;
					case DIAGNOL: 	sizesOfHorizonalGaps[i][j]  =0;
									sizesOfVerticalGaps[i][j] =0;
									break;
					default:
								break;
						
				}
				traceback[i][j]= (byte)bestWalkBack.getTracebackDirection().ordinal();
				diagnol = traceback[i-1][j];
				
				//printTraceBack(query,subject);
			}
		}
		//now do trace back
		alignment = traceBack(seq1Bytes, seq2Bytes, sizesOfHorizonalGaps,
				sizesOfVerticalGaps, currentStartPoint);
		
	}

	private void printTraceBack(Sequence<R> query, Sequence<R> subject){
		System.out.println(subject);
		for(int i=0; i<traceback.length; i++){
			if(i==0){
				System.out.print("  : ");
			}else{
				System.out.printf("%s : ", query.get(i-1));
			}
			for(int j=0; j<traceback[0].length; j++){
				System.out.printf("%s [%.0f] ",
						TracebackDirection.values()[traceback[i][j]].toString().charAt(0),
						scores[i][j]);
			}
			System.out.println("");
		}
	}
	
	protected abstract float getInitialRowScore(float[] bestPathSoFar, int rowNumber, float openGapPenalty,
			float extendGapPenalty);

	protected abstract CurrentStartPoint updateCurrentStartPoint(float currentBestScore,
			CurrentStartPoint currentStartPoint, int i, int j);

	protected abstract BestWalkBack computeBestWalkBack(float alignmentScore,
			float horrizontalGapPenalty, float verticalGapPenalty);

	protected A traceBack(byte[] seq1Bytes, byte[] seq2Bytes,
			short[][] sizesOfHorizonalGaps, short[][] sizesOfVerticalGaps,
			CurrentStartPoint currentStartPoint) {
		boolean done=false;
		int x=currentStartPoint.getX();
		int y = currentStartPoint.getY();
		
		SequenceAlignmentBuilder<R,S,A> alignmentBuilder = createSequenceAlignmentBuilder();
		R gap = getGap();
		while(!done){
			
			switch(TracebackDirection.values()[traceback[x][y]]){
			case VERTICAL :
				{
					alignmentBuilder.addGap(getResidueFromOrdinal(seq1Bytes[x-1]), gap);
					x--;
				}
					break;
					
			case HORIZONTAL :
				{
						alignmentBuilder.addGap(gap,getResidueFromOrdinal(seq2Bytes[y-1]));
						y--;
				}
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
		alignmentBuilder.reverse();
		return alignmentBuilder.build();
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
	protected static final class CurrentStartPoint{
		private final int x,y;
		private final float score;
		public CurrentStartPoint(){
			this(0,0,Float.NEGATIVE_INFINITY);
		}
		public CurrentStartPoint(int x, int y, float score) {
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
			if(score >= this.score){
				return new CurrentStartPoint(x,y,score);
			}
			return this;
		}
		@Override
		public String toString() {
			return "CurrentStartPoint [x=" + x + ", y=" + y + ", score="
					+ score + "]";
		}
		
	}
	
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
