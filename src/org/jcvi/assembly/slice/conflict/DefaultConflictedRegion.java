/*
 * Created on Dec 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.conflict;

import org.jcvi.assembly.Placed;

public class DefaultConflictedRegion implements ConflictedRegion {

    private final Placed placed;
    private final Conflict conflict;
    
    /**
     * @param conflict
     * @param placed
     */
    public DefaultConflictedRegion(Conflict conflict, Placed placed) {
        this.conflict = conflict;
        this.placed = placed;
    }

    @Override
    public Conflict getConflict() {
        return conflict;
    }

    @Override
    public long getEnd() {
        return placed.getEnd();
    }

    @Override
    public long getLength() {
        return placed.getLength();
    }

    @Override
    public long getStart() {
        return placed.getStart();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((conflict == null) ? 0 : conflict.hashCode());
        result = prime * result + ((placed == null) ? 0 : placed.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultConflictedRegion))
            return false;
        DefaultConflictedRegion other = (DefaultConflictedRegion) obj;
        if (conflict == null) {
            if (other.conflict != null)
                return false;
        } else if (!conflict.equals(other.conflict))
            return false;
        if (placed == null) {
            if (other.placed != null)
                return false;
        } else if (!placed.equals(other.placed))
            return false;
        return true;
    }

}
