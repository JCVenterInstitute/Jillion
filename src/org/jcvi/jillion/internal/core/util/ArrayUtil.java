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

import org.jcvi.jillion.core.util.IntList;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;
import java.util.RandomAccess;
import java.util.stream.IntStream;

/**
 * Utility class to work on arrays.
 * @author dkatzel
 *
 */
public final class ArrayUtil {
	/**
	 * Create a new List instance that has
	 * one element for each element
	 * in the int array.
	 * The returned instance keeps
	 * the backing array as primitives so
	 * there is no memory or performance
	 * penalty for boxing/unboxing until
	 * users perform a {@link List#get(int)}.
	 * 
	 * @param array the array to wrap in a List.
	 * 
	 * @return a new List of int.
	 */
	public static IntArrayList asList(int[] array){
		return new IntArrayList(array);
	}

	/**
	 * Create a new {@link IntList} of the given initial capacity
	 * @param initialCapacity the initial capacity of the backing array; can not be &lt; 0.
	 * @return a new IntList
	 * @throws NegativeArraySizeException if initialCapacity is negative.
	 * @since 6.1
	 */
	public static IntArrayList newIntList(int initialCapacity){
		return new IntArrayList(new int[initialCapacity], 0);
	}

	public static IntList immutableEmptyIntList(){
		return ImmutableEmptyIntList.INSTANCE;
	}
	
	private ArrayUtil(){
		//can not instantiate
	}
	public static final class ImmutableEmptyIntList extends AbstractList<Integer> implements IntList{

		private static final ImmutableEmptyIntList INSTANCE = new ImmutableEmptyIntList();

		private enum EmptyIterator implements OfInt{
			INSTANCE
			;

			@Override
			public int nextInt() {
				throw new NoSuchElementException();
			}

			@Override
			public boolean hasNext() {
				return false;
			}
		}
		@Override
		public Integer get(int index) {
			throw new IndexOutOfBoundsException(index);
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public OfInt intIterator() {
			return EmptyIterator.INSTANCE;
		}

		@Override
		public int getAsInt(int index) {
			throw new IndexOutOfBoundsException(index);
		}
		@Override
		public IntStream intStream() {
			return IntStream.empty();
		}
	}
	public static final class IntArrayList extends AbstractList<Integer> implements RandomAccess, IntList {

		private int[] array;
		private int currentLength;
		private IntArrayList(int[] array) {

			this.array = array;
			this.currentLength = array.length;
		}
		private IntArrayList(int[] array, int initialSize) {
			this.array = array;
			this.currentLength = initialSize;
		}

		public int getAsInt(int index){
			return array[index];
		}
		@Override
		public Integer get(int index) {
			return getAsInt(index);
		}

		@Override
		public IntStream intStream() {
			return Arrays.stream(array, 0, currentLength);
		}

		@Override
		public boolean addInt(int value) {
			addInt(size(), value);
			return true;
		}
		private void ensureCapacity(int minCapacity) {
			if (minCapacity > array.length) {
				modCount++;
				grow(minCapacity);
			}
		}
		private int[] grow() {
			return grow(currentLength + 1);
		}
		private int[] grow(int minCapacity) {
			return array = Arrays.copyOf(array,
					newCapacity(minCapacity));
		}
		private int newCapacity(int minCapacity) {
			// overflow-conscious code
			int oldCapacity = array.length;
			int newCapacity = oldCapacity + (oldCapacity >> 1);
			if (newCapacity - minCapacity <= 0) {
				if (minCapacity < 0) // overflow
					throw new OutOfMemoryError();
				return minCapacity;
			}
			return (newCapacity - MAX_ARRAY_SIZE <= 0)
					? newCapacity
					: hugeCapacity(minCapacity);
		}
		private static int hugeCapacity(int minCapacity) {
			if (minCapacity < 0) // overflow
				throw new OutOfMemoryError();
			return (minCapacity > MAX_ARRAY_SIZE)
					? Integer.MAX_VALUE
					: MAX_ARRAY_SIZE;
		}
		/**
		 * The maximum size of array to allocate (unless necessary).
		 * Some VMs reserve some header words in an array.
		 * Attempts to allocate larger arrays may result in
		 * OutOfMemoryError: Requested array size exceeds VM limit
		 */
		private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

		@Override
		public void addInt(int index, int value) {
			if(currentLength < array.length){
				//don't need to grow the array as we have extra space
				for(int i = currentLength-1; i>=index; i--){
					array[i+1] = array[i];
				}
				array[index] = value;

			}else if(index == array.length){
				array= Arrays.copyOf(array, newCapacity(currentLength+1));
				array[index] = value;


			}else {

				int[] newArray = new int[newCapacity(currentLength + 1)];
				System.arraycopy(array, 0, newArray, 0, index);
				newArray[index] = value;
				System.arraycopy(array, index, newArray, index + 1, array.length - index);

				array = newArray;
			}
			currentLength++;

		}

		@Override
		public int size() {
			return currentLength;
		}

		@Override
		public Integer set(int index, Integer element) {
			Integer old = array[index];
			array[index] = element;
			modCount++;
			return old;
		}

		@Override
		public void add(int index, Integer element) {
			addInt(index, element);
		}

		@Override
		public Integer remove(int index) {
			int removedValue = array[index];
			for(int i = currentLength-1; i>index; i--){
				array[i-1] = array[i];
			}
			currentLength--;
			return removedValue;
		}

		public int indexOf(Object o) {
           if(o==null){
        	   return -1;
           }
           if(!(o instanceof Integer)){
        	   return -1;
           }
           int val = ((Integer)o).intValue();
           
            for (int i=0; i<currentLength; i++){
                if (val==array[i]){
                    return i;
                }
            }
            return -1;
        }

        public boolean contains(Object o) {
            return indexOf(o) != -1;
        }
        /**
         * Optimization of equals since
         * we know we are have an array of ints
         * this should reduce boxing/unboxing
         * on our end at least.
         */
        @SuppressWarnings("rawtypes")
		public boolean equals(Object o) {
        	if (o == this){
        	    return true;
        	}
        	if (!(o instanceof List)){
        	    return false;
        	}
        	if( o instanceof IntArrayList) {
        		//quick equals check without boxing
        		return Arrays.equals(array, 0, currentLength,
						((IntArrayList)o).array, 0, currentLength);
        	}
        	List otherList = (List)o;
			if(currentLength != otherList.size()) {
        		return false;
        	}
        	int currentOffset=0;
        	ListIterator e2 = otherList.listIterator();
        	while(currentOffset<currentLength && e2.hasNext()) {
        	    Object o2 = e2.next();
        	    //will return false if o2 is null
        	    if(!(o2 instanceof Integer)){
        	    	return false;
        	    }
        	    if(array[currentOffset] !=((Integer)o2).intValue()){
        	    	return false;
        	    }
        	    currentOffset++;
        	}
        	return !(currentOffset< currentLength || e2.hasNext());
            }

            /**
             * Optimization of hashcode since
             * we know we have an array of ints.
             */
            public int hashCode() {

				if(array.length == currentLength){
					return Arrays.hashCode(array);
				}
				//code based on Arrays.hashcode() but we restrict length to currentLength

				int result = 1;
				for (int i=0; i<currentLength; i++) {
					result = 31 * result + array[i];
				}

				return result;
            }
            /**
             * Get a new {@link java.util.PrimitiveIterator.OfInt} primitive int
             * iterator (not threadsafe).
             * @return a new OfInt.
             * @since 6.0
             */
            public OfInt intIterator() {
            	return new IntIterator();
            }
		
            private class IntIterator implements OfInt{
            	private int currentOffset=0;
            	private final int  expectedModCount;
            	
				@Override
				public boolean hasNext() {
					return currentOffset<currentLength;
				}

				public IntIterator() {
					this.expectedModCount = modCount;
				}

				@Override
				public int nextInt() {
					if(!hasNext()) {
						throw new NoSuchElementException();
					}
					if(expectedModCount != modCount) {
						throw new ConcurrentModificationException();
					}
					return array[currentOffset++];
				}
            	
            }
	}
	/**
	 * In-place reverse the given array.
	 * @param array
	 * @since 6.0
	 */
	public static void reverse(int[] array) {
		int mid = array.length/2;
		for(int i = 0, j=array.length-1; i < mid; i++, j--){
		    int tmp = array[i];
		    array[i] = array[j];
		    array[j] = tmp;
		}
		
	}
	/**
	 * In-place reverse the given array.
	 * @param array
	 * @since 6.0
	 */
	public static void reverse(Object[] array) {
		int mid = array.length/2;
		for(int i = 0, j=array.length-1; i < mid; i++, j--){
		    Object tmp = array[i];
		    array[i] = array[j];
		    array[j] = tmp;
		}
		
	}
	
}
