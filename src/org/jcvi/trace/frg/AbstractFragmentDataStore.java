/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.Distance;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.sequence.DefaultLibrary;
import org.jcvi.sequence.Library;
import org.jcvi.sequence.MateOrientation;

public abstract class AbstractFragmentDataStore implements Frg2Visitor, FragmentDataStore{

    private boolean initialized;
    private boolean closed;
    
    
    private final Map<String,Library> libraries = new HashMap<String, Library>();
    
    
    protected boolean isInitialized() {
        return initialized;
    }
    protected boolean isClosed() {
        return closed;
    }

    
    protected void throwErrorIfClosed() {
        if(isClosed()){
            throw new IllegalStateException("datastore is closed");
        }
        
    }
    

    protected boolean isDelete(FrgVisitorAction action) {
        return action == FrgVisitorAction.DELETE;
    }

    protected boolean isAddOrModify(FrgVisitorAction action) {
        return action == FrgVisitorAction.ADD || action == FrgVisitorAction.MODIFY;
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
    public Iterator<Fragment> iterator() {
        throwErrorIfClosed();
        return new DataStoreIterator<Fragment>(this);
    }
    
    @Override
    public void visitLibrary(FrgVisitorAction action, String id,
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
