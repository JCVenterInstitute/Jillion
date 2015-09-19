/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;

final class PairwiseSequenceAlignmentWrapper<R extends Residue, S extends ResidueSequence<R>, A extends SequenceAlignment<R, S>> implements PairwiseSequenceAlignment<R, S>{
	
	
	private final A delegate;
	private final float score;
	
	
	public static <R extends Residue, S extends ResidueSequence<R>, A extends SequenceAlignment<R, S>> 
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
		@SuppressWarnings("rawtypes")
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
