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

package org.jcvi.common.core.util.iter;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@code CloseableIteratorAdapter} is an adapter
 * that will adapt an {@link Iterator} instance
 * into a {@link CloseableIterator}.
 * @author dkatzel
 *
 *
 */
public class CloseableIteratorAdapter<T> implements CloseableIterator<T>{

    /**
     * Adapt the given (non-null) {@link Iterator} instance
     * into a {@link CloseableIterator}.
     * @param <T> the type of elements the iterator iterates over.
     * @param iterator the iterator to adapt into a CloseableIterator.
     * @return a new {@link CloseableIterator}
     * @throws NullPointerException if iterator is {@code null}.
     */
    public static <T> CloseableIteratorAdapter<T> adapt(Iterator<T> iterator){
        return new CloseableIteratorAdapter<T>(iterator);
    }
    private final Iterator<T> iterator;
    private boolean isClosed=false;
    /**
     * @param iterator
     */
    private CloseableIteratorAdapter(Iterator<T> iterator) {
    	if(iterator ==null){
    		throw new NullPointerException("iterator can not be null");
    	}
        this.iterator = iterator;
    }

    /**
    * Close this iterator.  If the adapted
    * iterator happens to be a {@link CloseableIterator}
    * before adaption, then its {@link #close()}
    * method will be called too.  This will
    * force this iterator's {@link #hasNext()}
    * to return {@code false}
    * and {@link #next()} to throw
    * a {@link NoSuchElementException}
    * as if there were no more elements
    * to iterate over.
    */
    @Override
    public void close() throws IOException {
        if(isClosed){
        	return ; //no-op
        }
        if(iterator instanceof CloseableIterator){
            ((CloseableIterator)iterator).close();
        }
        isClosed=true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean hasNext() {
    	if(isClosed){
    		return false;
    	}
        return iterator.hasNext();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public T next() {
    	if(isClosed){
    		throw new NoSuchElementException("iterator has been closed");
    	}
        return iterator.next();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void remove() {
        iterator.remove();
        
    }
    
    
    
}
