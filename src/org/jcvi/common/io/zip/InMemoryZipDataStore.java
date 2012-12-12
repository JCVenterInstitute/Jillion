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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.datastore.impl.AbstractDataStore;
import org.jcvi.common.core.datastore.impl.DataStoreStreamingIterator;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * An {@code InMemoryZipDataStore} is a {@link ZipDataStore} implementation
 * that unzips the given zipped data and stores it in a Map with ByteBuffer
 * keys.
 * @author dkatzel
 *
 *
 */
public final class InMemoryZipDataStore extends AbstractDataStore<InputStream> implements ZipDataStore{

    private final Map<String, ByteBuffer> contents = new LinkedHashMap<String, ByteBuffer>();
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
    /**
     * Create a {@link DataStore} of <String, Inputstream> entries
     * one for each {@link ZipEntry} in this zip file. 
     * @param inputStream the inputstream of the zip file to convert
     * into a datastore.
     * @throws IOException if there is a problem reading the inputstream.
     */
    private InMemoryZipDataStore(ZipInputStream inputStream) throws IOException{
    	ZipEntry entry = inputStream.getNextEntry();
        while(entry !=null){
            String name = entry.getName();
            //depending on zip implementation, 
            //we might not know file size so entry.getSize() will return -1
            //therefore must use byteArrayoutputStream.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtil.copy(inputStream, output);
            addRecord(name, output.toByteArray());  
            entry = inputStream.getNextEntry();
        }
    }
    
    
    @Override
	protected boolean containsImpl(String id) {
		return contents.containsKey(id);
	}
	@Override
	protected InputStream getImpl(String id) {
		 ByteBuffer buffer = contents.get(id);
        return new ByteArrayInputStream(buffer.array());
	}
	@Override
	protected long getNumberOfRecordsImpl() {
		return contents.size();
	}
	@Override
	protected StreamingIterator<String> idIteratorImpl() {
		return DataStoreStreamingIterator.create(this, contents.keySet().iterator());
	}
	@Override
	protected StreamingIterator<InputStream> iteratorImpl() {
		return DataStoreStreamingIterator.create(this, new DataStoreIterator<InputStream>(this));
	}
	
   
   
    
    /**
     * 
     * {@inheritDoc}
     * <p/>
     * Closes the datastore and removes clears
     * all the contents of this zip file from the heap.
     * (but does not delete the file).
     */
    @Override
	protected void handleClose() throws IOException {
    	 contents.clear();
		
	}
	/**
     * Add the entry with the given entry name and its corresponding
     * data to this datastore.
     * @param entryName
     * @param data
     */
    private void addRecord(String entryName, byte[] data) {
        contents.put(entryName, ByteBuffer.wrap(data));
        
    }

    

}
