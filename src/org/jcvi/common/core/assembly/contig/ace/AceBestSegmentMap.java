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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import java.util.List;

import org.jcvi.common.core.Range;
/**
 * {@code AceBestSegmentMap} is a mapping of all
 * the {@link AceBestSegment}s for a contig.
 * @author dkatzel
 *
 *
 */
public interface AceBestSegmentMap extends Iterable<AceBestSegment>{
    /**
     * Get the {@link AceBestSegment} for the given consensus offset.
     * @param gappedConsensusOffset gapped offset
     * @return the AceBestSegment for the given offset or {@code null}
     * if no AceBestSegment exists.
     */
    AceBestSegment getBestSegmentFor(long gappedConsensusOffset);
    /**
     * Get the list of AceBestSegments covering a Range of
     * consensus offsets.
     * @param gappedConsensusRange range of consensus offsets.
     * @return a list of AceBestSegments, may be empty if no AceBestSegments
     * exist for the given range.
     */
    List<AceBestSegment> getBestSegmentsFor(Range gappedConsensusRange);
    /**
     * Get the number of AceBestSegments for the contig.
     * @return the number of best segments in the contig.
     */
    int getNumberOfBestSegments();
}
