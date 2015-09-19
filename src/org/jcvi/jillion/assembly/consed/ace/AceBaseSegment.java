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
/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Range;
/**
 * {@code AceBestSegment} is an object representation of 
 * an Ace "BS" record.  Best Segments represent which read
 * EXACTLY matches the consensus at the given range.
 * @author dkatzel
 *
 *
 */
interface AceBaseSegment {
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
    /**
     * Two AceBaseSegments are equal
     * if they have the same read name and gapped consensus range.
     * @param obj
     */
    @Override
    boolean equals(Object obj);
}
