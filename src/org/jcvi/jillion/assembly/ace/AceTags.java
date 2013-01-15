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

import java.util.List;
/**
 * {@code AceTags} is an interface
 * used to group all the different 
 * types of {@link AceTag}s
 * together.
 * @author dkatzel
 */
interface AceTags {
    /**
     * Get all the consensus tags for this 
     * assembly.
     * @return a List of {@link ConsensusAceTag}s
     * if no tags exists, then return an empty
     * list; never null.
     */
    List<ConsensusAceTag> getConsensusTags();
    /**
     * Get all the read tags for this 
     * assembly.
     * @return a List of {@link ReadAceTag}s
     * if no tags exists, then return an empty
     * list; never null.
     */
    List<ReadAceTag> getReadTags();
    /**
     * Get all the assembly tags for this 
     * assembly.
     * @return a List of {@link WholeAssemblyAceTag}s
     * if no tags exists, then return an empty
     * list; never null.
     */
    List<WholeAssemblyAceTag> getWholeAssemblyTags();
}