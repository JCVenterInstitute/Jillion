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
