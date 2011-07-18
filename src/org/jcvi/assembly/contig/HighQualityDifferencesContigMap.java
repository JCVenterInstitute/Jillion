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
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.common.core.seq.qual.PhredQuality;

public interface HighQualityDifferencesContigMap extends Iterable<List<DefaultQualityDifference>>{

    PhredQuality getQualityThreshold();
    /**
     * Get the list of {@link DefaultQualityDifference}s for the given placed read.
     * @param placedRead
     * @return Always returns a not-null List.  If there are no high quality
     * differences for the given read, this method should return an empty
     * list instead of null.
     */
    List<DefaultQualityDifference> getHighQualityDifferencesFor(PlacedRead placedRead);
    /**
     * Gets the number of reads with highQuality differences.
     * @return
     */
    int getNumberOfReadsWithHighQualityDifferences();

    Set<Entry<PlacedRead, List<DefaultQualityDifference>>> entrySet();

}
