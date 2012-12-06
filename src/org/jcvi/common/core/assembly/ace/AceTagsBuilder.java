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

package org.jcvi.common.core.assembly.ace;

import java.util.Collection;

import org.jcvi.common.core.util.Builder;

/**
 * {@code AceTagsBuilder} is a {@link Builder} implementation
 * that will build a valid {@link AceTags} object.
 * @author dkatzel
 *
 *
 */
interface AceTagsBuilder extends Builder<AceTags>{
    /**
     * Add the given {@link ConsensusAceTag} to this.
     * @param tag the {@link ConsensusAceTag} to add; can not be null.
     * @return this.
     * @throws NullPointerException if tag is null.
     */
    AceTagsBuilder addConsensusTag(ConsensusAceTag tag);
    /**
     * Add the all the given {@link ConsensusAceTag}s to this.
     * @param tags a collection of {@link ConsensusAceTag}s to add; 
     * neither the collection can not be null nor can any element in the 
     * collection be null.
     * @return this.
     * @throws NullPointerException if tags is null or any element in the tags is null.
     */
    AceTagsBuilder addAllConsensusTags(Collection<? extends ConsensusAceTag> tags);
    /**
     * Add the given {@link DefaultWholeAssemblyAceTag} to this.
     * @param tag the {@link DefaultWholeAssemblyAceTag} to add; can not be null.
     * @return this.
     * @throws NullPointerException if tag is null.
     */
    AceTagsBuilder addWholeAssemblyTag(DefaultWholeAssemblyAceTag tag);
    /**
     * Add the all the given {@link DefaultWholeAssemblyAceTag}s to this.
     * @param tags a collection of {@link DefaultWholeAssemblyAceTag}s to add; 
     * neither the collection can not be null nor can any element in the 
     * collection be null.
     * @return this.
     * @throws NullPointerException if tags is null or any element in the tags is null.
     */
    AceTagsBuilder addAllWholeAssemblyTags(Collection<? extends DefaultWholeAssemblyAceTag> tags);
    /**
     * Add the given {@link DefaultReadAceTag} to this.
     * @param tag the {@link DefaultReadAceTag} to add; can not be null.
     * @return this.
     * @throws NullPointerException if tag is null.
     */
    AceTagsBuilder addReadTag(DefaultReadAceTag tag);
    /**
     * Add the all the given {@link DefaultReadAceTag}s to this.
     * @param tags a collection of {@link DefaultReadAceTag}s to add; 
     * neither the collection can not be null nor can any element in the 
     * collection be null.
     * @return this.
     * @throws NullPointerException if tags is null or any element in the tags is null.
     */
    AceTagsBuilder addAllDefaultReadAceTags(Collection<? extends DefaultReadAceTag> tags);
}
