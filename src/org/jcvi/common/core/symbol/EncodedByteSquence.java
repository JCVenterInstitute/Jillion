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
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
/**
 * {@code EncodedByteSquence} encodes a {@link Sequence}
 * of {@link ByteSymbol}s.
 * @author dkatzel
 */
public class EncodedByteSquence implements Sequence<ByteSymbol>{

    private static final ByteGlyphFactory<ByteSymbol> FACTORY = new ByteGlyphFactory<ByteSymbol>(){

        @Override
        protected ByteSymbol createNewGlyph(Byte b) {
            return new ByteSymbol(b);
        }
        
    };
    private final byte[] data;
    public EncodedByteSquence(List<ByteSymbol> bytes){
        this.data = encode(bytes);
    }
    private byte[] encode(List<ByteSymbol> bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.size());
        for(ByteSymbol byteGlyph : bytes){
            buffer.put(byteGlyph.getNumber().byteValue());
        }
        return buffer.array();
    }
    
    
    @Override
    public List<ByteSymbol> decode() {
         return FACTORY.getGlyphsFor(data);
    }

    @Override
    public ByteSymbol get(int index) {
        return FACTORY.getGlyphFor(data[index]);
    }

    @Override
    public long getLength() {
        return data.length;
    }
    @Override
    public List<ByteSymbol> decode(Range range) {
        if(range==null){
            return decode();
        }
        List<ByteSymbol> result = new ArrayList<ByteSymbol>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<ByteSymbol> iterator() {
        return new ByteSequenceIterator();
    }
    
    private class ByteSequenceIterator implements Iterator<ByteSymbol>{
        private int i=0;

        @Override
        public boolean hasNext() {
            return i< getLength();
        }
        @Override
        public ByteSymbol next() {
            ByteSymbol next = get(i);
            i++;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("can not remove bytes");
            
        }
        
    }
    
    

}
