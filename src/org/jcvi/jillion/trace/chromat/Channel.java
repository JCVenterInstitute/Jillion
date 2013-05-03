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
