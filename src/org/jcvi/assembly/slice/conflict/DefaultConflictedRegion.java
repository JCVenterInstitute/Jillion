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
 * Created on Dec 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.conflict;

import org.jcvi.Range;
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

    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(ConflictedRegion o) {
        Range otherRange = Range.buildRange(o.getStart(), o.getEnd());
        Range range = Range.buildRange(getStart(), getEnd());
        return range.compareTo(otherRange);
    }

}
