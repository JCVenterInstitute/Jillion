/*
 * Created on Nov 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

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
