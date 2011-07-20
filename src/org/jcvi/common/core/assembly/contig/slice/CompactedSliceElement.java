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

package org.jcvi.common.core.assembly.contig.slice;

import java.util.Arrays;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class CompactedSliceElement implements SliceElement{

    private String id;
    private byte[] encodedData = new byte[2];
    public CompactedSliceElement(String id, byte[] encodedData){
        if(id ==null){
            throw new NullPointerException("fields can not be null");
        }
        if(encodedData.length !=2){
            throw new IllegalArgumentException("invalid encoded data");
        }
        this.id = id;
        System.arraycopy(encodedData, 0, this.encodedData, 0, 2);
    }
    public CompactedSliceElement(String id, NucleotideGlyph base, PhredQuality quality,
            Direction direction) {
        if(id ==null ||base ==null || quality ==null || direction == null){
            throw new NullPointerException("fields can not be null");
        }
        this.id= id;
        encodedData = CompactedSliceElementCodec.INSTANCE.compact(base, quality, direction);
    }
   
    /**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return id;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideGlyph getBase() {
        return CompactedSliceElementCodec.INSTANCE.getBase(encodedData);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public PhredQuality getQuality() {
        return CompactedSliceElementCodec.INSTANCE.getQuality(encodedData);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Direction getSequenceDirection() {
        return CompactedSliceElementCodec.INSTANCE.getSequenceDirection(encodedData);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(encodedData);
        result = prime * result + id.hashCode();
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
        if (!(obj instanceof SliceElement)) {
            return false;
        }
        SliceElement other = (SliceElement) obj;
        if (!id.equals(other.getId())) {
            return false;
        }
        if(!getQuality().equals(other.getQuality())){
            return false;
        }
        if(!getBase().equals(other.getBase())){
            return false;
        }
        if(!getSequenceDirection().equals(other.getSequenceDirection())){
            return false;
        }
        return true;
    }
    
    

}
