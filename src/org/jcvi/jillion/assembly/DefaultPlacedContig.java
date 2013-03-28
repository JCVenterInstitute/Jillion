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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

public class DefaultPlacedContig implements PlacedContig{
    private final String contigId;
    private final Range range;
    private final Direction direction;
    /**
     * Convenience constructor, defaults direction to {@link Direction#FORWARD}
     * @param id the id of the contig to be placed.
     * @param range the range this contig should be placed on the scaffold.
     */
    public DefaultPlacedContig(String id, Range range){
        this(id,range,Direction.FORWARD);
    }
    /**
     * Constructs a new DefaultPlacedContig.
     * @param id the id of the contig to be placed.
     * @param range the range this contig should be placed on the scaffold.
     * @param direction the direction this contig faces.
     */
    public DefaultPlacedContig(String id, Range range,Direction direction){
    	if(id ==null){
    		throw new NullPointerException("id can not be null");
    	}
    	if(range ==null){
    		throw new NullPointerException("range can not be null");
    	}
    	if(direction ==null){
    		throw new NullPointerException("direction can not be null");
    	}
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
        return range.getLength();
    }

    @Override
    public long getBegin() {
        return range.getBegin();
    }
    @Override
    public Range getValidRange() {
        return range;
    }
    @Override
    public Direction getDirection() {
        return direction;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + contigId.hashCode();
        result = prime * result
                + direction.hashCode();
        result = prime * result + range.hashCode();
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
        if (!contigId.equals(other.contigId)){
            return false;            
        }
        if (!direction.equals(other.direction)){
            return false;            
        }
        if (!range.equals(other.range)){
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
    public Range asRange() {
        return range;
    }

}
