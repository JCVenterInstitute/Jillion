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
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.datastore;

import java.io.IOException;

import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code AbstractDataStore} is an abstract implementation
 * of {@link DataStore} that handles closing
 * the datastore for its concrete subclasses.
 * Any subclass of {@link AbstractDataStore}
 * needs have its overriding methods marked as synchronized
 * and call to 
 * super in order to check if the datastore is already
 * closed.
 * @author dkatzel
 *
 * @param <T>
 */
public abstract class  AbstractDataStore<T> implements DataStore<T>{
    private volatile boolean isClosed;
    
    protected final void throwExceptionIfClosed() {
        if(isClosed){
            throw new IllegalStateException("DataStore is closed");
        }
    }
    /**
     * This method is called
     * when a datastore is closed
     * via the {@link #close()}.
     * Implementations should
     * use this method to handle
     * any resource cleanup.
     */
    protected abstract void handleClose() throws IOException;
    @Override
    public final synchronized void close() throws IOException {
    	if(!isClosed){
    		handleClose();
    	}
        isClosed = true;
    }

    public final boolean isClosed() {
        return isClosed;
    }
    
    @Override
    public StreamingIterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        throwExceptionIfClosed();
        return false;
    }

    @Override
    public T get(String id) throws DataStoreException {
        throwExceptionIfClosed();
        return null;
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        throwExceptionIfClosed();
        return null;
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        throwExceptionIfClosed();
        return 0;
    }
    
}
