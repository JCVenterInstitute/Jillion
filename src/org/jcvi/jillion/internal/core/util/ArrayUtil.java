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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;
import java.util.RandomAccess;
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
	
	private ArrayUtil(){
		//can not instantiate
	}
	
	public static final class IntArrayList extends AbstractList<Integer> implements RandomAccess {

		private final int[] array;
		
		private IntArrayList(int[] array) {
			this.array = array;
		}

		@Override
		public Integer get(int index) {
			return array[index];
		}

		@Override
		public int size() {
			return array.length;
		}

		@Override
		public Integer set(int index, Integer element) {
			Integer old = array[index];
			array[index] = element.intValue();
			modCount++;
			return old;
		}
		
		public int indexOf(Object o) {
           if(o==null){
        	   return -1;
           }
           if(!(o instanceof Integer)){
        	   return -1;
           }
           int val = ((Integer)o).intValue();
           
            for (int i=0; i<array.length; i++){
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
        		return Arrays.equals(array, ((IntArrayList)o).array);
        	}
        	List otherList = (List)o;
			if(array.length != otherList.size()) {
        		return false;
        	}
        	int currentOffset=0;
        	ListIterator e2 = otherList.listIterator();
        	while(currentOffset<array.length && e2.hasNext()) {
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
        	return !(currentOffset<array.length || e2.hasNext());
            }

            /**
             * Optimization of hashcode since
             * we know we have an array of ints.
             */
            public int hashCode() {
            	return Arrays.hashCode(array);
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
					return currentOffset<array.length;
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
