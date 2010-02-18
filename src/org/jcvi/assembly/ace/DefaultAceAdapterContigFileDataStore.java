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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;

public class DefaultAceAdapterContigFileDataStore extends AbstractAceAdaptedContigFileDataStore implements DataStore<DefaultAceContig>{

    private final Map<String, DefaultAceContig> map = new HashMap<String, DefaultAceContig>();
    private DataStore<DefaultAceContig> dataStore;
    
    /**
     * @param phdDate
     */
    public DefaultAceAdapterContigFileDataStore(Date phdDate) {
        super(phdDate);
    }

    @Override
    protected void visitAceContig(DefaultAceContig aceContig) {
        map.put(aceContig.getId(), aceContig);        
    }

    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        dataStore = new SimpleDataStore<DefaultAceContig>(map);
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    @Override
    public DefaultAceContig get(String id) throws DataStoreException {
        return dataStore.get(id);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return dataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return dataStore.size();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();
        
    }

    @Override
    public Iterator<DefaultAceContig> iterator() {
        return dataStore.iterator();
    }

}
