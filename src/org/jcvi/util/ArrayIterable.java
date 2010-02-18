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
/**
 * ArrayIterable.java
 *
 * Created: Oct 10, 2008 - 10:44:08 AM (jsitz)
 *
 * Copyright 2008 J. Craig Venter Institute
 */
package org.jcvi.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * An <code>ArrayIterable</code> is a convenience wrapper for Object arrays to
 * allow them to be treated as {@link Iterable}s.  While it is already valid
 * to loop on arrays as if they used the <code>Iterable</code> interface, there
 * may be many situations where it is useful to wrap them in some of the more
 * useful interfaces without having to instantiate a {@link List} and be
 * subject to the other side effects like the possibility of modification.
 * <p>
 * An <code>ArrayIterable</code> will provide the <code>Iterable</code> 
 * interface as well as a specialized {@link Iterator} which will allow 
 * (essentially) thread-safe access to the array.  Multiple iterators on the 
 * array will not interfere with each other, and they will share the same view
 * of the array.  This means that if the array changes during iteration, the
 * changed data will be immediately visible by all <code>ArrayIterable</code>s
 * linked to that array.
 * <p>
 * Note that the iterable can be declared to iterate on a different class than
 * the supplied array.  The array merely needs to extent or implement the 
 * declared class.
 *
 * @author jsitz@jcvi.org
 * @param <T> The type of object to iterate over.
 */
public class ArrayIterable<T> implements Iterable<T>
{
    

    /** The array to iterate over */
    private final T[] array;

    /**
     * Creates a new <code>ArrayIterable</code>.
     * <p>
     * Note that the type of the array supplied need not match the declared
     * return type of the iterator.  This allows for the {@link Iterable} to 
     * perform some level of type abstraction if necessary.
     * 
     * @param array The array to iterate over (can not be {@code null}).
     * @param <S> The type of the array.
     * @throws NullPointerException if given array is {@code null}.
     */
    public <S extends T> ArrayIterable(S[] array)
    {
        super();
        if(array ==null){
            throw new NullPointerException("array can not be null");
        }
        //defensive copy
        this.array = Arrays.copyOf(array,array.length);
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator()
    {
        return new ArrayIterator<T>(this.array);
    }
}
