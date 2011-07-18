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
 * Created on May 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.assembly.ace.AbstractAceContigBuilder;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigDataStore;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.CloseableIterator;
import org.jcvi.common.core.util.CloseableIteratorAdapter;

public class DefaultAceFileDataStore extends AbstractAceContigBuilder implements AceContigDataStore{

    private Map<String, AceContig> contigMap = new LinkedHashMap<String, AceContig>();

    private boolean isClosed;
   
    private void throwExceptionIfClosed() throws DataStoreException {
        if(isClosed){
            throw new DataStoreException("DataStore is closed");
        }
    }
    
    public DefaultAceFileDataStore(){
        super();
    }
    
    public DefaultAceFileDataStore(File aceFile) throws IOException{
        super();
        AceFileParser.parseAceFile(aceFile, this);
    }
    @Override
    protected void visitContig(AceContig contig) {
       contigMap.put(contig.getId(), contig);
        
    }
    @Override
    public boolean contains(String contigId) throws DataStoreException {
        throwExceptionIfClosed();
        return contigMap.containsKey(contigId);
    }
    @Override
    public AceContig get(String contigId) throws DataStoreException {
        throwExceptionIfClosed();
        return contigMap.get(contigId);
    }
    @Override
    public CloseableIterator<String> getIds() {       
        return CloseableIteratorAdapter.adapt(contigMap.keySet().iterator());
    }
    @Override
    public int size() {
        return contigMap.size();
    }
    @Override
    public void close() throws IOException {
        isClosed = true;
        contigMap.clear();
    }
    @Override
    public CloseableIterator<AceContig> iterator() {
        return CloseableIteratorAdapter.adapt(contigMap.values().iterator());
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return isClosed;
    }

}
