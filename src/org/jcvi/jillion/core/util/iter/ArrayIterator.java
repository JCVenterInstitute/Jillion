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
