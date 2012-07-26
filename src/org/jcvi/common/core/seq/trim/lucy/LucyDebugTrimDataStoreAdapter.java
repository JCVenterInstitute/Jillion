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
import org.jcvi.common.core.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class LucyDebugTrimDataStoreAdapter implements TrimPointsDataStore{

	 private final LucyDebugTrimRecordDataStore delegate;
	 
    public static TrimPointsDataStore adapt(LucyDebugTrimRecordDataStore datastore){
        return new LucyDebugTrimDataStoreAdapter(datastore);
    }

   

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
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return delegate.idIterator();
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
    public long getNumberOfRecords() throws DataStoreException {
        return delegate.getNumberOfRecords();
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
     * @throws DataStoreException 
    */
    @Override
    public StreamingIterator<Range> iterator() throws DataStoreException {
        return new StreamingIterator<Range>() {
            StreamingIterator<LucyDebugTrimRecord> iter= delegate.iterator();

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
