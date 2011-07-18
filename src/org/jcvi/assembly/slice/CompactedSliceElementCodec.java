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

import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.glyph.nuc.DefaultNucleotideGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

/**
 * {@code CompactedSliceElementCodec} is a codec that 
 * encodes a slice's basecall, quality and direction into
 * a total of 2 bytes.  Methods are provided for pulling that
 * data back out.
 * @author dkatzel
 *
 *
 */
enum CompactedSliceElementCodec {
    INSTANCE
    ;
    public static final int SIZE_OF_ENCODED_DATA =2;
    private static final DefaultNucleotideGlyphCodec CODEC = DefaultNucleotideGlyphCodec.getInstance();
    
    public byte[] compact(NucleotideGlyph base, PhredQuality quality, SequenceDirection direction) {
        byte compacted = CODEC.encode(base)[4];
        if(direction == SequenceDirection.FORWARD){
            compacted = (byte)(compacted | 0x01);
        }
        byte[] ret = new byte[SIZE_OF_ENCODED_DATA];
        ret[0] = quality.getNumber().byteValue();
        ret[1]= compacted;
        return ret;
    }

    public NucleotideGlyph getBase(byte[] encodedData) {
       ByteBuffer buf = ByteBuffer.allocate(5);
       buf.putInt(2);
       buf.put(encodedData[1]);
       return CODEC.decode(buf.array(), 0);
    }

    /**
    * {@inheritDoc}
    */
    public PhredQuality getQuality(byte[] encodedData) {
        return PhredQuality.valueOf(encodedData[0]);
    }

    /**
    * {@inheritDoc}
    */
    public SequenceDirection getSequenceDirection(byte[] encodedData) {
        if((byte)(encodedData[1] & 0x01) ==1){
            return SequenceDirection.FORWARD;
        }
        return SequenceDirection.REVERSE;
    }
}
