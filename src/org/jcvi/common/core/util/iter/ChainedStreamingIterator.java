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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;

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

    private final List<StreamingIterator<T>> delegates;
    private final Iterator<T> iterator;
    
    /**
     * @param delegates
     */
    public ChainedStreamingIterator(Collection<? extends StreamingIterator<T>> delegates) {
    	if(delegates.contains(null)){
            throw new NullPointerException("can not contain null iterator");
        }
        this.delegates = new ArrayList<StreamingIterator<T>>(delegates);
        this.iterator = ChainedIterator.create(delegates);
    }

    /**
    * Close all the iterators
    * being chained together.
    */
    @Override
    public void close() throws IOException {
      for(StreamingIterator<T> delegate : delegates){
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
