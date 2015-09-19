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
package org.jcvi.jillion.trace.chromat;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
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
	boolean equals(Object obj);

	/**
	 * Retrieves the phred Confidence values.
	 * @return the qualities
	 */
	QualitySequence getQualitySequence();

	/**
	 * Retrieves the trace sample position data.
	 * @return the positions
	 */
	PositionSequence getPositionSequence();

}
