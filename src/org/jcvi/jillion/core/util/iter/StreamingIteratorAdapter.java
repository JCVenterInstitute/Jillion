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

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@code StreamingIteratorAdapter} is an adapter
 * that will adapt an {@link Iterator} instance
 * into a {@link StreamingIterator}.
 * @author dkatzel
 *
 *
 */
final class StreamingIteratorAdapter<T> implements StreamingIterator<T>{

	private final Iterator<T> iterator;
	private volatile boolean isClosed = false;
    /**
     * Adapt the given (non-null) {@link Iterator} instance
     * into a {@link StreamingIterator}.
     * @param <T> the type of elements the iterator iterates over.
     * @param iterator the iterator to adapt into a CloseableIterator.
     * @return a new {@link StreamingIterator}
     * @throws NullPointerException if iterator is {@code null}.
     */
    public static <T> StreamingIteratorAdapter<T> adapt(Iterator<T> iterator){
        return new StreamingIteratorAdapter<T>(iterator);
    }
   
    /**
     * @param iterator
     */
    private StreamingIteratorAdapter(Iterator<T> iterator) {
    	if(iterator ==null){
    		throw new NullPointerException("iterator can not be null");
    	}
        this.iterator = iterator;
    }

    /**
    * Close this iterator.  If the adapted
    * iterator happens to be a {@link StreamingIterator}
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
        if(iterator instanceof StreamingIterator){
            ((StreamingIterator<T>)iterator).close();
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
    	throw new UnsupportedOperationException();	
        
    }
    
    
    
}
