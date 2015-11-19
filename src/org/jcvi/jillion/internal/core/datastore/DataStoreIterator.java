/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.core.datastore;

import java.util.Iterator;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code DataStoreIterator}
 * is a simple {@link StreamingIterator}
 * implementations meant for use by DataStores
 * to iterate over its contents using the same
 * order as {@link DataStore#idIterator()}.
 * This class uses the id iterator to get the next
 * id and then calls {@link DataStore#get(String)}
 * with that id; it is not an efficient algorithm
 * but is acceptable if a DataStore implementation
 * does not have a better way to create the iterator.
 * @author dkatzel
 *
 * @param <T> the Type of element returned by each call to {@link Iterator#next()}
 */
public final class DataStoreIterator<T> implements StreamingIterator<T>{
    private StreamingIterator<String> ids; 
    private final DataStore<T> dataStore;
    public DataStoreIterator(DataStore<T> dataStore){
        this.dataStore =  dataStore;
        try {
            ids = dataStore.idIterator();
        } catch (DataStoreException e) {
        	IOUtil.closeAndIgnoreErrors(ids);
            throw new IllegalStateException("could not iterate over ids", e);
        }
    }
    @Override
    public boolean hasNext() {
        return ids.hasNext();
    }

    @Override
    public T next() {
        try {
            return dataStore.get(ids.next());
        } catch (DataStoreException e) {
        	IOUtil.closeAndIgnoreErrors(ids);
            throw new IllegalStateException("could not get next element", e);
        }
    }

    @Override
    public void remove() {
       throw new UnsupportedOperationException("can not remove");
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void close(){
        ids.close();
        
    }
}
