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

package org.jcvi.common.io.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractInMemoryZipDataStore extends AbstractDataStore<InputStream> implements ZipDataStore {

    
    protected void insert(ZipInputStream inputStream) throws IOException {
        ZipEntry entry = inputStream.getNextEntry();
        while(entry !=null){
            String name = entry.getName();
            //depending on zip implementation, 
            //we might not know file size so entry.getSize() will return -1
            //therefore must use byteArrayoutputStream.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtil.writeToOutputStream(inputStream, output);
            addRecord(name, output.toByteArray());  
            entry = inputStream.getNextEntry();
        }
    }
    
    /**
     * Add the entry with the given entry name and its corresponding
     * data to this datastore.
     * @param entryName
     * @param data
     */
    protected abstract void addRecord(String entryName, byte[] data) throws IOException;
   


}
