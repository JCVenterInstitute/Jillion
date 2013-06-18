package org.jcvi.jillion.internal.core.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public final class ArrayUtil {
	/**
	 * Create a new List instance that has
	 * one element for each element
	 * in the int array.
	 * The returned instance keeps
	 * the backing array as primitives so
	 * there is no memory or performance
	 * penalty for boxing/unboxing until
	 * users perform a {@link List#get(int)}
	 * @param array
	 * @return
	 */
	public static List<Integer> asList(int[] array){
		return new IntArrayList(array);
	}
	
	private ArrayUtil(){
		//can not instantiate
	}
	
	private static final class IntArrayList extends AbstractList<Integer>
	implements RandomAccess {

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

        	int currentOffset=0;
        	ListIterator e2 = ((List) o).listIterator();
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
		
	}
}
