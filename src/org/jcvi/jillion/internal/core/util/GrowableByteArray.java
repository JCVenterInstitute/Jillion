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
package org.jcvi.jillion.internal.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.util.streams.BytePredicate;
import org.jcvi.jillion.core.util.streams.ThrowingIntIndexedByteConsumer;
import org.jcvi.jillion.internal.core.util.iter.PrimitiveArrayIterators;
/**
 * A {@code GrowableByteArray} is a utility class
 * that wraps a byte array that will dynamically
 * grow as needed when data is
 * appended, inserted, replaced and removed etc.
 * This is similar to an {@link java.util.ArrayList}
 * or {@link StringBuilder}
 * for primitive bytes.
 * This class is not Thread-safe.
 * @author dkatzel
 *
 */
public final class GrowableByteArray implements Iterable<Byte>{
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
	private byte[] data;
	
	public GrowableByteArray(){
		this(16);
	}
	/**
	 * Creates a new {@link GrowableByteArray}
	 * with the given initial capacity.
	 * @param initialCapacity the initial size 
	 * of the backing byte array.  When adding
	 * bytes will cause the byte array to overflow,
	 * the backing byte array will automatically
	 * grow larger.
	 * @throws IllegalArgumentException if initialCapacity is <0.
	 */
	public GrowableByteArray(int initialCapacity){
		if(initialCapacity <0){
			throw new IllegalArgumentException("initial capacity should be >= 0 :"+initialCapacity);
		}
		data = new byte[initialCapacity];		
	}
	
	/**
	 * Creates a new {@link GrowableByteArray}
	 * where the backing array contains
	 * the contents of the given Collection
	 * stored as primitives.  The order in the array
	 * is the determined by the Collection's iteration order.
	 * The capacity and length of this growable array
	 * are set to the collection's size.
	 * @param bytes the Collection of Integers to 
	 * create into a growable array from.
	 * @throws NullPointerException if the given
	 * collection is null or any elements in the collection
	 * are null.
	 */
	public GrowableByteArray(Collection<Byte> bytes){
		data = new byte[bytes.size()];
		int index=0;
		for(Byte i : bytes){
			data[index]=i.byteValue();
			index++;
		}
		currentLength=data.length;
	}
	
	
	/**
	 * Creates a new {@link GrowableByteArray}
	 * where the backing byte array is an exact
	 * copy of the input array and the initial
	 * capacity is set to the array length.
	 * This has similar (although optimized)
	 * functionality to
	 * <pre>
	 * byte[] bytes = ...
	 * GrowableByteArray gba = new GrowableByteArray(bytes.length);
	 * gba.append(bytes);
	 * </pre>
	 * @param bytes the initial byte values to set
	 * to the backing array.
	 * @throws NullPointerException if bytes is null.
	 */
	public GrowableByteArray(byte[] bytes){
		data = Arrays.copyOf(bytes, bytes.length);
		currentLength=data.length;
	}
	private GrowableByteArray(GrowableByteArray copy){
		data = Arrays.copyOf(copy.data, copy.data.length);
		currentLength = copy.currentLength;
	}
	/**
	 * Create a new instance of GrowableByteArray
	 * that is an exact copy of this instance.
	 * Any future modifications to either the original
	 * instance or the copy will NOT be reflected 
	 * in the other.
	 * @return a new instance of GrowableByteArray
	 * that contains the same data as this instance
	 * currently does.
	 */
	public GrowableByteArray copy(){
		return new GrowableByteArray(this);
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
			byte temp=data[i];
			int reverseI = currentLength-i-1;
			data[i] = data[reverseI];
			data[reverseI] = temp;
		}
	}
	public int getCurrentLength() {
		return currentLength;
	}

	public void append(byte value){
		ensureCapacity(currentLength+1);
		data[currentLength++]=value;
	}
	
	public void append(byte[] values){
		ensureCapacity(currentLength+values.length);
		System.arraycopy(values, 0, data, currentLength, values.length);
		currentLength+=values.length;
	}
	public void append(GrowableByteArray other){
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(other.data, 0, data, currentLength, other.currentLength);
		currentLength+=other.currentLength;
	}
	public byte get(int offset){
		assertValidOffset(offset);
		return data[offset];
	}
	
	public void prepend(byte value){
		insert(0,value);
	}
	
	public void prepend(byte[] values){
		insert(0,values);
	}
	public void prepend(GrowableByteArray other){
		insert(0,other);
	}
	public void replace(int offset, byte value){
		assertValidOffset(offset);
		data[offset]=value;
	}
	public void insert(int offset, byte[] values){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+values.length);
		System.arraycopy(data, offset, data, offset + values.length,
				currentLength - offset);
		
		System.arraycopy(values, 0, data, offset, values.length);
		currentLength+=values.length;
		
	}
	
	public void insert(int offset, GrowableByteArray other){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(data, offset, data, offset + other.currentLength,
				currentLength - offset);
		
		System.arraycopy(other.data, 0, data, offset, other.currentLength);
		currentLength+=other.currentLength;
		
	}
	public void insert(int offset, byte value){
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
	public byte remove(int offset){
		assertValidOffset(offset);
		byte oldValue = data[offset];

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
	
	public byte[] toArray(){
		return Arrays.copyOf(data,currentLength);
	}
	
	/**
	 * Searches the current values in this growable array
	 * using the binary search algorithm as implemented
	 * by {@link Arrays#binarySearch(byte[], byte)}.
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
	public int binarySearch(byte key){
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
	public boolean sortedRemove(byte value){	
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
	public int sortedInsert(byte value){
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
	public Iterator<Byte> iterator() {
		return PrimitiveArrayIterators.create(data, currentLength);
	}
	public GrowableByteArray subArray(Range range) {
		if(range ==null){
			throw new NullPointerException("range can not be null");
		}
		int start = (int)range.getBegin();
		int end = (int) range.getEnd() +1;
		if(start <0){
			start=0;
		}
		if(end >currentLength){
			//end is exclusive
			end = currentLength;
		}
		
		return new GrowableByteArray(Arrays.copyOfRange(data, start, end));
	}
	
	/**
	 * Insert the given sorted array of values into the 
	 * sorted backing array.  This should produce identical
	 * results
	 * to iterating over the array and calling
	 * {@link #sortedInsert(byte)} on each element,
	 * but is hopefully more efficient.
	 * 
	 * Calling this method on an unsorted
	 * backing array may not insert the value
	 * correctly.
	 * @param values the values to insert.
	 */
	public void sortedInsert(byte[] values){
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
		byte[] newData = new byte[data.length + values.length];
		int newCurrentLength = currentLength + values.length;
		
		int ourDataIndex=0, otherDataIndex=0;

		byte ourNextValue = data[0];
		byte otherNextValue = values[0];
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
	public int getCount(byte value){
		int count=0;
		for(int i=0; i<currentLength; i++){
			if(data[i] == value){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Create a sequential {@link IntStream}
	 * of the current array.
	 * @return a new {@link IntStream}
	 * will never be null but may be empty.
	 */
	public IntStream stream() {
		//there isn't a method to convert a byte[]
		//into an IntStream so we have to do it ourselves
		int[] copy = new int[currentLength];
		for(int i=0; i<currentLength; i++){
			copy[i] = data[i];
		}
		return Arrays.stream(copy, 0, currentLength);		
	}

	/**
	 * Create a sequential {@link IntStream}
	 * of the current array.
	 * @param range the subrange
	 * @return a new {@link IntStream}
	 * will never be null but may be empty.
	 *
	 * @since 5.3.2
	 */
	public IntStream stream(Range range) {
		//there isn't a method to convert a byte[]
		//into an IntStream so we have to do it ourselves
		int[] copy = new int[(int) range.getLength()];
		int end = (int) range.getEnd() +1;
		for(int i=(int) range.getBegin(), j=0; i<end; i++, j++){
			copy[j] = data[i];
		}
		return Arrays.stream(copy, 0, (int) range.getLength()) ;
	}
	/**
	 * Iterate over each element in the array and call the given consumer
	 * which captures the offset and the value.
	 * @param consumer the consumer of each element; can not be null.
	 * @param <E> the Throwable that might be thrown by the consumer.
	 * @throws E the Throwable from the consumer.
	 *
	 * @since 5.3
	 */
    public <E extends Throwable> void forEachIndexed(ThrowingIntIndexedByteConsumer<E> consumer) throws E{
		for(int i=0; i< currentLength; i++){
			consumer.accept(i, data[i]);
		}
    }

	/**
	 * Iterate over the elements in the given range of this array and call the given consumer
	 * which captures the offset and the value.
	 * @param consumer the consumer of each element; can not be null.
	 * @param <E> the Throwable that might be thrown by the consumer.
	 * @throws E the Throwable from the consumer.
	 *
	 * @since 5.3
	 */
	public <E extends Throwable> void forEachIndexed(Range range, ThrowingIntIndexedByteConsumer<E> consumer) throws E{
		int end = (int) Math.min(currentLength, range.getEnd()+1);
		for(int i=(int) range.getBegin(); i< end; i++){
			consumer.accept(i, data[i]);
		}
	}

	/**
	 * Replace any values that pass the given predicate with the given replacement value.
	 * @param predicate the predicate to test; can not be null.
	 * @param replacementValue the value to set for all values the predicate returns true.
	 * @throws NullPointerException if predicate is null.
	 * @return this
	 * 
	 * @since 6.0
	 */
	public GrowableByteArray replaceIf(BytePredicate predicate, byte replacementValue){
		for(int i=0; i< currentLength; i++) {
			if(predicate.test(data[i])) {
				data[i] = replacementValue;
			}
		}
		return this;
	}
	/**
	 * Replace any values within the given offset range that pass the given predicate with the given replacement value.
	 * @param range the offset range to check over; can not be null.
	 * @param predicate the predicate to test; can not be null.
	 * @param replacementValue the value to set for all values the predicate returns true.
	 * @throws NullPointerException if either range or predicate are null.
	 * 
	 * @return this
	 * 
	 * @since 6.0
	 */
	public GrowableByteArray replaceIf(Range range, BytePredicate predicate, byte replacementValue){
		int end = (int) Math.min(currentLength, range.getEnd()+1);
		for(int i=(int) range.getBegin(); i< end; i++){
			if(predicate.test(data[i])) {
				data[i] = replacementValue;
			}
		}
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GrowableByteArray)){
			return false;
		}
		GrowableByteArray bytes = (GrowableByteArray) o;
		if(currentLength != bytes.currentLength){
			return false;
		}
		for (int i=0; i<currentLength; i++) {
			if (data[i] != bytes.data[i]) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		//only compute has up to current length
		//since delete doesn't always remove elements from the end.
		int result = 1;
        for (int i=0; i< currentLength; i++) {
            result = 31 * result + data[i];
        }
		return result;
	}
}
