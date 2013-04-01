/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code LargePhdDataStore} is a {@link PhdDataStore} implementation
 * to be used a very large phd files or phdballs.  No data contained in this
 * phd file is stored in memory except the number of phd records (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the phd file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 *
 */
public final class LargePhdDataStore implements PhdDataStore{

    static final Pattern BEGIN_SEQUENCE_PATTERN = Pattern.compile("BEGIN_SEQUENCE\\s+(\\S+)");
    private final File phdFile;
    private Long size=null;
    boolean closed = false;
    
    /**
     * @param phdFile
     */
    public LargePhdDataStore(File phdFile) {
        if(!phdFile.exists()){
            throw new IllegalArgumentException("phd file does not exists "+ phdFile.getAbsolutePath());
        }
        this.phdFile = phdFile;
    }

    private void checkIfClosed(){
        if(closed){
            throw new IllegalStateException("datastore is closed");
        }
    }
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        checkIfClosed();
        StreamingIterator<Phd> iter = iterator();
        while(iter.hasNext()){
            Phd phd = iter.next();
            if(phd.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized Phd get(String id) throws DataStoreException {
        checkIfClosed();
        StreamingIterator<Phd> iter = iterator();
        while(iter.hasNext()){
            Phd phd = iter.next();
            if(phd.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return phd;
            }
        }
        //not found
        return null;
        
    }

    @Override
    public synchronized StreamingIterator<String> idIterator() throws DataStoreException {
        checkIfClosed();
        return new PhdIdIterator();
       
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkIfClosed();
        if(size ==null){
            long count=0;
            StreamingIterator<Phd> iter = iterator();
            while(iter.hasNext()){
                count++;
                iter.next();
            }
            size = Long.valueOf(count);
        }
        return size;
    }

    @Override
    public synchronized void close() throws IOException {
        closed = true;
        
    }

    @Override
    public synchronized StreamingIterator<Phd> iterator() {
        checkIfClosed();
        return LargePhdIterator.createNewIterator(phdFile);
    }

    
    private final class PhdIdIterator implements StreamingIterator<String>{

        private final StreamingIterator<Phd> phdIter;
        
        private PhdIdIterator(){
            phdIter = iterator();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            phdIter.remove();
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return phdIter.hasNext();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            phdIter.close();
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String next() {
            return phdIter.next().getId();
        }
       
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return closed;
    }
}
