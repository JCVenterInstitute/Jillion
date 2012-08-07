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
 * Created on Aug 1, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.common.core.Range;
/**
 * {@code EncodedSequence} is a composite object
 * containing a byte representation of data and an {@link GlyphCodec}
 * to decode it.  This allows {@link Sequence} data to be encoded
 * in different forms to take up the minimal amount of memory
 * possible for a given situation.
 * @author dkatzel
 */
public class  EncodedSequence<T extends Symbol> implements Sequence<T> {
    /**
     * codec used to decode the data.
     */
    private GlyphCodec<T> codec;
    /**
     * Our data.
     */
    private byte[] data;
    /**
     * Convenience constructor.  This is
     * the same as calling 
     * <code>new DefaultEncodedGlyphs(codec, codec.encode(glyphsToEncode));</code>
     * @param codec
     * @param glyphsToEncode
     */
    public EncodedSequence(GlyphCodec<T> codec, Collection<T> glyphsToEncode) {
        this(codec, codec.encode(glyphsToEncode));
    }
    
    protected byte[] getData(){
        return data;
    }
    
    public GlyphCodec<T> getCodec(){
        return codec;
    }
    /**
     * @param codec
     * @param data
     */
    public EncodedSequence(GlyphCodec<T> codec, byte[] data) {
        this.codec = codec;
        //defensive copy
        this.data = Arrays.copyOf(data, data.length);
    }

    public List<T> asList(){
        return codec.decode(data);
    }
    public long getLength(){
        return codec.decodedLengthOf(data);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + codec.hashCode();
        result = prime * result + Arrays.hashCode(data);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof Sequence)){
            return false;
        }
        Sequence other = (Sequence) obj;
        Iterator<T> ourIter = iterator();
        Iterator<?> otherIter = other.iterator();
        while(ourIter.hasNext()){
        	if(!otherIter.hasNext()){
        		return false;
        	}
        	if(!ourIter.next().equals(otherIter.next())){
        		return false;
        	}
        }
        if(otherIter.hasNext()){
        	return false;
        }
        return true;
    }
    @Override
    public T get(int index) {
        return codec.decode(data, index);
    }
   
    @Override
    public String toString() {
    	Iterator<T> iter = iterator();
    	StringBuilder builder = new StringBuilder((int)getLength()*5);
    	while(iter.hasNext()){
    		if(builder.length()>0){
    			builder.append(" ,");
    		}
    		builder.append(iter.next());
    	}
    	return builder.toString();
    }
    /**
    * Default iterator returns the iterator from
    * the result of {@link #asList()}.  This method
    * should be overridden if a more efficient 
    * iterator could be generated.
    */
    @Override
    public Iterator<T> iterator() {
        return new RangedIterator();
    }
    /**
     * Default iterator iterates
     * over the objects in this sequence using
     * {@link #get(int)}. This method
     * should be overridden if a more efficient 
     * iterator could be generated.
     * {@inheritDoc}
     */
	@Override
	public Iterator<T> iterator(Range range) {
		if(range ==null){
			return iterator();
		}
		return new RangedIterator(range);
	}
    
	private final class RangedIterator implements Iterator<T>{
		private int currentOffset;
		private final int stop;
		
		public RangedIterator(){
			currentOffset=0;
			stop = (int)getLength();
		}
		public RangedIterator(Range r){
			Range maxRange = Range.createOfLength(getLength());
			if(!r.isSubRangeOf(maxRange)){
				throw new IndexOutOfBoundsException(
						String.format("range %s contains offsets that are out of bounds of %s", r, maxRange));
			}
			currentOffset=(int)r.getBegin();
			stop = (int)(r.getEnd()+1);
		}
		@Override
		public boolean hasNext() {
			return currentOffset<stop;
		}

		@Override
		public T next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			T next = get(currentOffset);
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("can not remove from immutable sequence");
			
		}
		
	}


}
