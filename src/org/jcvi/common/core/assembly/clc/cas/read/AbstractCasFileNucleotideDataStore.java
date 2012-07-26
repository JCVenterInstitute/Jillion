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
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.clc.cas.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.assembly.clc.cas.AbstractOnePassCasFileVisitor;
import org.jcvi.common.core.assembly.clc.cas.CasFileInfo;
import org.jcvi.common.core.assembly.clc.cas.CasMatch;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MultipleDataStoreWrapper;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

public abstract class AbstractCasFileNucleotideDataStore extends AbstractOnePassCasFileVisitor implements CasNucleotideDataStore {

    private final List<NucleotideDataStore> nucleotideDataStores = new ArrayList<NucleotideDataStore>();
    
    private final CasDataStoreFactory casDataStoreFactory;
    private DataStore<NucleotideSequence> delegate;
   
    /**
     * @param casDataStoreFactory
     */
    public AbstractCasFileNucleotideDataStore(CasDataStoreFactory casDataStoreFactory) {
        this.casDataStoreFactory = casDataStoreFactory;
    }

   

    protected final void loadNucleotidesFrom(CasFileInfo readFileInfo) {
        for(String filePath: readFileInfo.getFileNames()){
            try {
                nucleotideDataStores.add(casDataStoreFactory.getNucleotideDataStoreFor(filePath));
            } catch (Exception e) {
               throw new IllegalStateException("could not load read file: "+ filePath,e);
            }
        }
    }
    
    @Override
    public synchronized void visitEndOfFile() {
        super.visitEndOfFile();
        delegate =  
                MultipleDataStoreWrapper.createMultipleDataStoreWrapper(DataStore.class, (List)nucleotideDataStores);
    }

    @Override
    public synchronized void close() throws IOException {
        for(NucleotideDataStore nucleotideDataStore: nucleotideDataStores){
            nucleotideDataStore.close();
        }
        delegate.close();
        nucleotideDataStores.clear();        
    }
    
    

    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }



    @Override
    protected void visitMatch(CasMatch match, long readCounter) {
        // TODO Auto-generated method stub
        
    }



    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        checkIsInitialized();
        return delegate.contains(id);
    }

    @Override
    public synchronized NucleotideSequence get(String id) throws DataStoreException {
        checkIsInitialized();
        return delegate.get(id);
    }

    @Override
    public synchronized StreamingIterator<String> idIterator() throws DataStoreException {
        checkIsInitialized();
        return delegate.idIterator();
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkIsInitialized();
        return delegate.getNumberOfRecords();
    }

    @Override
    public synchronized StreamingIterator<NucleotideSequence> iterator() throws DataStoreException {
        checkIsInitialized();
        return delegate.iterator();
    }

}
