package org.jcvi.jillion.trace.sanger.chromat;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.trace.sanger.PositionSequence;
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