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

package org.jcvi.datastore.zip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.zip.ZipInputStream;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.H2BinaryDataStore;

/**
 * {@code H2ZipDataStore} is a {@link ZipDataStore}
 * implementation that stores the data from a ZIP 
 * file inside an H2 database.
 * @author dkatzel
 *
 *
 */
public class H2ZipDataStore extends AbstractInMemoryZipDataStore{

    private final H2BinaryDataStore datastore;
    /**
     * Create an in memory H2 Datastore.
     * @param inputStream
     * @throws IOException
     */
    public H2ZipDataStore(ZipInputStream inputStream) throws IOException {
        try {
            datastore = new H2BinaryDataStore();
        } catch (DataStoreException e) {
           throw new IOException("error setting up H2 binary datastore",e);
        }
        this.insert(inputStream);
    }
    public H2ZipDataStore(ZipInputStream inputStream, String pathToDataStore) throws IOException {
        try {
            datastore = new H2BinaryDataStore(pathToDataStore);
        } catch (DataStoreException e) {
           throw new IOException("error setting up H2 binary datastore",e);
        }
        this.insert(inputStream);
    }
   

    /**
    * {@inheritDoc}
    */
    @Override
    protected void addRecord(String entryName, byte[] data) throws IOException{
        try {
            datastore.insertRecord(entryName, data);
        } catch (SQLException e) {
            throw new IOException("error inserting entry data into binary datastore", e);
        }
        
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return datastore.contains(id);
    }

    @Override
    public InputStream get(String id) throws DataStoreException {
        super.get(id);
        ByteBuffer buffer = datastore.get(id);
        return new ByteArrayInputStream(buffer.array());
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        super.getIds();
        return datastore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        super.size();
        return datastore.size();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        datastore.close();
    }

}
