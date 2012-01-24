package org.jcvi.common.core.align;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;

public interface SequenceAlignment<R extends Residue, S extends Sequence<R>> {

	double getPercentIdentity();

	int getAlignmentLength();

	int getNumberOfMismatches();

	int getNumberOfGapOpenings();

	/**
	 * @return the gappedQueryAlignment
	 */
	S getGappedQueryAlignment();

	/**
	 * @return the gappedSubjectAlignment
	 */
	S getGappedSubjectAlignment();
	
	DirectedRange getQueryRange();
    
    DirectedRange getSubjectRange();

}