/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleDataStore<T> extends AbstractDataStore<T> {

    private final Map<String, T> map = new HashMap<String, T>();
    public SimpleDataStore(Map<String, T> map){
        this.map.putAll(map);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return map.containsKey(id);
    }
    @Override
    public T get(String id) throws DataStoreException {
        super.get(id);
        return map.get(id);
    }
    @Override
    public Iterator<String> getIds() throws DataStoreException {
        super.getIds();
        return map.keySet().iterator();
    }
    @Override
    public int size() throws DataStoreException {
        super.size();
        return map.size();
    }
    @Override
    public void close() throws IOException {
       super.close();
       map.clear();
        
    }

    
    
}
