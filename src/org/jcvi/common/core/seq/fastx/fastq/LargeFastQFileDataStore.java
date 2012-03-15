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
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code LargeFastQFileDataStore} is a {@link FastQDataStore} implementation
 * to be used a very large FastQ Files.  No data contained in this
 * fastq file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances of 
 * {@link LargeFastQFileDataStore} are wrapped by {@link CachedDataStore}
 * @author dkatzel
 *
 *
 */
public final class LargeFastQFileDataStore implements FastQDataStore<FastQRecord> {
    private final FastQQualityCodec qualityCodec;
    private final File fastQFile;
    private Integer size=null;
    private volatile boolean closed;
    
    /**
     * @param qualityCodec
     */
    public LargeFastQFileDataStore(File fastQFile, FastQQualityCodec qualityCodec) {
        this.qualityCodec = qualityCodec;
        this.fastQFile = fastQFile;        
    }

    @Override
    public synchronized void close() throws IOException {
        closed = true;        
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public synchronized boolean isClosed() throws DataStoreException {
         return closed;
     }
    
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        throwExceptionIfClosed();
        CloseableIterator<FastQRecord> iter = iterator();
        while(iter.hasNext()){
            FastQRecord fastQ = iter.next();
            if(fastQ.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return true;
            }
        }
        return false;
    }
    private void throwExceptionIfClosed(){
        if(closed){
            throw new IllegalStateException("datastore is closed");
        }
    }
    @Override
    public synchronized FastQRecord get(String id) throws DataStoreException {
        if(closed){
            throw new DataStoreException("datastore is closed");
        }
        CloseableIterator<FastQRecord> iter = iterator();
        while(iter.hasNext()){
            FastQRecord fastQ = iter.next();
            if(fastQ.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return fastQ;
            }
        }
        throw new DataStoreException("could not find fastq record for "+id);
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        throwExceptionIfClosed();
        return new FastQIdIterator();        
    }

    @Override
    public synchronized int size() throws DataStoreException {
        throwExceptionIfClosed();
        if(size ==null){
            int count=0;
            CloseableIterator<FastQRecord> iter = iterator();
            while(iter.hasNext()){
                count++;
                iter.next();
            }
            size = Integer.valueOf(count);
        }
        return size;
    }
    
    @Override
    public synchronized CloseableIterator<FastQRecord> iterator() {
        throwExceptionIfClosed();
        return LargeFastQFileIterator.createNewIteratorFor(fastQFile,qualityCodec);
  
    }
    
    
    private final class FastQIdIterator implements CloseableIterator<String>{
        private final CloseableIterator<FastQRecord> iter;
        private FastQIdIterator(){
                iter = iterator();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String next() {
            return iter.next().getId();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
        	throw new UnsupportedOperationException();	           
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            iter.close();            
        }
        
    }
    

   
}
