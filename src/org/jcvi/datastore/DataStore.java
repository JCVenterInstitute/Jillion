/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.Closeable;
import java.util.Iterator;

public interface DataStore<T> extends Closeable,Iterable<T>{
    Iterator<String> getIds() throws DataStoreException;
    T get(String id) throws DataStoreException;
    
    boolean contains(String id) throws DataStoreException;
    
    int size() throws DataStoreException;
    
    
}
