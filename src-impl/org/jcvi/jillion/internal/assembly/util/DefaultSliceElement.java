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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.assembly.util;

import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.ObjectsUtil;

public class DefaultSliceElement implements SliceElement {
    private final Nucleotide base;
    private final PhredQuality quality;
    private final Direction direction;
    private final String name;
    /**
     * @param name
     * @param base
     * @param quality
     * @param direction
     * @throws IllegalArgumentException if any parameter is null.
     */
    public DefaultSliceElement(String name, Nucleotide base, PhredQuality quality,
            Direction direction) {
        if(name ==null ||base ==null || quality ==null || direction == null){
            throw new IllegalArgumentException("fields can not be null");
        }
        this.name = name;
        this.base = base;
        this.quality = quality;
        this.direction = direction;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        result = prime * result + base.hashCode();
        result = prime * result
                + direction.hashCode();
        result = prime * result + quality.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof SliceElement)){
            return false;
        }
        SliceElement other = (SliceElement) obj;
        return 
        ObjectsUtil.nullSafeEquals(getId(), other.getId()) 
        && ObjectsUtil.nullSafeEquals(getBase(), other.getBase())
        && ObjectsUtil.nullSafeEquals(getQuality(), other.getQuality())
        && ObjectsUtil.nullSafeEquals(getDirection(), other.getDirection());
       
    }

    @Override
    public Nucleotide getBase() {
        return base;
    }

    @Override
    public PhredQuality getQuality() {
        return quality;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("name %s %s (%d) %s",
                getId(),
                base.toString(),
                quality.getQualityScore(),
                direction);
        
    }

    @Override
    public String getId() {
        return name;
    }

    
}
