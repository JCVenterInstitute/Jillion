/*
 * Created on Feb 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.sequence.SequenceDirection;

public class AssembledFrom {

    private final String id;
    private final SequenceDirection dir;
    private final int startOffset;
    /**
     * @param id
     * @param startOffset
     * @param complimented
     */
    public AssembledFrom(String id, int startOffset, SequenceDirection dir) {
        if(id ==null){
            throw new IllegalArgumentException("id can not be null");
        }
        this.id = id;
        this.startOffset = startOffset;
        this.dir = dir;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AssembledFrom))
            return false;
        AssembledFrom other = (AssembledFrom) obj;
        return id.equals(other.getId());
    }
    public String getId() {
        return id;
    }

    public int getStartOffset() {
        return startOffset;
    }
    
    public SequenceDirection getSequenceDirection(){
        return dir;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(id).append(" ").append(startOffset).append("is complimented? ").append(dir ==SequenceDirection.REVERSE);
        return builder.toString();
    }
    
    
}
