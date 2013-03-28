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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * {@code ChainedStreamingIterator}
 * is a {@link StreamingIterator} that chains
 * multiple {@link StreamingIterator}s
 * together similar to {@link ChainedIterator}
 * but for {@link StreamingIterator}s
 * 
 * @author dkatzel
 *
 *
 */
final class ChainedStreamingIterator<T> implements StreamingIterator<T>{

    private final List<StreamingIterator<? extends T>> delegates;
    private final Iterator<? extends T> iterator;
    
    /**
     * @param delegates
     */
    public ChainedStreamingIterator(Collection<? extends StreamingIterator<? extends T>> delegates) {
    	if(delegates.contains(null)){
            throw new NullPointerException("can not contain null iterator");
        }
        this.delegates = new ArrayList<StreamingIterator<? extends T>>(delegates);
        
        this.iterator = ChainedIterator.create(delegates);
    }

    /**
    * Close all the iterators
    * being chained together.
    */
    @Override
    public void close() throws IOException {
      for(StreamingIterator<? extends T> delegate : delegates){
            IOUtil.closeAndIgnoreErrors(delegate);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public T next() {
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
