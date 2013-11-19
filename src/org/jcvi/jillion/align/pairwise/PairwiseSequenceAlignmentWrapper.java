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
package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;

public final class PairwiseSequenceAlignmentWrapper<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> implements PairwiseSequenceAlignment<R, S>{
	
	
	private final A delegate;
	private final float score;
	
	
	public static <R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> 
	PairwiseSequenceAlignment<R, S> wrap(A alignment, float score){
		return new PairwiseSequenceAlignmentWrapper<R,S,A>(alignment, score);
	}
	
	
	
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
