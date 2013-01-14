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
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.frg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.internal.datastore.DataStoreIterator;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

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
            throw new IllegalStateException("datastore is closed");
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
