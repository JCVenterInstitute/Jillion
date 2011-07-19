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
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Range;
/**
 * {@code EncodedByteSquence} encodes a {@link Sequence}
 * of {@link ByteGlyph}s.
 * @author dkatzel
 */
public class EncodedByteSquence implements Sequence<ByteGlyph>{

    private static final ByteGlyphFactory<ByteGlyph> FACTORY = new ByteGlyphFactory<ByteGlyph>(){

        @Override
        protected ByteGlyph createNewGlyph(Byte b) {
            return new ByteGlyph(b);
        }
        
    };
    private final byte[] data;
    public EncodedByteSquence(List<ByteGlyph> bytes){
        this.data = encode(bytes);
    }
    private byte[] encode(List<ByteGlyph> bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.size());
        for(ByteGlyph byteGlyph : bytes){
            buffer.put(byteGlyph.getNumber().byteValue());
        }
        return buffer.array();
    }
    
    
    @Override
    public List<ByteGlyph> decode() {
         return FACTORY.getGlyphsFor(data);
    }

    @Override
    public ByteGlyph get(int index) {
        return FACTORY.getGlyphFor(data[index]);
    }

    @Override
    public long getLength() {
        return data.length;
    }
    @Override
    public List<ByteGlyph> decode(Range range) {
        if(range==null){
            return decode();
        }
        List<ByteGlyph> result = new ArrayList<ByteGlyph>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

}
