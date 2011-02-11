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
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.io.IOUtil;
import org.jcvi.util.CloseableIterator;

public class LargeSffFileDataStore extends AbstractDataStore<SFFFlowgram> implements SffDataStore{

    private final File sffFile;
    private Integer size=null;
    
    /**
     * @param sffFile
     */
    public LargeSffFileDataStore(File sffFile) {
        this.sffFile = sffFile;
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return get(id)!=null;
    }

    @Override
    public synchronized SFFFlowgram get(String id) throws DataStoreException {
        super.get(id);
        SingleSffGetter datastore= new SingleSffGetter(id);
        InputStream in = null;
        try{
            in =new FileInputStream(sffFile);
            SffParser.parseSFF(in , datastore);
            return datastore.get(id);
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not read sffFile ",e);
        } catch (SFFDecoderException e) {
            throw new DataStoreException("could not parse sffFile ",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        super.getIds();
        SffIdIterator iter = new SffIdIterator();
        InputStream in = null;
        try{
            in =new FileInputStream(sffFile);
            SffParser.parseSFF(in , iter);
            return iter;
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not read sffFile ",e);
        } catch (SFFDecoderException e) {
            throw new DataStoreException("could not parse sffFile ",e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
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
    public CloseableIterator<SFFFlowgram> iterator() {
        super.iterator();
       // return new DataStoreIterator<SFFFlowgram>(this);
        try {
        	SffFileIterator iter= new SffFileIterator(sffFile);
        	iter.start();
        	return iter;
		} catch (InterruptedException e) {
			throw new IllegalStateException("could not create iterator",e);
		}
    }

    private static final class SffIdIterator implements SffFileVisitor, CloseableIterator<String>{
        private List<String> ids = new ArrayList<String>();
        private Iterator<String> iter=null;
        @Override
        public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
            return true;
        }

        @Override
        public boolean visitReadData(SFFReadData readData) {
            return true;
        }

        @Override
        public boolean visitReadHeader(SFFReadHeader readHeader) {
            ids.add(readHeader.getName());
            return true;
        }

        @Override
        public void visitEndOfFile() {
            iter = ids.iterator();            
        }

        @Override
        public void visitFile() {
            
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public String next() {
            return iter.next();
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
            ids.clear();
            
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
    private static final class SingleSffGetter extends DefaultSffFileDataStore{

        private final String idToFetch;
        private boolean foundRecord =false;
        /**
         * @param idToFetch
         */
        public SingleSffGetter(String idToFetch) {
            super();
            this.idToFetch = idToFetch;
            
        }
        @Override
        public boolean visitReadData(SFFReadData readData) {
            //if we get here this is the record we wanted
            super.visitReadData(readData);
            //return false to stop parsing.
            return false;
        }
        @Override
        public boolean visitReadHeader(SFFReadHeader readHeader) { 
            if(idToFetch.equals(readHeader.getName())){
                foundRecord=true;
                super.visitReadHeader(readHeader);
            }
            //skip read if this isn't the record we want.
            return foundRecord;
        }

       
        
    }
}
