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

package org.jcvi.assembly.slice;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.glyph.nuc.DefaultNucleotideGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public class CompactedSliceElement implements SliceElement{

    private static final DefaultNucleotideGlyphCodec CODEC = DefaultNucleotideGlyphCodec.getInstance();
    private String id;
    private byte[] encodedData = new byte[2];
    
    public CompactedSliceElement(String id, NucleotideGlyph base, PhredQuality quality,
            SequenceDirection direction) {
        if(id ==null ||base ==null || quality ==null || direction == null){
            throw new IllegalArgumentException("fields can not be null");
        }
        this.id= id;
        encodedData[0] = quality.getNumber().byteValue();
        encodedData[1]= compact(base, direction);
    }
    /**
     * @param base
     * @param direction
     * @return
     */
    private byte compact(NucleotideGlyph base, SequenceDirection direction) {
        byte compacted=CODEC.encode(Arrays.asList(base))[4];
        if(direction == SequenceDirection.FORWARD){
            compacted = (byte)(compacted | 0x01);
        }
        return compacted;
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
       ByteBuffer buf = ByteBuffer.allocate(5);
       buf.putInt(2);
       buf.put(encodedData[1]);
       return CODEC.decode(buf.array(), 0);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public PhredQuality getQuality() {
        return PhredQuality.valueOf(encodedData[0]);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public SequenceDirection getSequenceDirection() {
        if((byte)(encodedData[1] & 0x01) ==1){
            return SequenceDirection.FORWARD;
        }
        return SequenceDirection.REVERSE;
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
        if (!(obj instanceof CompactedSliceElement)) {
            return false;
        }
        CompactedSliceElement other = (CompactedSliceElement) obj;
        if (!Arrays.equals(encodedData, other.encodedData)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
    
    

}
