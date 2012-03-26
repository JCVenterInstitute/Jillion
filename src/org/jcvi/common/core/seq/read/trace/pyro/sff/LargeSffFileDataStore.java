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
 * Created on Jan 28, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code LargeSffFileDataStore} is a {@link SffDataStore}
 * implementation that doesn't store any read information in memory.
 *  No data contained in this
 * sff file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the sff file
 * which can take some time.  It is recommended that instances of 
 * {@link LargeSffFileDataStore} are wrapped by {@link CachedDataStore}
 * @author dkatzel
 *
 */
public final class LargeSffFileDataStore extends AbstractDataStore<Flowgram> implements SffDataStore{

    private final File sffFile;
    private Integer size=null;
    
    /**
     * Create a new instance of {@link LargeSffFileDataStore}.
     * @param sffFile the sff file to parse.
     * @return a new SffDataStore; never null.
     * @throws NullPointerException if sffFile is null.
     * @throws FileNotFoundException if sffFile does not exist.
     */
    public static SffDataStore create(File sffFile) throws FileNotFoundException{
    	if(sffFile ==null){
    		throw new NullPointerException("file can not be null");
    	}
    	if(!sffFile.exists()){
    		throw new FileNotFoundException("sff file does not exist");
    	}
    	return new LargeSffFileDataStore(sffFile);
    }
    /**
     * @param sffFile
     */
    private LargeSffFileDataStore(File sffFile) {
        this.sffFile = sffFile;
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return get(id)!=null;
    }

    @Override
    public synchronized Flowgram get(String id) throws DataStoreException {
        super.get(id);        
        try{
        	SffDataStore datastore= DefaultSffFileDataStore.createDataStoreOfSingleRead(sffFile,id);
            return datastore.get(id);
        } catch (IOException e) {
            throw new DataStoreException("could not read sffFile ",e);
        }
       
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        super.getIds();
        return new SffIdIterator(SffFileIterator.createNewIteratorFor(sffFile));
        
    }

    @Override
    public synchronized int size() throws DataStoreException {
        super.size();
        if(this.size ==null){
            this.size = 0;
            SffSize sffSize = new SffSize();
            InputStream in = null;
            try{
                in =new FileInputStream(sffFile);
                SffParser.parseSFF(in , sffSize);
               this.size = sffSize.getSize();
            } catch (FileNotFoundException e) {
                throw new DataStoreException("could not read sffFile ",e);
            } catch (SFFDecoderException e) {
                throw new DataStoreException("could not parse sffFile ",e);
            }
            finally{
                IOUtil.closeAndIgnoreErrors(in);
            }
        }
        return size;
    }

   

    @Override
	protected void handleClose() throws IOException {
		//no-op
		
	}
	@Override
    public synchronized CloseableIterator<Flowgram> iterator() {
        super.iterator();
      return SffFileIterator.createNewIteratorFor(sffFile);
    }
    /**
     * {@code SffIdIterator} is a {@link CloseableIterator}
     * that wraps the Iterator of Flowgrams and returns just
     * the id of each record when {@link Iterator#next()}
     * is called.
     * @author dkatzel
     *
     */
    private static final class SffIdIterator implements CloseableIterator<String>{
        
        private final CloseableIterator<Flowgram> iter;
       
        SffIdIterator(CloseableIterator<Flowgram> iter){
        	this.iter= iter;
        }
        
        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public String next() {
            return iter.next().getId();
        }

        @Override
        public void remove() {
            iter.remove();            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            iter.close();
            
        }
        
    }
    private static final class SffSize extends AbstractSffFileVisitor{
        private int size=0;

        @Override
        public boolean visitReadData(SFFReadData readData) {
            size++;
            return true;
        }

        public int getSize() {
            return size;
        }
        
        
    }
    
}
