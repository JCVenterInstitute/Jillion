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
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;

abstract class AbstractDefaultPlacedAceTag extends AbstractDefaultAceTag implements RangeableAceTag{
    private final String id;
    
    private final Rangeable location;
    private final boolean isTransient;
    
    /**
     * @param id
     * @param type
     * @param creator
     * @param creationDate
     * @param location
     * @param data
     */
    public AbstractDefaultPlacedAceTag(String id, String type, String creator,
            Date creationDate, Rangeable location, String data,boolean isTransient) {
        super(type, creator, creationDate, data);
        this.id = id;
        this.location = location;
        this.isTransient = isTransient;
    }

   
    /**
     * Get the Id of read
     * this tag references.
     */
    @Override
    public String getId() {
        return id;
    }



    @Override
    public boolean isTransient() {
        return isTransient;
    }



    @Override
    public Range asRange() {
        return location.asRange();
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (isTransient ? 1231 : 1237);
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
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
        if (!(obj instanceof AbstractDefaultPlacedAceTag)) {
            return false;
        }
        AbstractDefaultPlacedAceTag other = (AbstractDefaultPlacedAceTag) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (isTransient != other.isTransient) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        return true;
    }
    

}
