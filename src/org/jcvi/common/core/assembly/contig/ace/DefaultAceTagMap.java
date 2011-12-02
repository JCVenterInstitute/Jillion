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
 * Created on Jan 8, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultAceTagMap implements AceTags{

    private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
    private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
    private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
    
    public static final DefaultAceTagMap EMPTY_MAP = new DefaultAceTagMap.Builder().build();
    
    public DefaultAceTagMap(List<ConsensusAceTag> consensusTags,
            List<ReadAceTag> readTags,
            List<WholeAssemblyAceTag> wholeAssemblyTags){
        this.consensusTags.addAll(consensusTags);
        this.readTags.addAll(readTags);
        this.wholeAssemblyTags.addAll(wholeAssemblyTags);
    }
    
    @Override
    public List<ConsensusAceTag> getConsensusTags() {
        return Collections.unmodifiableList(consensusTags);
    }

    @Override
    public List<ReadAceTag> getReadTags() {
        return Collections.unmodifiableList(readTags);
    }

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
        if (!(obj instanceof DefaultAceTagMap)) {
            return false;
        }
        DefaultAceTagMap other = (DefaultAceTagMap) obj;
        return consensusTags.equals(other.getConsensusTags()) 
            && readTags.equals(other.getReadTags())
            && wholeAssemblyTags.equals(other.getWholeAssemblyTags());
        
        
    }



    public static class Builder implements org.jcvi.common.core.util.Builder<DefaultAceTagMap>{
        private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
        private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
        private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
        @Override
        public DefaultAceTagMap build() {
            return new DefaultAceTagMap(consensusTags, readTags, wholeAssemblyTags);
        }
        
        public Builder addConsensusTag(ConsensusAceTag tag){
            consensusTags.add(tag);
            return this;
        }
        
        public Builder addWholeAssemblyTag(WholeAssemblyAceTag tag){
            wholeAssemblyTags.add(tag);
            return this;
        }
        public Builder addReadTag(ReadAceTag tag){
            readTags.add(tag);
            return this;
        }
    }
}
