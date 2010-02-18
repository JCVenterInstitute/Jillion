/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.List;

import org.jcvi.Range;
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
