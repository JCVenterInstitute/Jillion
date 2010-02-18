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
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.DefaultLocation;
import org.jcvi.assembly.Location;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultSliceLocation<T> extends DefaultLocation<T> implements SliceLocation<T> {

    private final PhredQuality quality;
    
    public DefaultSliceLocation(Location<T> location, PhredQuality quality){
        this(location.getSource(), location.getIndex(), quality);
    }
    public DefaultSliceLocation(T source, int index, PhredQuality quality) {
        super(source, index);
        if(quality == null){
            throw new IllegalArgumentException("quality can not be null");
        }
        this.quality = quality;
    }

    @Override
    public PhredQuality getQuality() {
        return quality;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(! super.equals(obj)){
            return false;
        }
        if (!(obj instanceof SliceLocation)){
            return false;
        }
        SliceLocation other = (SliceLocation) obj;
        return other.getQuality().equals(this.quality);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + quality.hashCode();
        return result;
    }

   

   

}
