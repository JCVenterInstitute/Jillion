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

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.util.IntList;
import org.jcvi.jillion.core.util.ReplacementIntFunction;
import org.jcvi.jillion.core.util.streams.ThrowingIntIndexedIntConsumer;
import org.jcvi.jillion.internal.core.util.iter.PrimitiveArrayIterators;
/**
 * A {@code GrowableIntArray} is a utility class
 * that wraps a int array that will dynamically
 * grow as needed when data is
 * appended, inserted, replaced and removed etc.
 * This is similar to an {@link ArrayList}
 * or {@link StringBuilder}
 * for primitive shorts.
 * This class is not Thread-safe.
 * @author dkatzel
 *
 */
public final class GrowableIntArray implements Iterable<Integer>{

	/**
	 * The current length of valid data
	 * this is not the same as the length
	 * of the int array (capacity) since
	 * there still might be room to grow.
	 * There might even be old data in the array
	 * past current length if the array
	 * has been modified via the {@link #remove(int)}
	 * methods.
	 */
	private int currentLength=0;
	/**
	 * Our actual int array,
	 * the capacity is the size of the array.
	 */
	private int[] data;
	/**
	 * Creates a new Growable array
	 * with the given initial capacity.
	 * @param initialCapacity the initial size 
	 * of the backing int array.  When adding
	 * ints will cause the int array to overflow,
	 * the backing int array will automatically
	 * grow larger.
	 * @throws IllegalArgumentException if initialCapacity is <0.
	 */
	public GrowableIntArray(int initialCapacity){
		if(initialCapacity <0){
			throw new IllegalArgumentException("initial capacity should be >= 0 :"+initialCapacity);
		}
		data = new int[initialCapacity];		
	}
	/**
	 * Creates a new Growable array
	 * where the backing array contains
	 * the contents of the given Collection
	 * stored as primitives.  The order in the array
	 * is the determined by the Collection's iteration order.
	 * The capacity and length of this growable array
	 * are set to the collection's size.
	 * @param ints the Collection of Integers to 
	 * create into a growable array from.
	 * @throws NullPointerException if the given
	 * collection is null or any elements in the collection
	 * are null.
	 */
	public GrowableIntArray(Collection<Integer> ints){
		data = new int[ints.size()];
		int index=0;
		for(Integer i : ints){
			data[index]=i.intValue();
			index++;
		}
		currentLength=data.length;
	}

	/**
	 * Creates a new Growable array
	 * where the backing array contains
	 * the contents of the given Collection
	 * stored as primitives.  The order in the array
	 * is the determined by the Collection's iteration order.
	 * The capacity and length of this growable array
	 * are set to the collection's size.
	 * @param ints the Collection of Integers to
	 * create into a growable array from.
	 * @throws NullPointerException if the given
	 * collection is null or any elements in the collection
	 * are null.
	 */
	public GrowableIntArray(IntList ints){
		data = new int[ints.size()];
		int index=0;
		PrimitiveIterator.OfInt iter = ints.intIterator();
		while(iter.hasNext()){
			data[index++]=iter.nextInt();
		}

		currentLength=data.length;
	}
	/**
	 * Creates a new Growable array
	 * where the backing int array is an exact
	 * copy of the input array and the initial
	 * capacity is set to the array length.
	 * This has similar (although optimized)
	 * functionality to
	 * <pre>
	 * int[] ints = ...
	 * GrowableIntArray gba = new GrowableIntArray(ints.length);
	 * gba.append(int);
	 * </pre>
	 * @param ints the initial short values to set
	 * to the backing array.
	 * @throws NullPointerException if ints is null.
	 */
	@JsonCreator
	public GrowableIntArray(int[] ints){
		data = Arrays.copyOf(ints, ints.length);
		currentLength=data.length;
	}

	/**
	 * Copy data from this array into the destination array.
	 * @implNote This should be the same as <code>System.arraycopy(data,srcOffset, destArray,destOffset, length )</code>
	 * @param srcOffset the offset into THIS growable array.
	 * @param destArray the destination array to copy into.
	 * @param destOffset the offset to start copy data into the destination array.
	 * @param length the number of values to copy.
	 * @since 6.1
	 */
	public void arrayCopy(int srcOffset, int[] destArray, int destOffset, int length) {
		System.arraycopy(data,srcOffset, destArray,destOffset, length );
	}
	/**
	 * Copy data from this array into the destination array;
	 * this will grow the destArray if needed.
	 * @implNote This should be the same as <code>System.arraycopy(data,srcOffset, destArray,destOffset, length )</code>
	 * except extra checks are done to expand the destArray if needed.
	 *
	 * @param srcOffset the offset into THIS growable array.
	 * @param destArray the destination array to copy into.
	 * @param destOffset the offset to start copy data into the destination array.
	 * @param length the number of values to copy.
	 * @since 6.1
	 */
	public void arrayCopy(int srcOffset, GrowableIntArray destArray, int destOffset, int length) {
		int newDestLength = length + destOffset;
		boolean resize=false;
		if(newDestLength > destArray.currentLength){
			destArray.ensureCapacity(newDestLength);
			resize=true;
		}

		System.arraycopy(data,srcOffset, destArray.data,destOffset, length );
		if(resize) {
			destArray.currentLength = newDestLength;
		}
	}

	private static final String[][] EMPTY_CACHE_TO_STRING;

	static{
		EMPTY_CACHE_TO_STRING =  new String[128][128];
		//prefill
		EMPTY_CACHE_TO_STRING['['][']'] = "[]";
		EMPTY_CACHE_TO_STRING['{']['}'] = "{}";


	}

	private static String getFromCache(char a, char b){
		String s = EMPTY_CACHE_TO_STRING[a][b];
		if(s==null){
			synchronized (EMPTY_CACHE_TO_STRING){
				s = EMPTY_CACHE_TO_STRING[a][b];
				if(s==null){
					String ret = new StringBuilder(2).append(a).append(b).toString();
					EMPTY_CACHE_TO_STRING[a][b] = ret;
					return ret;
				}

			}
		}
		return s;

	}

	public String toString(char prefix, char suffix) {
		//copy of relevant code from Arrays#toString() but adding current length
		int iMax = currentLength - 1;
		if (iMax == -1) {
			return getFromCache(prefix, suffix);
		}

		StringBuilder b = new StringBuilder(currentLength);
		b.append(prefix);
		for (int i = 0; ; i++) {
			b.append(data[i]);
			if (i == iMax)
				return b.append(suffix).toString();
			b.append(", ");
		}
	}
	@Override
	public String toString() {
		return toString('[', ']');
	}

	/**
	 * Creates a new {@link GrowableIntArray}
	 * with the given initial capacity.
	 * @param range the initial Ranges to populate backing long array.
	 *
	 */
	public GrowableIntArray(Range range){


		data = new int[(int) range.getLength()];

		range.forEachValue(l-> this.append((int)l));

	}
	/**
	 * Creates a new {@link GrowableIntArray}
	 * with the given initial capacity.
	 * @param ranges the initial List of Ranges to populate backing long array.
	 *
	 */
	public GrowableIntArray(List<Range> ranges){
		Objects.requireNonNull(ranges);
		int initialCapacity = (int) ranges.stream()
				.mapToLong(Range::getLength)
				.sum();

		data = new int[initialCapacity];
		for(Range r : ranges){
			r.forEachValue(l-> this.append((int)l));
		}
	}

	private GrowableIntArray(GrowableIntArray copy){
		data = Arrays.copyOf(copy.data, copy.data.length);
		currentLength = copy.currentLength;
	}
	/**
	 * Creates a new Growable array
	 * with a default initial capacity.
	 */
	public GrowableIntArray() {
		this(16);
	}
	/**
	 * Create a new instance of GrowableShortArray
	 * that is an exact copy of this instance.
	 * Any future modifications to either the original
	 * instance or the copy will NOT be reflected 
	 * in the other.
	 * @return a new instance of GrowableIntArray
	 * that contains the same data as this instance
	 * currently does.
	 */
	public GrowableIntArray copy(){
		return new GrowableIntArray(this);
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
			int temp=data[i];
			int reverseI = currentLength-i-1;
			data[i] = data[reverseI];
			data[reverseI] = temp;
		}
	}
	public int getCurrentLength() {
		return currentLength;
	}

	public void append(int value){
		ensureCapacity(currentLength+1);
		data[currentLength++]=value;
	}
	
	public void append(int[] values){
		ensureCapacity(currentLength+values.length);
		System.arraycopy(values, 0, data, currentLength, values.length);
		currentLength+=values.length;
	}
	public void append(int[] values, ReplacementIntFunction replacementFunction){
		if(replacementFunction==null){
			append(values);
			return;
		}
		ensureCapacity(currentLength+values.length);
		for(int i=0; i< values.length; i++){
			data[i+currentLength] = replacementFunction.applyAsInt(values[i]);
		}
		currentLength+=values.length;
	}
	public void append(GrowableIntArray other){
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(other.data, 0, data, currentLength, other.currentLength);
		currentLength+=other.currentLength;
	}
	public void append(GrowableIntArray other, ReplacementIntFunction replacementFunction){
		if(replacementFunction==null){
			append(other);

			return;
		}
		ensureCapacity(currentLength+other.currentLength);
		other.forEachIndexed((i, v)->{
			data[i+currentLength]= replacementFunction.applyAsInt(v);
		});

		currentLength+=other.currentLength;

	}
	public void append(GrowableIntArray other, int start, int length, ReplacementIntFunction replacementFunction){
		if(replacementFunction==null){
			if(start==0 && length ==other.currentLength) {
				append(other);
			}else {
				Range range = new Range.Builder(length).shift(start).build();
				append(other.toArray(range));
			}
			return;
		}
		ensureCapacity(currentLength+length);
		Range range = new Range.Builder(length).shift(start).build();
		other.forEachIndexed(range, (i, v)->{
			data[i+currentLength- (int) range.getBegin()]= replacementFunction.applyAsInt(v);
		});

		currentLength+=length;
	}
	public int get(int offset){
		assertValidOffset(offset);
		return data[offset];
	}
	
	public void prepend(int value){
		insert(0,value);
	}
	
	public void prepend(int[] values){
		insert(0,values);
	}
	public void prepend(GrowableIntArray other){
		insert(0,other);
	}
	public void replaceUnsafe(int offset, int value){
		data[offset]=value;
	}
	public void replace(int offset, int value){
		assertValidOffset(offset);
		replaceUnsafe(offset, value);
	}
	public void insert(int offset, int[] values){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+values.length);
		System.arraycopy(data, offset, data, offset + values.length,
				currentLength - offset);
		
		System.arraycopy(values, 0, data, offset, values.length);
		currentLength+=values.length;
		
	}
	
	public void insert(int offset, GrowableIntArray other){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(data, offset, data, offset + other.currentLength,
				currentLength - offset);
		
		System.arraycopy(other.data, 0, data, offset, other.currentLength);
		currentLength+=other.currentLength;
		
	}
	public void insert(int offset, GrowableIntArray other, ReplacementIntFunction replacementFunction){
		if(replacementFunction==null){
			insert(offset, other);
			return;
		}
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(data, offset, data, offset + other.currentLength,
				currentLength - offset);

		other.forEachIndexed((i, v)->{
			data[i+offset] = replacementFunction.applyAsInt(v);
		});


		currentLength+=other.currentLength;

	}
	public void insert(int offset, int value){
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

	public int remove(int offset){
		assertValidOffset(offset);
		int oldValue = data[offset];

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
	@JsonValue
	public int[] toArray(){
		return Arrays.copyOf(data,currentLength);
	}
	public int[] toArray(Range range){
		return Arrays.copyOfRange(data, (int) range.getBegin(), (int) range.getEnd()+1);
	}
	/**
	 * Searches the current values in this growable array
	 * using the binary search algorithm as implemented
	 * by {@link Arrays#binarySearch(int[], int)}.
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
	public int binarySearch(int key){
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
	public boolean sortedRemove(int value){	
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
	public int sortedInsert(int value){
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
	 * Insert the given sorted array of values into the 
	 * sorted backing array.  This should produce identical
	 * results
	 * to iterating over the array and calling
	 * {@link #sortedInsert(int)} on each element,
	 * but is hopefully more efficient.
	 * 
	 * Calling this method on an unsorted
	 * backing array may not insert the value
	 * correctly.
	 * @param values the values to insert.
	 */
	public void sortedInsert(int[] values){
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
		int[] newData = new int[data.length + values.length];
		int newCurrentLength = currentLength + values.length;
		
		int ourDataIndex=0, otherDataIndex=0;

		int ourNextValue = data[0];
		int otherNextValue = values[0];
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
	public PrimitiveIterator.OfInt iterator() {
		return PrimitiveArrayIterators.create(data, currentLength);
	}

	public PrimitiveIterator.OfInt iterator(int startOffset) {
		return PrimitiveArrayIterators.create(data, startOffset, currentLength-1);
	}


	public PrimitiveIterator.OfInt reverseIterator() {
		return PrimitiveArrayIterators.createReverse(data, currentLength);
	}
	public PrimitiveIterator.OfInt reverseIterator(int startOffset) {
		return PrimitiveArrayIterators.createReverse(data, startOffset+1);
	}
	
	/**
	 * Get the number of values
	 * in this array that currently
	 * have the given value.
	 * @param value the value to look for.
	 * @return the number of cells with 
	 * the value; will always be >= 0.
	 */
	public int getCount(int value){
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
		return Arrays.stream(data, 0, currentLength);		
	}
	
	public IntStream streamUntil(int value) {
		int end = binarySearch(value);
		if(end <0) {
			end = -(end+1);
		}
		return Arrays.stream(data, 0, end);
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
		return Arrays.stream(data, (int) range.getBegin(), (int) range.getEnd() +1);
	}
	/**
	 * Create a new List of Integers with the same data
	 * as the current array values. The returned list
	 * IS A COPY so any modifications to the returned list 
	 * or the backing GrowableIntArray will not affect the other.
	 * 
	 * @return a new modifable list; will never be null but may be empty.
	 * 
	 * @since 5.3
	 */
	public IntList toBoxedList() {
		if(currentLength ==0){
			//return new list to make it mutable
			return ArrayUtil.asList(new int[0]);
		}
		return ArrayUtil.asList(toArray());
	}

	/**
	 * Iterate over each element in the list and call the given consumer
	 * which captures the offset and the value.
	 * @param consumer the consumer of each element; can not be null.
	 * @param <E> the Throwable that might be thrown by the consumer.
	 * @throws E the Throwable from the consumer.
	 *
	 * @since 5.3
	 *
	 * @throws NullPointerException if consumer is null.
	 */
	public <E extends Throwable> void forEachIndexed(ThrowingIntIndexedIntConsumer<E> consumer) throws E{
		Objects.requireNonNull(consumer);
		for(int i=0; i< currentLength; i++){
			consumer.accept(i, data[i]);
		}
	}
	/**
	 * Iterate over each element in the list in REVERSE ORDER and call the given consumer
	 * which captures the offset and the value.
	 * @param consumer the consumer of each element; can not be null.
	 * @param <E> the Throwable that might be thrown by the consumer.
	 * @throws E the Throwable from the consumer.
	 *
	 * @since 6.1
	 *
	 * @throws NullPointerException if consumer is null.
	 */
	public <E extends Throwable> void forEachIndexedReversed(ThrowingIntIndexedIntConsumer<E> consumer) throws E{
		Objects.requireNonNull(consumer);
		for(int i=currentLength-1; i>=0; i--){
			consumer.accept(i, data[i]);
		}
	}

	/**
	 * Iterate over the elements in the given range of this array and call the given consumer
	 * which captures the offset and the value.
	 * @param range the offset range to check over; can not be null.
	 * @param consumer the consumer of each element; can not be null.
	 * @param <E> the Throwable that might be thrown by the consumer.
	 * @throws E the Throwable from the consumer.
	 *
	 * @throws NullPointerException exception if either range or consumer are null.
	 * @since 5.3
	 */
	public <E extends Throwable> void forEachIndexed(Range range, ThrowingIntIndexedIntConsumer<E> consumer) throws E{
		int end = (int) Math.min(currentLength, range.getEnd()+1);
		for(int i=(int) range.getBegin(); i< end; i++){
			consumer.accept(i, data[i]);
		}
	}

	/**
	 * Iterate over the elements in the given range of this array in REVERSE ORDER and call the given consumer
	 * which captures the offset and the value.
	 * @param range the offset range to check over; can not be null.
	 * @param consumer the consumer of each element; can not be null.
	 * @param <E> the Throwable that might be thrown by the consumer.
	 * @throws E the Throwable from the consumer.
	 *
	 * @throws NullPointerException exception if either range or consumer are null.
	 * @since 6.1
	 */
	public <E extends Throwable> void forEachIndexedReversed(Range range, ThrowingIntIndexedIntConsumer<E> consumer) throws E{
		int end = (int) Math.min(currentLength, range.getEnd()+1);
		int begin = (int) range.getBegin();
		for(int i=end; i>= begin; i--){
			consumer.accept(i, data[i]);
		}
	}

	/**
	 * Replace any values that pass the given predicate with the given replacement value.
	 * @param predicate the predicate to test; can not be null.
	 * @param replacementValue the value to set for all values the predicate returns true.
	 * @throws NullPointerException if predicate is null.
	 * 
	 * @return this
	 * 
	 * @since 6.0
	 */
	public GrowableIntArray replaceIf(IntPredicate predicate, int replacementValue){
		for(int i=0; i< currentLength; i++) {
			if(predicate.test(data[i])) {
				data[i] = replacementValue;
			}
		}
		return this;
	}
	/**
	 * Replace any values that pass the given predicate with the given replacement value.
	 * @param predicate the predicate to test; can not be null.
	 * @param replacementFunction the function to replace the value given the previous value.
	 * @throws NullPointerException if predicate is null.
	 *
	 * @return this
	 *
	 * @since 6.0.4
	 */
	public GrowableIntArray replaceIf(IntPredicate predicate, IntUnaryOperator replacementFunction){
		Objects.requireNonNull(replacementFunction);
		Objects.requireNonNull(predicate);
		for(int i=0; i< currentLength; i++) {
			if(predicate.test(data[i])) {
				data[i] = replacementFunction.applyAsInt(data[i]);
			}
		}
		return this;
	}
	/**
	 * Replace all values with the given replacement value.
	 * @param replacementFunction the function to replace the value given the previous value.
	 * @throws NullPointerException if replacementFunction is null.
	 *
	 * @return this
	 *
	 * @since 6.1
	 */
	public GrowableIntArray replaceAll(IntUnaryOperator replacementFunction){
		Objects.requireNonNull(replacementFunction);

		for(int i=0; i< currentLength; i++) {
			data[i] = replacementFunction.applyAsInt(data[i]);

		}
		return this;
	}
	/**
	 * Replace all values with the given replacement value.
	 * @param range the offset range to check over; can not be null.
	 * @param replacementFunction the function to replace the value given the previous value.
	 * @throws NullPointerException if replacementFunction is null.
	 *
	 * @return this
	 *
	 * @since 6.1
	 */
	public GrowableIntArray replaceAll(Range range, IntUnaryOperator replacementFunction){
		Objects.requireNonNull(replacementFunction);

		int end = (int) Math.min(currentLength, range.getEnd()+1);
		for(int i=(int) range.getBegin(); i< end; i++){
			data[i] = replacementFunction.applyAsInt(data[i]);

		}
		return this;
	}
	/**
	 * Replace any values within the given offset range that pass the given predicate with the given replacement value.
	 * @param range the offset range to check over; can not be null.
	 * @param predicate the predicate to test; can not be null.
	 * @param replacementValue the value to set for all values the predicate returns true.
	 * 
	 * @return this
	 * @throws NullPointerException if either range or predicate are null.
	 * @since 6.0
	 */
	public GrowableIntArray replaceIf(Range range, IntPredicate predicate, int replacementValue){
		int end = (int) Math.min(currentLength, range.getEnd()+1);
		for(int i=(int) range.getBegin(); i< end; i++){
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
	 * @param replacementFunction the function to replace the value given the previous value.
	 * @throws NullPointerException if predicate is null.
	 *
	 * @return this
	 *
	 * @since 6.0.4
	 */
	public GrowableIntArray replaceIf(Range range, IntPredicate predicate, IntUnaryOperator replacementFunction){
		Objects.requireNonNull(replacementFunction);
		Objects.requireNonNull(predicate);
		int end = (int) Math.min(currentLength, range.getEnd()+1);
		for(int i=(int) range.getBegin(); i< end; i++){
			if(predicate.test(data[i])) {
				data[i] = replacementFunction.applyAsInt(data[i]);
			}
		}
		return this;
	}

	/**
	 * Replace any values within from the given offset, to the end of the array that pass the given predicate with the given replacement value.
	 * @param startOffset the offset to start from
	 * @param replacementFunction the function to replace the value given the previous value.
	 * @throws NullPointerException if predicate is null.
	 *
	 * @return this
	 *
	 * @since 6.0.4
	 */
	public GrowableIntArray replaceDownstream(int startOffset, IntUnaryOperator replacementFunction){
		Objects.requireNonNull(replacementFunction);

		for(int i=startOffset; i< currentLength; i++){
			data[i] = replacementFunction.applyAsInt(data[i]);

		}
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GrowableIntArray)){
			return false;
		}
		GrowableIntArray bytes = (GrowableIntArray) o;
		if(currentLength != bytes.currentLength){
			return false;
		}
		return Arrays.equals(data,0, currentLength, bytes.data, 0, currentLength);

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

	/**
	 * Set this array to the newer smaller length.
	 * This is a way to "remove" downstream positions without having to do any
	 * array operations.
	 *
	 * @param newLength the new length of the array; must be &ge; 0 and &lt; the current length.
	 */
	public void truncate(int newLength) {
		if(newLength > currentLength){
			throw new IllegalArgumentException("truncated length can not be greater than current length");
		}
		if(newLength < 0){
			throw new IllegalArgumentException("new length must be >= 0");
		}
		currentLength = newLength;
	}

	public List<Range> asRanges() {
		return Ranges.asRanges(data,0,currentLength);
	}


}
