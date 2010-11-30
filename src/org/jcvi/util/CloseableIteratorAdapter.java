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

import java.io.IOException;
import java.util.Iterator;

/**
 * @author dkatzel
 *
 *
 */
public class CloseableIteratorAdapter<E> implements CloseableIterator<E>{

    
    public static <E> CloseableIteratorAdapter<E> adapt(Iterator<E> iterator){
        return new CloseableIteratorAdapter<E>(iterator);
    }
    private final Iterator<E> iterator;

    /**
     * @param iterator
     */
    private CloseableIteratorAdapter(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        //no-op
        if(iterator instanceof CloseableIterator){
            ((CloseableIterator)iterator).close();
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
    public E next() {
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
