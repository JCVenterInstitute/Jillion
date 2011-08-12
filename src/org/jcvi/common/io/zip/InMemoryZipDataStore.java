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
package org.jcvi.common.io.zip;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;
/**
 * An {@code InMemoryZipDataStore} is a {@link ZipDataStore} implementation
 * that unzips the given
 * @author dkatzel
 *
 *
 */
public class InMemoryZipDataStore extends AbstractInMemoryZipDataStore{

    private final Map<String, ByteBuffer> contents = new HashMap<String, ByteBuffer>();
    /**
     * Create an {@link InMemoryZipDataStore} from the given {@link File}.
     * @param zipFile a zipFile as a {@link File}.
     * @return a new {@link InMemoryZipDataStore} (can not be null).
     * @throws IOException if there is a problem parsing the zipFile.
     */
    public static InMemoryZipDataStore createInMemoryZipDataStoreFrom(File zipFile) throws IOException{
        return createInMemoryZipDataStoreFrom(new ZipFile(zipFile));
    }
    /**
     * Create an {@link InMemoryZipDataStore} from the given {@link ZipFile}.
     * @param zipFile a zipFile as a {@link ZipFile}.
     * @return a new {@link InMemoryZipDataStore} (can not be null).
     * @throws IOException if there is a problem parsing the zipFile.
     * @throws NullPointerException if zipFile is null.
     */
    public static InMemoryZipDataStore createInMemoryZipDataStoreFrom(ZipFile zipFile) throws IOException{
        ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile.getName()));
        try{
            return new InMemoryZipDataStore(in);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Create an {@link InMemoryZipDataStore} from the given {@link InputStream}
     * of a zip file.
     * @param inputStream an {@link InputStream}
     * of a zip file.
     * @return a new {@link InMemoryZipDataStore} (can not be null).
     * @throws IOException if there is a problem parsing the inputStream.
     * @throws NullPointerException if inputStream is null.
     */
    public static InMemoryZipDataStore createInMemoryZipDataStoreFrom(InputStream inputStream) throws IOException{
        return createInMemoryZipDataStoreFrom(new ZipInputStream(inputStream));
    }
    /**
     * Create an {@link InMemoryZipDataStore} from the given {@link ZipInputStream}
     * of a zip file.
     * @param inputStream an {@link ZipInputStream}
     * of a zip file.
     * @return a new {@link InMemoryZipDataStore} (can not be null).
     * @throws IOException if there is a problem parsing the zipInputStream.
     * @throws NullPointerException if zipInputStream is null.
     */
    public static InMemoryZipDataStore createInMemoryZipDataStoreFrom(ZipInputStream zipInputStream) throws IOException{
        return new InMemoryZipDataStore(zipInputStream);
    }
    private InMemoryZipDataStore(ZipInputStream inputStream) throws IOException{
        insert(inputStream);
    }
    
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return contents.containsKey(id);
    }

    @Override
    public synchronized InputStream get(String id) throws DataStoreException {
        super.get(id);
        ByteBuffer buffer = contents.get(id);
        return new ByteArrayInputStream(buffer.array());
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        super.getIds();
        return CloseableIteratorAdapter.adapt(contents.keySet().iterator());
    }

    @Override
    public synchronized int size() throws DataStoreException {
        super.size();
        return contents.size();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        contents.clear();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    protected void addRecord(String entryName, byte[] data) {
        contents.put(entryName, ByteBuffer.wrap(data));
        
    }

    

}
