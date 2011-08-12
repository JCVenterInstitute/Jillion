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
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;

public abstract class AbstractTraceDataStoreAdapter<D extends Trace, T> implements DataStore<T> {

    private final DataStore<D> delegate;

    protected  abstract T adapt(D delegate);
    /**
     * @param delegate
     */
    public AbstractTraceDataStoreAdapter(DataStore<D> delegate) {
        this.delegate = delegate;
    }

    @Override
    public final boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    @Override
    public final T get(String id) throws DataStoreException {
        D result = delegate.get(id);
        if(result==null){
            return null;
        }
        return adapt(result);
    }

    @Override
    public final CloseableIterator<String> getIds() throws DataStoreException {
        return delegate.getIds();
    }

    @Override
    public final int size() throws DataStoreException {
        return delegate.size();
    }

    @Override
    public final void close() throws IOException {
        delegate.close();
        
    }
    

    @Override
    public final boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }
    @Override
    public final CloseableIterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }
}
