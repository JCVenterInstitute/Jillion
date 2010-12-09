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

package org.jcvi.datastore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import org.jcvi.Range;
import org.jcvi.util.CloseableIterator;
import org.jcvi.util.IndexedFileRange;

/**
 * {@code H2IndexedFileRange} is an {@link IndexedFileRange}
 * implementation that uses an H2 database to store
 * the index information.
 * @author dkatzel
 *
 *
 */
public class H2IndexedFileRange implements IndexedFileRange{

    private final H2BinaryDataStore datastore;
    /**
     * Create an in-memory H2 database to store
     * the index information.
     * @throws DataStoreException if
     * there was a problem creating the H2 database.
     */
    public H2IndexedFileRange() throws DataStoreException{
        datastore = new H2BinaryDataStore();
    }
    /**
     * Create a file H2 database to store the index
     * information.
     * @param filePrefix the file prefix of the 
     * H2 file database to create.  The H2 database will
     * make files named filePrefix.h2.db and filePrefix.lock.db
     * and possibly others.
     * @throws DataStoreException if
     * there was a problem creating the H2 database.
     */
    public H2IndexedFileRange(String filePrefix) throws DataStoreException{
        datastore = new H2BinaryDataStore(filePrefix);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range getRangeFor(String id) {
        try {
            ByteBuffer buf =datastore.get(id);
            return Range.buildRange(buf.getLong(), buf.getLong());
        } catch (DataStoreException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) {
        try {
            return datastore.contains(id);
        } catch (DataStoreException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void put(String id, Range range) {
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.putLong(range.getStart());
        buf.putLong(range.getEnd());
        try {
            datastore.insertRecord(id, buf.array());
        } catch (SQLException e) {
            throw new IllegalStateException("error inserting record into H2IndexedFileRange",e);
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void remove(String id) {
        //no-op
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        try {
            return datastore.isClosed();
        } catch (DataStoreException e) {
           throw new IllegalStateException(e);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() {
        try {
            datastore.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() {
        try {
            return datastore.size();
        } catch (DataStoreException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() {
        try {
            return datastore.getIds();
        } catch (DataStoreException e) {
            throw new IllegalStateException(e);
        }
    }

    

}
