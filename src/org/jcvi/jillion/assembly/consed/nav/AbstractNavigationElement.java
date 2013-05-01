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
package org.jcvi.jillion.assembly.consed.nav;

import org.jcvi.jillion.core.Range;

/**
 * {@code AbstractNavigationElement} is an abstract
 * implementation of {@link NavigationElement}
 * that manages all the common fields of a {@link NavigationElement}.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractNavigationElement implements NavigationElement{

    private final Type type;
    private final Range ungappedPositionRange;
    private final String comment;
    private final String targetId;
    
    /**
     * Constructs a new {@link NavigationElement}.
     * @param type the Type of the element; cannot be null.
     * @param targetId the id of the target of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     */
    public AbstractNavigationElement(Type type, String targetId,
            Range ungappedPositionRange, String comment) {
        if(type ==null){
            throw new NullPointerException("type can not be null");
        }
        if(targetId ==null){
            throw new NullPointerException("element id can not be null");
        }
        if(ungappedPositionRange ==null){
            throw new NullPointerException("ungappedPositionRange can not be null");
        }
        this.type = type;
        this.targetId = targetId;
        this.ungappedPositionRange = ungappedPositionRange;
        this.comment = comment;
    }
    /**
     * Convenience constructor.  This is the same as
     * {@link #AbstractNavigationElement(org.jcvi.jillion.assembly.ace.consed.NavigationElement.Type, String, Range, String)
     * new AbstractNavigationElement(type, elementId, ungappedPositionRange, null)}
     * @param type the Type of the element; cannot be null.
     * @param elementId the id of the element of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     * @see #AbstractNavigationElement(org.jcvi.jillion.assembly.ace.consed.NavigationElement.Type, String, Range, String)
     */
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
    public String getTargetId() {
        return targetId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result+ targetId.hashCode();
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
       if (!targetId.equals(other.targetId)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if(!ungappedPositionRange.equals(other.ungappedPositionRange)) {
            return false;
        }
        return true;
    }
    
    

}
