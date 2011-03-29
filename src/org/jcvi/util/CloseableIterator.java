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

package org.jcvi.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@code CloseableIterator} is an
 * {@link Iterator} that also
 * implements {@link Closeable}.
 * This can allow an iterator to be closed
 * and clean up any resources it is using
 * before the iterator reaches
 * the end of iterating over all the elements.
 * Calling {@link #close()} even when
 * {@link #hasNext()} returns {@code true}
 * should halt iterating.
 * @author dkatzel
 *
 *
 */
public interface CloseableIterator<T> extends Closeable, Iterator<T>{
	/**
	 * Does this iterator have any elements left
	 * to iterate.
	 * @returns {@code false} if this iterator
	 * has been closed or if there are no more
	 * elements left to iterate.
	 */
	@Override
    boolean hasNext();
	
	/**
    * Close this iterator and clean up
    * any open resources.  This will
    * force this iterator's {@link #hasNext()}
    * to return {@code false}
    * and {@link #next()} to throw
    * a {@link NoSuchElementException}
    * as if there were no more elements
    * to iterate over.  
    */
    @Override
    void close() throws IOException;
    /**
     * @throws NoSuchElementException if
     * this iterator has been closed; or if there are 
     * no more elements to iterate over.
     */
    @Override
    T next();
}
