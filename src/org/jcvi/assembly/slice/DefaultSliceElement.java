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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.CommonUtil;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.jcvi.common.core.seq.read.SequenceDirection;

public class DefaultSliceElement implements SliceElement {
    private final NucleotideGlyph base;
    private final PhredQuality quality;
    private final SequenceDirection direction;
    private final String name;
    /**
     * @param name
     * @param base
     * @param quality
     * @param direction
     * @throws IllegalArgumentException if any parameter is null.
     */
    public DefaultSliceElement(String name, NucleotideGlyph base, PhredQuality quality,
            SequenceDirection direction) {
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
        CommonUtil.similarTo(getId(), other.getId()) &&
        CommonUtil.similarTo(getBase(), other.getBase()) &&
        CommonUtil.similarTo(getQuality(), other.getQuality()) &&
        CommonUtil.similarTo(getSequenceDirection(), other.getSequenceDirection());
       
    }

    @Override
    public NucleotideGlyph getBase() {
        return base;
    }

    @Override
    public PhredQuality getQuality() {
        return quality;
    }

    @Override
    public SequenceDirection getSequenceDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("name %s %s (%d) %s",
                getId(),
                base.getName(),
                quality.getNumber(),
                direction);
        
    }

    @Override
    public String getId() {
        return name;
    }

    
}
