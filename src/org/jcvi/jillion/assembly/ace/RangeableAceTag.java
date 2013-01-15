/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ace;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
/**
 * {@code RangeableAceTag} is a version of an 
 * {@link AceTag} that maps to a particular location
 * on a genomic element in the assembly.
 * @author dkatzel
 */
public interface RangeableAceTag extends AceTag, Rangeable {
    /**
     * Get the Id of this tag which can refer to the read or contig
     * this tag references.
     * @return a String; never null.
     */
    String getId();
    /**
     * Gapped Range.
     */
    @Override
    Range asRange();
    /**
     * Should this tag be transferred to new
     * assembly if reassembled?
     * @return {@code true} if should <strong>not</strong> be transferred; {@code false}
     * otherwise.
     */
    boolean isTransient();
}
