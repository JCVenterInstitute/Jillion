/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.Range;
/**
 * {@code AceBestSegment} is an object representation of 
 * an Ace "BS" record.  Best Segments represent which read
 * EXACTLY matches the consensus at the given range.
 * @author dkatzel
 *
 *
 */
public interface AceBestSegment {
    /**
     * Name of the read that matches the consensus.
     * @return name of the read.
     */
    String getReadName();
    /**
     * Range that this read matches the consensus.
     * @return a Range in consensus where this read
     * matches EXACTLY.
     */
    Range  getGappedConsensusRange();
}
