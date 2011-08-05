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

import java.nio.ByteBuffer;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideGlyphCodec;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

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
    private static final DefaultNucleotideGlyphCodec CODEC = DefaultNucleotideGlyphCodec.INSTANCE;
    
    public byte[] compact(Nucleotide base, PhredQuality quality, Direction direction) {
        byte compacted = CODEC.encode(base)[4];
        if(direction == Direction.FORWARD){
            compacted = (byte)(compacted | 0x01);
        }
        byte[] ret = new byte[SIZE_OF_ENCODED_DATA];
        ret[0] = quality.getValue().byteValue();
        ret[1]= compacted;
        return ret;
    }

    public Nucleotide getBase(byte[] encodedData) {
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
    public Direction getSequenceDirection(byte[] encodedData) {
        if((byte)(encodedData[1] & 0x01) ==1){
            return Direction.FORWARD;
        }
        return Direction.REVERSE;
    }
}
