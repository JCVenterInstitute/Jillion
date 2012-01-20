package org.jcvi.common.core.align;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;

public interface SequenceAlignment<R extends Residue, S extends Sequence<R>> {

	public abstract double getPercentIdentity();

	public abstract int getAlignmentLength();

	public abstract int getNumberOfMismatches();

	public abstract int getNumberOfGapOpenings();

	/**
	 * @return the gappedQueryAlignment
	 */
	public abstract S getGappedQueryAlignment();

	/**
	 * @return the gappedSubjectAlignment
	 */
	public abstract S getGappedSubjectAlignment();

}