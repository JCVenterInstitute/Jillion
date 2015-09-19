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
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

abstract class AbstractFragmentDataStore implements Frg2Visitor, FragmentDataStore{

    private boolean initialized;
    private boolean closed;
    
    
    private final Map<String,Library> libraries = new HashMap<String, Library>();
    
    
    protected boolean isInitialized() {
        return initialized;
    }
    @Override
    public boolean isClosed() {
        return closed;
    }

    
    protected void throwErrorIfClosed() {
        if(isClosed()){
            throw new DataStoreClosedException("datastore is closed");
        }
        
    }
    

    protected boolean isDelete(FrgAction action) {
        return action == FrgAction.DELETE;
    }

    protected boolean isAddOrModify(FrgAction action) {
        return action == FrgAction.ADD || action == FrgAction.MODIFY;
    }

    protected void throwErrorIfAlreadyInitialized() {
        if(isInitialized()){
            throw new IllegalStateException("can not add Fragments after initialization");
        }
    }
    
    @Override
    public void close() throws IOException {
        closed= true;
        
    }
    

    @Override
    public void visitEndOfFile() {
        initialized = true;
        
    }
    
    @Override
    public void visitFile() {
        throwErrorIfClosed();
        
    }
    @Override
    public StreamingIterator<Fragment> iterator() {
        throwErrorIfClosed();
        return new DataStoreIterator<Fragment>(this);
    }
    
    
    
    @Override
	public StreamingIterator<DataStoreEntry<Fragment>> entryIterator()
			throws DataStoreException {
    	 throwErrorIfClosed();
    	 StreamingIterator<DataStoreEntry<Fragment>> iter = new StreamingIterator<DataStoreEntry<Fragment>>(){
    		 StreamingIterator<Fragment> delegate = iterator();
			@Override
			public boolean hasNext() {
				return delegate.hasNext();
			}

			@Override
			public void close() {
				delegate.close();
			}

			@Override
			public DataStoreEntry<Fragment> next() {
				Fragment frag = delegate.next();
				return new DataStoreEntry<Fragment>(frag.getId(), frag);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
    		 
    	 };
		return DataStoreStreamingIterator.create(this, iter);
	}
	@Override
    public void visitLibrary(FrgAction action, String id,
            MateOrientation orientation, Distance distance) {
        throwErrorIfAlreadyInitialized();
        if(isAddOrModify(action)){
            Library library = new DefaultLibrary(id, distance, orientation);
            libraries.put(id, library);
        }
        else if(isDelete(action)){
            libraries.remove(id);
        }
    }
    
    
    @Override
    public boolean containsLibrary(String libraryId) throws DataStoreException{
        throwErrorIfClosed();
        return libraries.containsKey(libraryId);
    }

    @Override
    public Library getLibrary(String libraryId) throws DataStoreException{
        throwErrorIfClosed();
        return libraries.get(libraryId);
    }
    
    
}
