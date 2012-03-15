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

package org.jcvi.common.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code ChainedCloseableIterator}
 * is a CloseableIterator that chains
 * multiple {@link CloseableIterator}s
 * together similar to {@link ChainedIterator}
 * but for {@link CloseableIterator}s
 * 
 * @author dkatzel
 *
 *
 */
public class ChainedCloseableIterator<T> implements CloseableIterator<T>{

    private final List<CloseableIterator<T>> delegates;
    private final Iterator<T> iterator;
    
    /**
     * @param delegates
     */
    @SuppressWarnings("unchecked")
    public ChainedCloseableIterator(Collection<? extends CloseableIterator<T>> delegates) {
        this.delegates = new ArrayList<CloseableIterator<T>>(delegates);
        this.iterator = ChainedIterator.create(delegates);
    }

    /**
    * Close all the iterators
    * being chained together.
    */
    @Override
    public void close() throws IOException {
      for(CloseableIterator<T> delegate : delegates){
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
