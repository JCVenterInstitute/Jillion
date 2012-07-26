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

package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class PositionDataStoreAdapter implements PositionDataStore{

    private final DataStore<Sequence<ShortSymbol>> datastore;
    
    
    public PositionDataStoreAdapter(
            DataStore<Sequence<ShortSymbol>> datastore) {
        this.datastore = datastore;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return datastore.idIterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Sequence<ShortSymbol> get(String id) throws DataStoreException {
        return datastore.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return datastore.getNumberOfRecords();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return datastore.isClosed();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        datastore.close();
        
    }

    /**
    * {@inheritDoc}
     * @throws DataStoreException 
    */
    @Override
    public StreamingIterator<Sequence<ShortSymbol>> iterator() throws DataStoreException {
        return datastore.iterator();
    }

}
