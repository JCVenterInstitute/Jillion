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
/*
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * {@code EmptyIterator} is a NullObject implementation
 * of {@link Iterator}; an EmptyIterator will never 
 * have any elements to iterate over.
 * @author dkatzel
 *
 *
 */
final class EmptyIterator<E> implements Iterator<E> {
    /**
     * Singleton instance of Empty iterator that can be shared 
     * by all.
     */
    @SuppressWarnings("rawtypes")
	static final EmptyIterator INSTANCE  = new EmptyIterator();
    /**
     * Private constructor so no one can subclass.
     */
    private EmptyIterator(){}
    /**
     * Never has a next.
     * @return {@code false}
     */
    @Override
    public boolean hasNext() {
        return false;
    }
    /**
     * Will always throw an NoSuchElementException.
     * @throws NoSuchElementException because there will never be a next.
     */
    @Override
    public E next() {
        throw new NoSuchElementException("no elements in empty iterator");
    }
    /**
     * Does nothing.
     */
    @Override
    public void remove() {
        //no-op
    }

}
