/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
/**
 * {@code Sequence} is an interface for an
 * ordered list of {@link Symbol}s.  How
 * this sequence is stored is abstracted
 * away so that different implementations
 * may encode or compress the symbols
 * so that they take up less memory.
 * @author dkatzel
 *
 *
 */
public interface Sequence<T extends Symbol> extends Iterable<T>{
    /**
     * Get all the {@link Symbol}s as
     * a List.
     * @return a List of {@link Symbol}s.
     */
    List<T> asList();
    /**
     * Gets the specific {@link Symbol} at the specified index.
     * this should return the same {@link Symbol} as
     * {@code decode().get(index)} but hopefully
     * in a more efficient manner.
     * @param index the index of the {@link Symbol} to get.
     * @return the {@link Symbol} at the specified index.
     */
    T get(int index);
    /**
     * Get the number of {@link Symbol}s that are encoded.
     * This should return the same value as
     * {@code decode().size()}.
     * @return the length, will never
     * be less than {@code 0}.
     */
    long getLength();
    @Override
    int hashCode();
    
    @Override
    boolean equals(Object obj);
    /**
     * Get only the {@link Symbol}s for the given range
     * @param range the range to trim against, if null, then decode
     * all {@link Symbol}s (the same as {@link #asList()}).
     * @return
     */
    List<T> asList(Range range);
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
