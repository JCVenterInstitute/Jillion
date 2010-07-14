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

package org.jcvi.assembly.cas.read;

import java.util.List;

/**
 * {@code ReadIndexToContigLookup} is an interface
 * that maps .cas read indexes to their corresponding
 * .cas contig ids.  This is needed because there is no
 * way short of parsing the entire .cas file to map
 * which reads go to which contig.
 * @author dkatzel
 *
 *
 */
public interface ReadIndexToContigLookup {
    /**
     * Get the cas contig id for the given read id.
     * @param readIndex
     * @return the contig id as a long, or {@code null}
     * if the read does not map to any contig.
     */
    Long getContigIdForRead(long readIndex);
    
    
    List<Long> getReadIdsForContig(long contigId);
    
    int getNumberOfContigs();
}
