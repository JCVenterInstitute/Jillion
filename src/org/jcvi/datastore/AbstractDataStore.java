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
package org.jcvi.datastore;

import java.io.IOException;

import org.jcvi.util.CloseableIterator;

public abstract class  AbstractDataStore<T> implements DataStore<T>{
    private boolean isClosed;
    
    private synchronized void throwExceptionIfClosed() throws DataStoreException {
        if(isClosed){
            throw new DataStoreException("DataStore is closed");
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        isClosed = true;
    }

    public synchronized boolean isClosed() {
        return isClosed;
    }
    
    @Override
    public synchronized CloseableIterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        throwExceptionIfClosed();
        return false;
    }

    @Override
    public synchronized T get(String id) throws DataStoreException {
        throwExceptionIfClosed();
        return null;
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        throwExceptionIfClosed();
        return null;
    }

    @Override
    public synchronized int size() throws DataStoreException {
        throwExceptionIfClosed();
        return 0;
    }
    
}
