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
/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core;

import java.util.Iterator;

/**
 * {@code Sequence} is an interface for an
 * ordered list of objects.  How
 * this sequence is stored is abstracted
 * away so that different implementations
 * may encode or compress the symbols
 * so that they take up less memory.
 * @author dkatzel
 *
 *
 */
public interface Sequence<T> extends Iterable<T>{
   
    /**
     * Gets the specific element at the specified index.
     * @param offset the 0-based offset of the element to get.
     * @return the element at the specified index;
     * will never be null.
     * @throws IndexOutOfBoundsException if the given offset
     * is negative or beyond the last offset in
     * the sequence.
     */
    T get(long offset);
    /**
     * Get the number of elements that are in
     * this sequence.
     * @return the length, will never
     * be less than {@code 0}.
     */
    long getLength();
    @Override
    int hashCode();
    /**
     * Two sequences should be equal
     * if they are both the same
     * length and contain the same 
     * elements in the same order.
     */
    @Override
    boolean equals(Object obj);

    /**
     * Create a new {@link Iterator}
     * which only iterates over the specified
     * Range of elements in this sequence.
     * @param range the range to iterate over.
     * @return a new {@link Iterator}; will never
     * be null.
     * @throws NullPointerException if range is null.
     * @throws IndexOutOfBoundsException if Range contains
     * values outside of the possible sequence offsets.
     */
    Iterator<T> iterator(Range range);
}
