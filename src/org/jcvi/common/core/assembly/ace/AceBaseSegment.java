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
package org.jcvi.common.core.assembly.ace;

import org.jcvi.common.core.Range;
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
     * Two {@link AceBaseSegment}s are equal
     * if they have the same read name and gapped consensus range.
     * @param obj
     */
    @Override
    boolean equals(Object obj);
}
