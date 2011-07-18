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
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.datastore.EmptyDataStoreFilter;
import org.jcvi.util.CloseableIterator;
import org.jcvi.util.CloseableIteratorAdapter;

public class DefaultSffFileDataStore implements SffDataStore, SffFileVisitor{

    private final Map<String, SFFFlowgram> map = new HashMap<String, SFFFlowgram>();
    private boolean initialized=false;
    private boolean closed = false;

    private final DataStoreFilter filter;
    
    private SFFReadHeader currentReadHeader;
    /**
     * @param phredQualityGlyphCodec
     */
    public DefaultSffFileDataStore() {
        this(EmptyDataStoreFilter.INSTANCE);
    }
    public DefaultSffFileDataStore(DataStoreFilter filter) {
        if(filter ==null){
            throw new NullPointerException("filter can not be null");
        }
        this.filter = filter;
    }
    public DefaultSffFileDataStore(File sffFile) throws SFFDecoderException, FileNotFoundException {
        this(sffFile, EmptyDataStoreFilter.INSTANCE);
    }
    public DefaultSffFileDataStore(File sffFile,
            DataStoreFilter filter) throws SFFDecoderException, FileNotFoundException {
        this(filter);
        SffParser.parseSFF(sffFile, this);
    }
    private void throwExceptionIfNotInitialized(){
        if(!initialized){
            throw new IllegalStateException("Not initialized");
        }
    }
    private void throwExceptionIfInitialized(){
        if(initialized){
            throw new IllegalStateException("Not initialized");
        }
    }
    private void throwExceptionIfClosed(){
        if(closed){
            throw new IllegalStateException("is closed");
        }
    }
    private void throwExceptionIfNotInitializedOrClosed(){
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();
        return map.containsKey(id);
    }

    @Override
    public SFFFlowgram get(String id) throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();       
        return map.get(id);
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();
        return CloseableIteratorAdapter.adapt(map.keySet().iterator());
    }

    @Override
    public int size() throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();
        return map.size();
    }

    @Override
    public void close() throws IOException {        
        closed = true;
        map.clear();
    }

    @Override
    public CloseableIterator<SFFFlowgram> iterator() {
        throwExceptionIfNotInitializedOrClosed();
        return new DataStoreIterator<SFFFlowgram>(this);
    }

    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        throwExceptionIfInitialized();
        return true;
    }

    @Override
    public boolean visitReadData(SFFReadData readData) {
       throwExceptionIfInitialized();
       map.put(currentReadHeader.getName(), SFFUtil.buildSFFFlowgramFrom(currentReadHeader, readData));
       currentReadHeader=null;
       return true;
    }

    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        throwExceptionIfInitialized();
        currentReadHeader = readHeader;
        String id = readHeader.getName();
        return filter.accept(id);
    }

    @Override
    public void visitEndOfFile() {
       throwExceptionIfInitialized();
       initialized=true;        
    }

    @Override
    public void visitFile() {
        throwExceptionIfInitialized();
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return closed;
    }
}
