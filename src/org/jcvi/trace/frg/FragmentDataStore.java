/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;


import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.sequence.Library;

public interface FragmentDataStore extends DataStore<Fragment>{

    boolean containsLibrary(String libraryId) throws DataStoreException;
    Library getLibrary(String libraryId) throws DataStoreException;
    
    Fragment getMateOf(Fragment fragment) throws DataStoreException;
    boolean hasMate(Fragment fragment) throws DataStoreException;
    
}
