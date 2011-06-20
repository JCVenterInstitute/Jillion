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
    /**
     * Convenience constructor, defaults direction to {@link SequenceDirection#FORWARD}
     * @param id the id of the contig to be placed.
     * @param range the range this contig should be placed on the scaffold.
     */
    public DefaultPlacedContig(String id, Range range){
        this(id,range,SequenceDirection.FORWARD);
    }
    /**
     * Constructs a new DefaultPlacedContig.
     * @param id the id of the contig to be placed.
     * @param range the range this contig should be placed on the scaffold.
     * @param direction the direction this contig faces.
     */
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
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof DefaultPlacedContig)){
            return false;
        }
        DefaultPlacedContig other = (DefaultPlacedContig) obj;
        if (contigId == null) {
            if (other.contigId != null){
                return false;
            }
        } else if (!contigId.equals(other.contigId)){
            return false;            
        }
        if (direction == null) {
            if (other.direction != null){
                return false;
            }
        } else if (!direction.equals(other.direction)){
            return false;            
        }
        if (range == null) {
            if (other.range != null)
                return false;
        } else if (!range.equals(other.range)){
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "DefaultPlacedContig [contigId=" + contigId + ", direction="
                + direction + ", range=" + range + "]";
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedContig o) {       
        return range.compareTo(o.asRange());
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return range;
    }

}
