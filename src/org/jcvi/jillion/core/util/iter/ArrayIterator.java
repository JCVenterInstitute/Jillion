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
package org.jcvi.jillion.core.util.iter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {

	private final T[] array;
	private int count;
	
	/**
	 * Create a new ArrayInterator instance
	 * that will iterate over a defensive copy
	 * of the input array.
	 * 
	 * @param array the array to iterate over; can not be null.
	 * 
	 * @throws NullPointerException if array is null.
	 * 
	 * @apiNote this is the same as {@code new ArrayIterator<>(array, true) }
	 * 
	 * @see #ArrayIterator(Object[], boolean)
	 */
	public ArrayIterator(T[] array) {
		this(array, true);
	}
	
	/**
	 * Create a new ArrayInterator instance
	 * that will iterate over a defensive copy
	 * of the input array.
	 * @param array the array to iterate over; can not be null.
	 * @param makeDefensiveCopy should a defensive copy of the input array
	 * be used.
	 * @throws NullPointerException if array is null.
	 */
	public ArrayIterator(T[] array, boolean makeDefensiveCopy) {
		if(array==null){
			throw new NullPointerException("array can not be null");
		}
		if(makeDefensiveCopy){
			this.array = Arrays.copyOf(array, array.length);
		}else{
			this.array = array;
		}
		
	}

	@Override
	public boolean hasNext() {
		return count<array.length;
	}

	@Override
	public T next() {
		if(!hasNext()){
			throw new NoSuchElementException();
		}
		return array[count++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");
		
	}

}
