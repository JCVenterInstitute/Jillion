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
 * Created on Nov 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The <code>ArrayIterator</code> is the internal implementation of the 
 * {@link ArrayIterable}.  It is a iterator which maintains a cursor into
 * an array while iterating over that array.
 * 
 * @param <T> The {@link Class} of object to return while iterating.
 * @author jsitz
 */
public class ArrayIterator<T> implements Iterator<T>
{
    /** The array to iterate over */
    private final T[] array;
    /** The index of the next element to return */
    private int cursor;

    /**
     * Creates a new <code>ArrayIterable.ArrayIterator</code> based on the
     * supplied array.
     * <p>
     * Note that the type of the array supplied need not match the declared
     * return type of the iterator.  This allows for the iterator to 
     * perform some level of type abstraction if necessary.
     * 
     * @param array The array to iterate over.
     * @param <V> The type of array being supplied.
     */
    public <V extends T> ArrayIterator(V[] array)
    {
         super();
         this.array = Arrays.copyOf(array,array.length);
         this.cursor = -1;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public synchronized boolean hasNext()
    {
        
        return this.cursor < (this.array.length - 1);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public synchronized T next()
    {
        if(!hasNext()){
            throw new NoSuchElementException();
        }
        this.cursor++;
        return this.array[this.cursor];
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove()
    {
        // Not implemented;
    }
}
