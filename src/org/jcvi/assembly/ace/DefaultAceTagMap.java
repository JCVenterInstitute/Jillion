/*
 * Created on Jan 8, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultAceTagMap implements AceTagMap{

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



    public static class Builder implements org.jcvi.Builder<DefaultAceTagMap>{
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
