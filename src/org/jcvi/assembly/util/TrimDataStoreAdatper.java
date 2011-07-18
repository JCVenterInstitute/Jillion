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

package org.jcvi.assembly.util;

import java.io.IOException;

import org.jcvi.Range;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.CloseableIterator;

/**
 * {@code TrimDataStoreAdatper} wraps
 * a {@link DataStore} of {@link Range}s around a   
 * {@link TrimDataStore}.
 * wrapper
 * @author dkatzel
 *
 *
 */
public class TrimDataStoreAdatper implements TrimDataStore{

    private final DataStore<Range> delegate;
    /**
     * Adapt the given {@link DataStore} of {@link Range}s
     * into a {@link TrimDataStore}.
     * @param toBeAdapted the datastore to be adapted into a TrimDataStore.
     * @return a new {@link TrimDataStore} which wraps the given
     * {@link DataStore}
     * @throws NullPointerException if the given datastore is null.
     */
    public static TrimDataStoreAdatper adapt(DataStore<Range> toBeAdapted){
        return new TrimDataStoreAdatper(toBeAdapted);
    }
    /**
     * Adapt the given {@link DataStore} of {@link Range}s
     * into a {@link TrimDataStore}.
     * @param delegate the datastore to be adapted into a TrimDataStore.
     * @return a new {@link TrimDataStore} which wraps the given
     * {@link DataStore}
     * @throws NullPointerException if the given datastore is null.
     */
    private TrimDataStoreAdatper(DataStore<Range> delegate) {
        if(delegate ==null){
            throw new NullPointerException("delegate can not be null");
        }
        this.delegate = delegate;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range get(String id) throws DataStoreException {
        return delegate.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return delegate.getIds();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        return delegate.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        delegate.close();
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<Range> iterator() {
        return delegate.iterator();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }

}
