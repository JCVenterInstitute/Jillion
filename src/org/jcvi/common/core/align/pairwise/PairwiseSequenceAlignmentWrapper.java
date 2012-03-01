package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;

public final class PairwiseSequenceAlignmentWrapper<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> implements PairwiseSequenceAlignment<R, S>{
	
	public static <R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> 
	PairwiseSequenceAlignment<R, S> wrap(A alignment, float score){
		return new PairwiseSequenceAlignmentWrapper<R,S,A>(alignment, score);
	}
	
	private final A delegate;
	private final float score;
	
	
	private PairwiseSequenceAlignmentWrapper(
			A delegate, float score) {
		this.delegate = delegate;
		this.score = score;
	}

	@Override
	public double getPercentIdentity() {
		return delegate.getPercentIdentity();
	}

	@Override
	public int getAlignmentLength() {
		return delegate.getAlignmentLength();
	}

	@Override
	public int getNumberOfMismatches() {
		return delegate.getNumberOfMismatches();
	}

	@Override
	public int getNumberOfGapOpenings() {
		return delegate.getNumberOfGapOpenings();
	}

	@Override
	public S getGappedQueryAlignment() {
		return delegate.getGappedQueryAlignment();
	}

	@Override
	public S getGappedSubjectAlignment() {
		return delegate.getGappedSubjectAlignment();
	}

	@Override
	public DirectedRange getQueryRange() {
		return delegate.getQueryRange();
	}

	@Override
	public DirectedRange getSubjectRange() {
		return delegate.getSubjectRange();
	}

	@Override
	public float getScore() {
		return score;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((delegate == null) ? 0 : delegate.hashCode());
		result = prime * result + Float.floatToIntBits(score);
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
		PairwiseSequenceAlignmentWrapper other = (PairwiseSequenceAlignmentWrapper) obj;
		if (delegate == null) {
			if (other.delegate != null){
				return false;
			}
		} else if (!delegate.equals(other.delegate)){
			return false;
		}
		if (Float.floatToIntBits(score) != Float.floatToIntBits(other.score)){
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PairwiseSequenceAlignmentWrapper [delegate=" + delegate
				+ ", score=" + score + "]";
	}
	
	
}
