package org.jcvi.common.core.seq.trace.sanger.chromat;

import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.common.core.symbol.qual.QualitySequence;
/**
 * <code>Channel</code> represents the
 * data from a single trace channel (lane).
 * @author dkatzel
 *
 */
public interface Channel {

	/**
	 * Two {@link Channel}s are equal
	 * if they have equal positions and confidences.
	 * @param obj
	 * @return
	 */
	public boolean equals(Object obj);

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode();

	/**
	 * Retrieves the phred Confidence values.
	 * @return the confidence
	 */
	public QualitySequence getConfidence();

	/**
	 * Retrieves the trace sample position data.
	 * @return the positions
	 */
	public PositionSequence getPositions();

}