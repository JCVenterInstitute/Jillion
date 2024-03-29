/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Aug 1, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
/**
 * {@code EncodedSequence} is a composite object
 * containing a byte representation of data and an {@link GlyphCodec}
 * to decode it.  This allows {@link Sequence} data to be encoded
 * in different forms to take up the minimal amount of memory
 * possible for a given situation.
 * @author dkatzel
 */
public abstract class  EncodedSequence<T> implements Sequence<T> {
    /**
     * codec used to decode the data.
     */
    private final GlyphCodec<T> codec;
    /**
     * Our data.
     */
    protected final byte[] data;
    
    private int hash;
    /**
     * Create a new EncodedSequence instance.
     * @param codec the {@link GlyphCodec} to use to encode/decode
     * the sequence data.
     * @param data the encoded data for this sequence that has already
     * been encoded by the same {@link GlyphCodec}.
     */
    public EncodedSequence(GlyphCodec<T> codec, byte[] data) {
        this.codec = codec;
        //defensive copy
        this.data = Arrays.copyOf(data, data.length);
    }
    @Override
    public long getLength(){
        return codec.decodedLengthOf(data);
    }

    protected GlyphCodec<T> getCodec() {
		return codec;
	}
	@Override
	public int hashCode() {
		if(hash==0 && getLength() >0){
	        final int prime = 31;
	        int result = 1;
	        Iterator<T> iter = iterator();
	        while(iter.hasNext()){
	        	result = prime * result + iter.next().hashCode();
	        }
	        hash= result;
		}
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Sequence)) {
			return false;
		}
		Sequence<?> other = (Sequence<?>) obj;
		if(getLength() !=other.getLength()){
			return false;
		}
		Iterator<T> iter = iterator();
		Iterator<?> otherIter = other.iterator();
		while(iter.hasNext()){
			if(!iter.next().equals(otherIter.next())){
				return false;
			}
		}
		return true;
	}

    @Override
    public T get(long index) {
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
     * Default iterator iterates
     * over the objects in this sequence using
     * {@link #get(long)}. This method
     * should be overridden if a more efficient 
     * iterator could be generated.
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new RangedIterator();
    }
    /**
     * Default iterator iterates
     * over the objects in this sequence using
     * {@link #get(long)}. This method
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
			Range maxRange = new Range.Builder(getLength()).build();
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
		
	}


}
