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

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
/**
 * {@code EncodedShortGlyph} encodes a {@link Sequence}
 * of {@link ShortSymbol}s.
 * @author dkatzel
 */
public class EncodedShortSymbol implements Sequence<ShortSymbol>{

    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private final short[] data;
    public EncodedShortSymbol(List<ShortSymbol> shorts){
        this.data = encode(shorts);
    }
    private short[] encode(List<ShortSymbol> shorts) {
        ShortBuffer buffer = ShortBuffer.allocate(shorts.size());
        for(ShortSymbol byteGlyph : shorts){
            buffer.put(byteGlyph.getValue().shortValue());
        }
        return buffer.array();
    }
    
    
    @Override
    public List<ShortSymbol> asList() {
         return FACTORY.getGlyphsFor(data);
    }

    @Override
    public ShortSymbol get(int index) {
        return FACTORY.getGlyphFor(data[index]);
    }

    @Override
    public long getLength() {
        return data.length;
    }
    @Override
    public List<ShortSymbol> asList(Range range) {
        if(range==null){
            return asList();
        }
        List<ShortSymbol> result = new ArrayList<ShortSymbol>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
     @Override
     public Iterator<ShortSymbol> iterator() {
         return new ShortSequenceIterator();
     }
     /**
      * {@inheritDoc}
      */
      @Override
      public Iterator<ShortSymbol> iterator(Range range) {
          return new ShortSequenceIterator(range);
      }
     private class ShortSequenceIterator implements Iterator<ShortSymbol>{
         private int i=0;
         private final long length;
         
         ShortSequenceIterator(){
        	 this.i=0;
        	 this.length = getLength();
         }
         ShortSequenceIterator(Range range){
        	 if(range.isSubRangeOf(Range.createOfLength(getLength()))){
        		 throw new IndexOutOfBoundsException("range "+ range + " is out of bounds of sequence "+ Range.createOfLength(getLength()));
        	 }
        	 this.i=(int)range.getBegin();
        	 this.length = range.getLength();
         }
         @Override
         public boolean hasNext() {
             return i< length;
         }
         @Override
         public ShortSymbol next() {
             ShortSymbol next = get(i);
             i++;
             return next;
         }

         @Override
         public void remove() {
             throw new UnsupportedOperationException("can not remove shorts");
             
         }
         
     }
}
