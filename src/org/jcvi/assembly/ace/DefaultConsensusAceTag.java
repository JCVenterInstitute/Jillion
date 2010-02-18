/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jcvi.assembly.Placed;

public class DefaultConsensusAceTag extends AbstractDefaultPlacedAceTag implements ConsensusAceTag{
    private final Set<String> comments;
    
    private DefaultConsensusAceTag(String id, String type, String creator,
            Date creationDate, Placed location, String data, Set<String> comments, boolean isTransient) {
        super(id, type, creator, creationDate, location, data, isTransient);
        this.comments = comments;
    }
    @Override
    public Set<String> getComments() {
        return comments;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((comments == null) ? 0 : comments.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof DefaultConsensusAceTag)) {
            return false;
        }
        DefaultConsensusAceTag other = (DefaultConsensusAceTag) obj;
        if (comments == null) {
            if (other.comments != null) {
                return false;
            }
        } else if (!comments.equals(other.comments)) {
            return false;
        }
        return true;
    }




    @Override
    public String toString() {
        return "DefaultConsensusAceTag [comments=" + comments + ", getEnd()="
                + getEnd() + ", getId()=" + getId() + ", getStart()="
                + getStart() + ", isTransient()=" + isTransient()
                + ", getCreationDate()=" + getCreationDate()
                + ", getCreator()=" + getCreator() + ", getData()=" + getData()
                + ", getType()=" + getType() + "]";
    }




    public static class Builder implements org.jcvi.Builder<DefaultConsensusAceTag>{
        private final Set<String> comments = new LinkedHashSet<String>();
        private final String id;
        
        private final Placed location;
        private final boolean isTransient;
        
        private final String type;
        private final String creator;
        private final Date creationDate;
        private final StringBuilder dataBuilder = new StringBuilder();
        
        
        
        /**
         * @param id
         * @param type
         * @param creator
         * @param creationDate
         * @param location
         * @param isTransient
         * @param data
         */
        public Builder(String id, String type, String creator,
                Date creationDate, Placed location, boolean isTransient) {
            this.id = id;
            this.location = location;
            this.isTransient = isTransient;
            this.type = type;
            this.creator = creator;
            this.creationDate = creationDate;
        }

        public Builder appendData(String data){
            dataBuilder.append(data);
            return this;
        }

        public Builder addComment(String comment){
            comments.add(comment);
            return this;
        }

        @Override
        public DefaultConsensusAceTag build() {
            return new DefaultConsensusAceTag(id, type, creator, 
                    creationDate, location, dataBuilder.toString(), 
                    comments, isTransient);
        }
        
    }
}
