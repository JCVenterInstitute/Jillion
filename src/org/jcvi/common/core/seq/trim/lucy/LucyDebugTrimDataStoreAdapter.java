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

package org.jcvi.common.core.seq.trim.lucy;

import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.trim.TrimDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class LucyDebugTrimDataStoreAdapter implements TrimDataStore{

    public static TrimDataStore adapt(LucyDebugTrimRecordDataStore datastore){
        return new LucyDebugTrimDataStoreAdapter(datastore);
    }

    private final LucyDebugTrimRecordDataStore delegate;

    /**
     * @param delegate
     */
    private LucyDebugTrimDataStoreAdapter(LucyDebugTrimRecordDataStore delegate) {
        this.delegate = delegate;
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
    public Range get(String id) throws DataStoreException {
        return delegate.get(id).getClearRange();
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
    public int size() throws DataStoreException {
        return delegate.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
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
        return new CloseableIterator<Range>() {
            CloseableIterator<LucyDebugTrimRecord> iter = delegate.iterator();

            @Override
            public void remove() {
                iter.remove();                
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public void close() throws IOException {
                iter.close();                
            }

            @Override
            public Range next() {
                return iter.next().getClearRange();
            }            
        };
    }
    
    
}
