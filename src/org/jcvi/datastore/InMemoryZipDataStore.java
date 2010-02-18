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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jcvi.io.IOUtil;

public class InMemoryZipDataStore extends AbstractDataStore<InputStream> implements ZipDataStore {

    private final Map<String, ByteBuffer> contents = new HashMap<String, ByteBuffer>();
    
    public InMemoryZipDataStore(ZipInputStream inputStream) throws IOException{
        
        ZipEntry entry = inputStream.getNextEntry();
        while(entry !=null){
            String name = entry.getName();
            //depending on zip implementation, 
            //we might not know file size so entry.getSize() will return -1
            //therefore must use byteArrayoutputStream.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtil.writeToOutputStream(inputStream, output);
            contents.put(name, ByteBuffer.wrap(output.toByteArray()));  
            entry = inputStream.getNextEntry();
        }
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return contents.containsKey(id);
    }

    @Override
    public InputStream get(String id) throws DataStoreException {
        super.get(id);
        ByteBuffer buffer = contents.get(id);
        return new ByteArrayInputStream(buffer.array());
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        super.getIds();
        return contents.keySet().iterator();
    }

    @Override
    public int size() throws DataStoreException {
        super.size();
        return contents.size();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        contents.clear();
    }

    

}
