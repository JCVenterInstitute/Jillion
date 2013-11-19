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

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;

abstract class AbstractPairwiseSequenceAlignment<R extends Residue, S extends Sequence<R>> implements PairwiseSequenceAlignment<R, S>{

	private final PairwiseSequenceAlignment<R, S> delegate;
	
	
	public AbstractPairwiseSequenceAlignment(PairwiseSequenceAlignment<R, S> delegate) {
		this.delegate = delegate;
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
		return delegate.getScore();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(getScore());
		
		result = prime * result + getAlignmentLength();
		result = prime * result + getNumberOfGapOpenings();
		result = prime * result + getNumberOfMismatches();
		long temp;
		temp = Double.doubleToLongBits(getPercentIdentity());
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((getGappedQueryAlignment() == null) ? 0 : getGappedQueryAlignment().hashCode());
		result = prime * result
				+ ((getQueryRange() == null) ? 0 : getQueryRange().hashCode());
		result = prime
				* result
				+ ((getGappedSubjectAlignment() == null) ? 0 : getGappedSubjectAlignment()
						.hashCode());
		result = prime * result
				+ ((getSubjectRange() == null) ? 0 : getSubjectRange().hashCode());
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
		if (!(obj instanceof PairwiseSequenceAlignment)){	
			return false;
		}
		PairwiseSequenceAlignment<?,?> other = (PairwiseSequenceAlignment<?,?>) obj;
		if (Float.floatToIntBits(getScore()) != Float.floatToIntBits(other.getScore())){
			return false;
		}
		
		if (getAlignmentLength() != other.getAlignmentLength()){
			return false;
		}
		if (getNumberOfGapOpenings() != other.getNumberOfGapOpenings()){
			return false;
		}
		if (getNumberOfMismatches() != other.getNumberOfMismatches()){
			return false;
		}
		if (Double.doubleToLongBits(getPercentIdentity()) != Double
				.doubleToLongBits(other.getPercentIdentity())){
			return false;
		}
		if (getGappedQueryAlignment() == null) {
			if (other.getGappedQueryAlignment() != null){
				return false;
			}
		} else if (!getGappedQueryAlignment().equals(other.getGappedQueryAlignment())){
			return false;
		}
		if(!getQueryRange().equals(other.getQueryRange())){
			return false;
		}
		if (!getGappedSubjectAlignment().equals(other.getGappedSubjectAlignment())){
			return false;
		}
		if (!getSubjectRange().equals(other.getSubjectRange())){
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AbstractPairwiseSequenceAlignment [delegate=" + delegate
				+ ", getAlignmentLength()=" + getAlignmentLength()
				+ ", getPercentIdentity()=" + getPercentIdentity()
				+ ", getScore()=" + getScore() + ", getNumberOfMismatches()="
				+ getNumberOfMismatches() + ", getNumberOfGapOpenings()="
				+ getNumberOfGapOpenings() + ", getGappedQueryAlignment()="
				+ getGappedQueryAlignment() + ", getGappedSubjectAlignment()="
				+ getGappedSubjectAlignment() + ", getQueryRange()="
				+ getQueryRange() + ", getSubjectRange()=" + getSubjectRange()
				+ "]";
	}
	
	
}
