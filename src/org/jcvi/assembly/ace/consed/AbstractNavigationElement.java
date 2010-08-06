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

package org.jcvi.assembly.ace.consed;

import org.jcvi.Range;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractNavigationElement implements NavigationElement{

    private final Type type;
    private final Range ungappedPositionRange;
    private final String comment;
    private final String elementId;
    
    /**
     * @param type
     * @param elementId
     * @param ungappedPositionRange
     * @param comment
     */
    public AbstractNavigationElement(Type type, String elementId,
            Range ungappedPositionRange, String comment) {
        if(type ==null){
            throw new NullPointerException("type can not be null");
        }
        if(elementId ==null){
            throw new NullPointerException("element id can not be null");
        }
        if(ungappedPositionRange ==null){
            throw new NullPointerException("ungappedPositionRange can not be null");
        }
        this.type = type;
        this.elementId = elementId;
        this.ungappedPositionRange = ungappedPositionRange;
        this.comment = comment;
    }
    public AbstractNavigationElement(Type type, String elementId,
            Range ungappedPositionRange){
        this(type, elementId, ungappedPositionRange,null);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Type getType() {
        return type;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range getUngappedPositionRange() {
        return ungappedPositionRange;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String getComment() {
        return comment;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String getElementId() {
        return elementId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result+ elementId.hashCode();
        result = prime * result + type.hashCode();
        result = prime * result + ungappedPositionRange.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractNavigationElement)) {
            return false;
        }
        AbstractNavigationElement other = (AbstractNavigationElement) obj;
        if (comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
       else if (!elementId.equals(other.elementId)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (ungappedPositionRange == null) {
            if (other.ungappedPositionRange != null) {
                return false;
            }
        } else if (!ungappedPositionRange.equals(other.ungappedPositionRange)) {
            return false;
        }
        return true;
    }
    
    

}
