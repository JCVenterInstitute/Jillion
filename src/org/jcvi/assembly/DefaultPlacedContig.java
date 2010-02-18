/*
 * Created on Mar 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;

public class DefaultPlacedContig implements PlacedContig{
    private final String contigId;
    private final Range range;
    private final SequenceDirection direction;
    
    public DefaultPlacedContig(String id, Range range,SequenceDirection direction){
        contigId = id;
        this.range = range;
        this.direction = direction;
    }
    @Override
    public String getContigId() {
        return contigId;
    }

    @Override
    public long getEnd() {
        return range.getEnd();
    }

    @Override
    public long getLength() {
        return range.size();
    }

    @Override
    public long getStart() {
        return range.getStart();
    }
    @Override
    public Range getValidRange() {
        return range;
    }
    @Override
    public SequenceDirection getSequenceDirection() {
        return direction;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((contigId == null) ? 0 : contigId.hashCode());
        result = prime * result
                + ((direction == null) ? 0 : direction.hashCode());
        result = prime * result + ((range == null) ? 0 : range.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultPlacedContig))
            return false;
        DefaultPlacedContig other = (DefaultPlacedContig) obj;
        if (contigId == null) {
            if (other.contigId != null)
                return false;
        } else if (!contigId.equals(other.contigId))
            return false;
        if (direction == null) {
            if (other.direction != null)
                return false;
        } else if (!direction.equals(other.direction))
            return false;
        if (range == null) {
            if (other.range != null)
                return false;
        } else if (!range.equals(other.range))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "DefaultPlacedContig [contigId=" + contigId + ", direction="
                + direction + ", range=" + range + "]";
    }

}
