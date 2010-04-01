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
package org.jcvi.datastore.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;

/**
 * {@code DefaultZipDataStore} is a default implementation
 * of {@link ZipDataStore} which fetches data contained
 * inside a {@link ZipFile}.
 * @author dkatzel
 *
 *
 */
public class DefaultZipDataStore implements ZipDataStore{

    private final ZipFile zipfile;
    /**
     * Construct a ZipDataStore for the given {@link ZipFile}.
     * @param zipfile the {@link ZipFile} to wrap, can not be null.
     * @throws NullPointerException if zipfile is null.
     */
    public DefaultZipDataStore(ZipFile zipfile){
        if(zipfile ==null){
            throw new NullPointerException("zip file can not be null");
        }
        this.zipfile = zipfile;
    }
    /**
     * Does this {@link ZipDataStore} contain an entry
     * with the given entryName.
     * @param entryName the name of the entry to check for.
     */
    @Override
    public boolean contains(String entryName) throws DataStoreException {
        return zipfile.getEntry(entryName) !=null;
    }
    /**
     * Get the InputStream of the entry with the given entryName.
     * @param entryName the name of the entry to get.
     * @return an InputStream of the entry data.
     */
    @Override
    public InputStream get(String entryName) throws DataStoreException {
        ZipEntry entry =zipfile.getEntry(entryName);
        try {
            return zipfile.getInputStream(entry);
        } catch (IOException e) {
           throw new DataStoreException("error getting "+entryName +" from zip datastore",e);
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
