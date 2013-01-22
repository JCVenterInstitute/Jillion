/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 8, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 * {@code DefaultAceTags} is a default implementation of
 * {@link AceTags} that creates immutable
 * Lists of each tag type.
 * @author dkatzel
 *
 *
 */
final class DefaultAceTags implements AceTags{

    private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
    private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
    private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
    /**
     * Singleton instance of an {@link AceTags} implementation where all the getXXXTags()
     * methods return empty lists.
     */
    public static final DefaultAceTags EMPTY_MAP = new DefaultAceTags.Builder().build();
    
    public static AceTagsBuilder createBuilder(){
        return new DefaultAceTags.Builder();
    }
    /**
     * Create a new instance of DefaultAceTags using the given {@link AceTag}s.
     * The input lists are copied to internal fields; 
     * therefore any modifications to the input lists will not affect this object.
     * @param consensusTags the list of {@link ConsensusAceTag}s; can not be null but may
     * be empty.
     * @param readTags the list of {@link ReadAceTag}s; can not be null but may
     * be empty.
     * @param wholeAssemblyTags the list of {@link WholeAssemblyAceTag}s; can not be null but may
     * be empty.
     * @throws NullPointerException if any of the input lists are null.
     */
    private DefaultAceTags(List<ConsensusAceTag> consensusTags,
            List<ReadAceTag> readTags,
            List<WholeAssemblyAceTag> wholeAssemblyTags){
        this.consensusTags.addAll(consensusTags);
        this.readTags.addAll(readTags);
        this.wholeAssemblyTags.addAll(wholeAssemblyTags);
    }
    /**
     * {@inheritDoc} 
     * @return an unmodifiable list
     */
    @Override
    public List<ConsensusAceTag> getConsensusTags() {
        return Collections.unmodifiableList(consensusTags);
    }
    /**
     * {@inheritDoc} 
     * @return an unmodifiable list
     */
    @Override
    public List<ReadAceTag> getReadTags() {
        return Collections.unmodifiableList(readTags);
    }
    /**
     * {@inheritDoc} 
     * @return an unmodifiable list
     */
    @Override
    public List<WholeAssemblyAceTag> getWholeAssemblyTags() {
        return Collections.unmodifiableList(wholeAssemblyTags);
    }

    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + consensusTags.hashCode();
        result = prime * result
                + readTags.hashCode();
        result = prime
                * result
                +  wholeAssemblyTags.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultAceTags)) {
            return false;
        }
        DefaultAceTags other = (DefaultAceTags) obj;
        return consensusTags.equals(other.getConsensusTags()) 
            && readTags.equals(other.getReadTags())
            && wholeAssemblyTags.equals(other.getWholeAssemblyTags());
        
        
    }



    private static class Builder implements AceTagsBuilder {
        private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
        private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
        private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
        @Override
        public DefaultAceTags build() {
            return new DefaultAceTags(consensusTags, readTags, wholeAssemblyTags);
        }
        /**
         * {@inheritDoc}
         */
        public Builder addConsensusTag(ConsensusAceTag tag){
            checkNotNull(tag);
            consensusTags.add(tag);
            return this;
        }

        private void checkNotNull(AceTag tag) {
            if(tag ==null){
                throw new NullPointerException("tag can not be null");
            }
        }
        /**
         * {@inheritDoc}
         */
        public Builder addWholeAssemblyTag(WholeAssemblyAceTag tag){
            checkNotNull(tag);
            wholeAssemblyTags.add(tag);
            return this;
        }
        /**
         * {@inheritDoc}
         */
        public Builder addReadTag(ReadAceTag tag){
            checkNotNull(tag);
            readTags.add(tag);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AceTagsBuilder addAllConsensusTags(
                Collection<? extends ConsensusAceTag> tags) {
            for(ConsensusAceTag tag : tags){
                addConsensusTag(tag);
            }
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AceTagsBuilder addAllWholeAssemblyTags(
                Collection<? extends WholeAssemblyAceTag> tags) {
            for(WholeAssemblyAceTag tag : tags){
                addWholeAssemblyTag(tag);
            }
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AceTagsBuilder addAllDefaultReadAceTags(
                Collection<? extends ReadAceTag> tags) {
            for(ReadAceTag tag : tags){
                addReadTag(tag);
            }
            return this;
        }
        
        
    }
}
