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
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class DefaultZipDataStore implements ZipDataStore{

    private final ZipFile zipfile;
    
    public DefaultZipDataStore(ZipFile zipfile){
        if(zipfile ==null){
            throw new NullPointerException("zip file can not be null");
        }
        this.zipfile = zipfile;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return zipfile.getEntry(id) !=null;
    }

    @Override
    public InputStream get(String id) throws DataStoreException {
        ZipEntry entry =zipfile.getEntry(id);
        try {
            return zipfile.getInputStream(entry);
        } catch (IOException e) {
           throw new DataStoreException("error getting "+id +" from jar datastore",e);
        }
    }

    @Override
    public Iterator<String> getIds() {
        return new EntryNameIterator(zipfile.entries());
    }

    @Override
    public int size() throws DataStoreException {
        return zipfile.size();
    }

    @Override
    public void close() throws IOException {
        zipfile.close();        
    }

    @Override
    public Iterator<InputStream> iterator() {
        return new DataStoreIterator<InputStream>(this);
    }
    
    private static final class EntryNameIterator<E extends ZipEntry> implements Iterator<String>{
        private Enumeration<E> entryEnumerator;
        private EntryNameIterator(Enumeration<E> entryEnumerator){
            this.entryEnumerator = entryEnumerator;
        }
        @Override
        public boolean hasNext() {
            return entryEnumerator.hasMoreElements();
        }
        @Override
        public String next() {
            return entryEnumerator.nextElement().getName();
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException("can not remove elements from Enumeration");
            
        }
        
        
    }
}
