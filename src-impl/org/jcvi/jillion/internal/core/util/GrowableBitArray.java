/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.core.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.jcvi.jillion.core.Range;
/**
 * A {@code GrowableBitArray} is a utility class
 * that wraps an array that will dynamically
 * grow as needed when data is
 * appended, inserted, replaced and removed etc.
 * This is similar to an {@link ArrayList}
 * or {@link StringBuilder}
 * for primitive bits.
 * This class is not Thread-safe.
 * @author dkatzel
 *
 */
public final class GrowableBitArray {
	/**
	 * The current length of valid data
	 * this is not the same as the length
	 * of the byte array (capacity) since
	 * there still might be room to grow.
	 * There might even be old data in the array
	 * past current length if the array
	 * has been modified via the {@link #remove(int)}
	 * methods.
	 */
	private int currentLength=0;
	/**
	 * Our actual byte array,
	 * the capacity is the size of the array.
	 */
	private boolean[] data;
	/**
	 * Creates a new {@link GrowableBitArray}
	 * with the given initial capacity.
	 * @param initialCapacity the initial size 
	 * of the backing byte array.  When adding
	 * bytes will cause the byte array to overflow,
	 * the backing byte array will automatically
	 * grow larger.
	 * @throws IllegalArgumentException if initialCapacity is <=0.
	 */
	public GrowableBitArray(int initialCapacity){
		if(initialCapacity <=0){
			throw new IllegalArgumentException("initial capacity should be > 0 :"+initialCapacity);
		}
		data = new boolean[initialCapacity];		
	}
	/**
	 * Creates a new {@link GrowableBitArray}
	 * where the backing byte array is an exact
	 * copy of the input array and the initial
	 * capacity is set to the array length.
	 * This has similar (although optimized)
	 * functionality to
	 * <pre>
	 * byte[] bytes = ...
	 * GrowableBitArray gba = new GrowableBitArray(bytes.length);
	 * gba.append(bytes);
	 * </pre>
	 * @param bytes the initial byte values to set
	 * to the backing array.
	 * @throws NullPointerException if bytes is null.
	 */
	public GrowableBitArray(boolean[] bytes){
		data = Arrays.copyOf(bytes, bytes.length);
		currentLength=data.length;
	}
	private GrowableBitArray(GrowableBitArray copy){
		data = Arrays.copyOf(copy.data, copy.data.length);
		currentLength = copy.currentLength;
	}
	/**
	 * Create a new instance of GrowableBitArray
	 * that is an exact copy of this instance.
	 * Any future modifications to either the original
	 * instance or the copy will NOT be reflected 
	 * in the other.
	 * @return a new instance of GrowableBitArray
	 * that contains the same data as this instance
	 * currently does.
	 */
	public GrowableBitArray copy(){
		return new GrowableBitArray(this);
	}
	private void assertValidOffset(int offset) {
		if (offset <0 || offset >= currentLength){
		    throw new IndexOutOfBoundsException(
			"Index: "+offset+", Size: "+currentLength);
	    }
	}
	private void assertValidRange(Range range) {
		if (range.getBegin()<0 || range.getEnd() >= currentLength){
		    throw new IndexOutOfBoundsException(
			"range: "+range+", array size: "+currentLength);
	    }
	}
	
	public void reverse(){
		int pivotPoint = currentLength/2;
		for(int i=0; i<pivotPoint;i++){
			boolean temp=data[i];
			int reverseI = currentLength-i-1;
			data[i] = data[reverseI];
			data[reverseI] = temp;
		}
	}
	public int getCurrentLength() {
		return currentLength;
	}

	public void append(boolean value){
		ensureCapacity(currentLength+1);
		data[currentLength++]=value;
	}
	
	public void append(boolean[] values){
		ensureCapacity(currentLength+values.length);
		System.arraycopy(values, 0, data, currentLength, values.length);
		currentLength+=values.length;
	}
	public void append(GrowableBitArray other){
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(other.data, 0, data, currentLength, other.currentLength);
		currentLength+=other.currentLength;
	}
	public boolean get(int offset){
		assertValidOffset(offset);
		return data[offset];
	}
	
	public void prepend(boolean value){
		insert(0,value);
	}
	
	public void prepend(boolean[] values){
		insert(0,values);
	}
	public void prepend(GrowableBitArray other){
		insert(0,other);
	}
	public void replace(int offset, boolean value){
		assertValidOffset(offset);
		data[offset]=value;
	}
	public void insert(int offset, boolean[] values){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+values.length);
		System.arraycopy(data, offset, data, offset + values.length,
				currentLength - offset);
		
		System.arraycopy(values, 0, data, offset, values.length);
		currentLength+=values.length;
		
	}
	
	public void insert(int offset, GrowableBitArray other){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(data, offset, data, offset + other.currentLength,
				currentLength - offset);
		
		System.arraycopy(other.data, 0, data, offset, other.currentLength);
		currentLength+=other.currentLength;
		
	}
	public void insert(int offset, boolean value){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+1);
		System.arraycopy(data, offset, data, offset + 1,
				currentLength - offset);
		data[offset] = value;
		currentLength++;
	}
	private void assertValidInsertOffset(int offset) {
		//inserts allow offset to be length
		if(offset !=currentLength){
			assertValidOffset(offset);
		}
		
		
	}
	public void remove(Range range){
		assertValidRange(range);
		int numMoved = currentLength -(int)range.getBegin()-(int) range.getLength();
		if (numMoved > 0){
			System.arraycopy(data, (int)range.getEnd()+1, 
					data, (int)range.getBegin(),  numMoved);
		}
		currentLength-=(int)range.getLength();    
	}
	public boolean remove(int offset){
		assertValidOffset(offset);
		boolean oldValue = data[offset];

		int numMoved = currentLength - offset - 1;
		if (numMoved > 0){
		    System.arraycopy(data, offset+1, data, offset,    numMoved);
		}
		currentLength--;
		return oldValue;
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		if (minCapacity > oldCapacity) {
		    //algorithm borrowed from ArrayList
		    int newCapacity = (oldCapacity * 3)/2 + 1;
    	    if (newCapacity < minCapacity){
    	    	newCapacity = minCapacity;
    	    }
            // minCapacity is usually close to size, so this is a win:
            data = Arrays.copyOf(data, newCapacity);
		}
    }
	
	public boolean[] toArray(){
		return Arrays.copyOf(data,currentLength);
	}
}
