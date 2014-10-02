/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.LongStream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.iter.PrimitiveArrayIterators;
/**
 * A {@code GrowableLongArray} is a utility class
 * that wraps a long[] that will dynamically
 * grow as needed when data is
 * appended, inserted, replaced and removed etc.
 * This is similar to an {@link ArrayList}
 * or {@link StringBuilder}
 * for primitive longs.
 * This class is not Thread-safe.
 * @author dkatzel
 *
 */
public final class GrowableLongArray implements Iterable<Long>{
	/**
	 * The current length of valid data
	 * this is not the same as the length
	 * of the array (capacity) since
	 * there still might be room to grow.
	 * There might even be old data in the array
	 * past current length if the array
	 * has been modified via the {@link #remove(int)}
	 * methods.
	 */
	private int currentLength=0;
	/**
	 * Our actual long array,
	 * the capacity is the size of the array.
	 */
	private long[] data;
	/**
	 * Creates a new {@link GrowableByteArray}
	 * with the given initial capacity.
	 * @param initialCapacity the initial size 
	 * of the backing long array.  When adding
	 * longs will cause the long array to overflow,
	 * the backing long array will automatically
	 * grow larger.
	 * @throws IllegalArgumentException if initialCapacity is <0.
	 */
	public GrowableLongArray(int initialCapacity){
		if(initialCapacity <0){
			throw new IllegalArgumentException("initial capacity should be >= 0 :"+initialCapacity);
		}
		data = new long[initialCapacity];		
	}
	
	/**
	 * Creates a new {@link GrowableLongArray}
	 * where the backing array contains
	 * the contents of the given Collection
	 * stored as primitives.  The order in the array
	 * is the determined by the Collection's iteration order.
	 * The capacity and length of this growable array
	 * are set to the collection's size.
	 * @param shorts the Collection of Integers to 
	 * create into a growable array from.
	 * @throws NullPointerException if the given
	 * collection is null or any elements in the collection
	 * are null.
	 */
	public GrowableLongArray(Collection<Long> longs){
		data = new long[longs.size()];
		int index=0;
		for(Long i : longs){
			data[index]=i.longValue();
			index++;
		}
		currentLength=data.length;
	}
	
	
	/**
	 * Creates a new {@link GrowableLongArray}
	 * where the backing long array is an exact
	 * copy of the input array and the initial
	 * capacity is set to the array length.
	 * This has similar (although optimized)
	 * functionality to
	 * <pre>
	 * long[] longs = ...
	 * GrowableByteArray gba = new GrowableByteArray(longs.length);
	 * gba.append(bytes);
	 * </pre>
	 * @param longs the initial values to set
	 * to the backing array.
	 * @throws NullPointerException if longs is null.
	 */
	public GrowableLongArray(long[] longs){
		data = Arrays.copyOf(longs, longs.length);
		currentLength=data.length;
	}
	private GrowableLongArray(GrowableLongArray copy){
		data = Arrays.copyOf(copy.data, copy.data.length);
		currentLength = copy.currentLength;
	}
	/**
	 * Create a new instance of GrowableByteArray
	 * that is an exact copy of this instance.
	 * Any future modifications to either the original
	 * instance or the copy will NOT be reflected 
	 * in the other.
	 * @return a new instance of GrowableLongArray
	 * that contains the same data as this instance
	 * currently does.
	 */
	public GrowableLongArray copy(){
		return new GrowableLongArray(this);
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
			long temp=data[i];
			int reverseI = currentLength-i-1;
			data[i] = data[reverseI];
			data[reverseI] = temp;
		}
	}
	public int getCurrentLength() {
		return currentLength;
	}

	public void append(long value){
		ensureCapacity(currentLength+1);
		data[currentLength++]=value;
	}
	
	public void append(long[] values){
		ensureCapacity(currentLength+values.length);
		System.arraycopy(values, 0, data, currentLength, values.length);
		currentLength+=values.length;
	}
	public void append(GrowableLongArray other){
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(other.data, 0, data, currentLength, other.currentLength);
		currentLength+=other.currentLength;
	}
	public long get(int offset){
		assertValidOffset(offset);
		return data[offset];
	}
	
	public void prepend(long value){
		insert(0,value);
	}
	
	public void prepend(long[] values){
		insert(0,values);
	}
	public void prepend(GrowableLongArray other){
		insert(0,other);
	}
	public void replace(int offset, long value){
		assertValidOffset(offset);
		data[offset]=value;
	}
	public void insert(int offset, long[] values){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+values.length);
		System.arraycopy(data, offset, data, offset + values.length,
				currentLength - offset);
		
		System.arraycopy(values, 0, data, offset, values.length);
		currentLength+=values.length;
		
	}
	
	public void insert(int offset, GrowableLongArray other){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(data, offset, data, offset + other.currentLength,
				currentLength - offset);
		
		System.arraycopy(other.data, 0, data, offset, other.currentLength);
		currentLength+=other.currentLength;
		
	}
	public void insert(int offset, long value){
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
	public long remove(int offset){
		assertValidOffset(offset);
		long oldValue = data[offset];

		int numMoved = currentLength - offset - 1;
		if (numMoved > 0){
		    System.arraycopy(data, offset+1, data, offset,    numMoved);
		}
		currentLength--;
		return oldValue;
	}
	/**
	 * Get the current capacity of the backing
	 * array.  This may be larger than the value
	 * returned by {@link #getCurrentLength()}.
	 * Modifying this growable array to extend
	 * beyond the current capacity will require
	 * the backing array to be grown.
	 * @return the current capacity;
	 * will always be >=0;
	 */
	public int getCurrentCapacity(){
		return data.length;
	}
	
	private void ensureCapacity(int minCapacity) {
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
	
	public long[] toArray(){
		return Arrays.copyOf(data,currentLength);
	}
	/**
	 * Searches the current values in this growable array
	 * using the binary search algorithm as implemented
	 * by {@link Arrays#binarySearch(long[], long)}.
	 * The array must be sorted (as
     * by the {@link #sort()} method) prior to making this call.  If it
     * is not sorted, the results are undefined.  If the array contains
     * multiple elements with the specified value, there is no guarantee which
     * one will be found.
	 * 
	 * @param key the value to be searched for.
	 * @return index of the search key, if it is contained in the array;
     *	       otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
     *	       <i>insertion point</i> is defined as the point at which the
     *	       key would be inserted into the array: the index of the first
     *	       element greater than the key, or <tt>a.length</tt> if all
     *	       elements in the array are less than the specified key.  Note
     *	       that this guarantees that the return value will be &gt;= 0 if
     *	       and only if the key is found.
     * @throws IllegalArgumentException
     *	       if {@code fromIndex > toIndex}
     * @throws ArrayIndexOutOfBoundsException
     *	       if {@code fromIndex < 0 or toIndex > a.length}
     *@see Arrays#binarySearch(byte[], byte)
     */
	public int binarySearch(long key){
		return Arrays.binarySearch(data, 0, currentLength, key);
	}
	
	
	/**
	 * Remove the given value from this
	 * sorted array.  This method
	 * assumes that the values in the backing
	 * array have been previously sorted.
	 * If this sorted array contains several
	 * indexes with this value
	 * then only one will be removed
	 * and it is undefined which one will
	 * actually be removed.
	 * Calling this method on an unsorted
	 * backing array may not remove the value
	 * correctly.
	 * @param value the value to remove
	 * @return {@code true} if the value
	 * was found and removed; {@code false}
	 * if the value does not exist in the
	 * sorted backing array.
	 */
	public boolean sortedRemove(long value){	
		int index = binarySearch(value);

		if (index >= 0) {
			remove(index);
			return true;
		}
		return false;
	}
	/**
	 * Insert the given value into the 
	 * sorted backing array.  
	 * Calling this method on an unsorted
	 * backing array may not insert the value
	 * correctly.
	 * @param value the value to insert.
	 * @return the index that this value
	 * was inserted into.
	 */
	public int sortedInsert(long value){
		int index = binarySearch(value);
		if(index <0){
			//not found
			//value returned is (-insertion point) -1)
			index = -index -1;
		}
		insert(index, value);
		return index;
	}
	/**
	 * Sort the current values in this growable array
	 * using the default comparator.
	 */
	public void sort(){
		Arrays.sort(data, 0, currentLength);
	}
	
	/**
	 * Set the current length to 0.
	 */
	public void clear(){
		this.currentLength=0;
	}
	
	@Override
	public Iterator<Long> iterator() {
		return PrimitiveArrayIterators.create(data, currentLength);
	}
	
	/**
	 * Insert the given sorted array of values into the 
	 * sorted backing array.  This should produce identical
	 * results
	 * to iterating over the array and calling
	 * {@link #sortedInsert(long)} on each element,
	 * but is hopefully more efficient.
	 * 
	 * Calling this method on an unsorted
	 * backing array may not insert the value
	 * correctly.
	 * @param value the value to insert.
	 */
	public void sortedInsert(long[] values){
		if(values.length==0){
			//no-op
			return;
		}
		if(currentLength ==0){
			//we have 0 length 
			//act just like append
			append(values);
			return;
		}
		long[] newData = new long[data.length + values.length];
		int newCurrentLength = currentLength + values.length;
		
		int ourDataIndex=0, otherDataIndex=0;

		long ourNextValue = data[0];
		long otherNextValue = values[0];
		int i=0;
		for(; ourDataIndex<currentLength && otherDataIndex < values.length; i++){
			if(ourNextValue <otherNextValue){
				newData[i] = ourNextValue;
				ourDataIndex ++;
				if(ourDataIndex <currentLength){
					ourNextValue = data[ourDataIndex];
				}
				
			}else{
				newData[i] = otherNextValue;
				otherDataIndex++;
				if(otherDataIndex <values.length){
					otherNextValue = values[otherDataIndex];
				}
			}
		}
		//by the time we get here
		//at least one of the original
		//sorted arrays has been exhausted
		//(possibly both if they are the same size
		//with perfect interleaving...
		
		if(ourDataIndex<currentLength){
			//fill in rest of our data
			for(; ourDataIndex<currentLength; i++, ourDataIndex++){
				newData[i] =data[ourDataIndex];
			}
		}else{
			//fill in rest of other data
			for(; otherDataIndex<values.length; i++, otherDataIndex++){
				newData[i] =values[otherDataIndex];
			}
		}
			
		data = newData;
		currentLength = newCurrentLength;
	}
	
	/**
	 * Get the number of values
	 * in this array that currently
	 * have the given value.
	 * @param value the value to look for.
	 * @return the number of cells with 
	 * the value; will always be >= 0.
	 */
	public int getCount(long value){
		int count=0;
		for(int i=0; i<currentLength; i++){
			if(data[i] == value){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Create a sequential {@link LongStream}
	 * of the current array.
	 * @return a new {@link LongStream}
	 * will never be null but may be empty.
	 */
	public LongStream stream() {
		return Arrays.stream(data, 0, currentLength);		
	}
}
