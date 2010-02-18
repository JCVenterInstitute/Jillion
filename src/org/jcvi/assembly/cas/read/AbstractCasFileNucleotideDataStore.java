/*
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.assembly.cas.AbstractOnePassCasFileVisitor;
import org.jcvi.assembly.cas.CasFileInfo;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public abstract class AbstractCasFileNucleotideDataStore extends AbstractOnePassCasFileVisitor implements CasNucleotideDataStore {

    private List<DataStore<NucleotideEncodedGlyphs>> nucleotideDataStores = new ArrayList<DataStore<NucleotideEncodedGlyphs>>();
    
    private final CasDataStoreFactory casDataStoreFactory;
    private DataStore<NucleotideEncodedGlyphs> delegate;
   
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
        delegate.close();
        nucleotideDataStores.clear();
        
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        checkIsInitialized();
        return delegate.contains(id);
    }

    @Override
    public synchronized NucleotideEncodedGlyphs get(String id) throws DataStoreException {
        checkIsInitialized();
        return delegate.get(id);
    }

    @Override
    public synchronized Iterator<String> getIds() throws DataStoreException {
        checkIsInitialized();
        return delegate.getIds();
    }

    @Override
    public synchronized int size() throws DataStoreException {
        checkIsInitialized();
        return delegate.size();
    }

    @Override
    public synchronized Iterator<NucleotideEncodedGlyphs> iterator() {
        checkIsInitialized();
        return delegate.iterator();
    }

}
