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
import java.util.ListIterator;
import java.util.NoSuchElementException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A ListIterator wrapping an Array but does
 * not support optional methods such as remove(), add() or set().
 * @param <T> the type of element returned in each next() or previous() call.
 */
public class ArrayIterator<T> implements ListIterator<T> {

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
	 * @apiNote this is the same as {@code new ArrayIterator<>(array,0, true) }
	 * 
	 * @see #ArrayIterator(Object[], int, boolean)
	 */
	public ArrayIterator(T[] array) {
		this(array, 0);
	}
	/**
	 * Create a new ArrayInterator instance
	 * that will iterate over a defensive copy
	 * of the input array.
	 *
	 * @param array the array to iterate over; can not be null.
	 * @param makeDefensiveCopy should a defensive copy of the input array.
	 * @throws NullPointerException if array is null.
	 *
	 * @apiNote this is the same as {@code new ArrayIterator<>(array,0, true) }
	 *
	 * @see #ArrayIterator(Object[], int, boolean)
	 */
	public ArrayIterator(T[] array, boolean makeDefensiveCopy) {
		this(array, 0, makeDefensiveCopy);
	}

	/**
	 * Create a new ArrayInterator instance
	 * that will iterate over a defensive copy
	 * of the input array.
	 *
	 * @param array the array to iterate over; can not be null.
	 *
	 * @throws NullPointerException if array is null.
	 *
	 * @apiNote this is the same as {@code new ArrayIterator<>(array,0, true) }
	 *
	 * @see #ArrayIterator(Object[], int, boolean)
	 */
	public ArrayIterator(T[] array, int initialStartIndex) {
		this(array, initialStartIndex, true);
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
	@SuppressFBWarnings("EI_EXPOSE_REP2") //FindBugs expose array reference which we want to sometimes for speed
	public ArrayIterator(T[] array,int initialStartIndex, boolean makeDefensiveCopy) {
		if(array==null){
			throw new NullPointerException("array can not be null");
		}
		if(initialStartIndex <0 || initialStartIndex > array.length){
			throw new IllegalArgumentException("initial start index must be within array bounds");
		}
		this.count = initialStartIndex;
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
	public boolean hasPrevious() {
		return count>0;
	}

	@Override
	public T previous() {
		if(!hasPrevious()){
			throw new NoSuchElementException();
		}
		return array[--count];
	}

	@Override
	public int nextIndex() {
		return count+1;
	}

	@Override
	public int previousIndex() {
		return count-1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");
		
	}

	@Override
	public void set(T t) {

	}

	@Override
	public void add(T t) {

	}

}
