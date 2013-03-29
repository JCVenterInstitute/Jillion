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
package org.jcvi.jillion.core.util.iter;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@code StreamingIterator} is an
 * {@link Iterator} 
 * that might not have its next element
 * loaded into memory.  {@link StreamingIterator}s
 * are useful when iterating over elements that are 
 * resource intensive so that only as few as the
 * current element need to actually be loaded into memory
 * at one time.  {@link StreamingIterator}s are
 * often used to iterate over records stored in files.
 * Client code must explicitly 
 * {@link #close()} this iterator when done iterating
 * (preferably in a try-finally block) so
 * that any resources used by this iterator
 * can be cleaned up.  Not completely iterating over all the objects in this iterator
 * <strong>and</strong> not calling {@link #close()} could in some implementations 
 * cause memory leaks,
 * deadlocks and/or permanently blocked background threads.
 * Closing a {@link StreamingIterator} before it has
 * finished iterating over all the records
 * will cause {@link #hasNext()}
 * to return {@code false} and {@link #next()}
 * to throw a {@link NoSuchElementException} as if this iterator
 * has finished iterating.
 * <p/>
 * <strong>NOTE:</strong> some implementations
 * might throw unchecked exceptions in
 * {@link #hasNext()} or {@link #next()}
 * if there are problems fetching the next element
 * to be iterated.
 * @author dkatzel
 *
 *
 */
public interface StreamingIterator<T> extends Closeable, Iterator<T>{
	/**
	 * Does this iterator have any elements left
	 * to iterate.
	 * @returns {@code false} if this iterator
	 * has been closed or if there are no more
	 * elements left to iterate.
	 * @throws RuntimeException (unchecked)
	 * if the iterator has not
	 * yet been explicitly closed
	 * and 
	 * there is a problem determining
	 * if there is a next element.
	 */
	@Override
    boolean hasNext();
	
	/**
    * Close this iterator and clean up
    * any open resources. This will
    * force this iterator's {@link #hasNext()}
    * to return {@code false}
    * and {@link #next()} to throw
    * a {@link NoSuchElementException}
    * as if there were no more elements
    * to iterate over.  
    * <p/>
    * If this method is not
    * explicitly called and this iterator
    * still has elements left to iterate over,
    * then some implementations could cause memory leaks,
    * deadlocks and/or permanently blocked threads. 
    */
    @Override
    void close() throws IOException;
    /**
     * Returns the next element in the iterator.
     * @throws NoSuchElementException if
     * this iterator has been closed; or if there are 
     * no more elements to iterate over.
     * @throws RuntimeException (unchecked)
	 * if the iterator has not
	 * yet been explicitly closed
	 * and either
	 * there is a problem determining
	 * if there is a next element
	 * or there is a problem
	 * fetching/creating the next element to return.
     */
    @Override
    T next();
    /**
     * Not supported; will always throw
     * UnsupportedOperationException.
     * @throws UnsupportedOperationException always.
     */
    @Override
    void remove();

}
