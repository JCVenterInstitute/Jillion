/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.AbstractContigFileDataStore;
import org.jcvi.assembly.contig.DefaultContigFileParser;

public class DefaultContigFileDataStore extends AbstractContigFileDataStore implements ContigDataStore<PlacedRead, Contig<PlacedRead>>{
    private final Map<String,Contig<PlacedRead>> contigs;

    private boolean isClosed = false;
    
    
    private DefaultContigFileDataStore(){
        contigs = new HashMap<String, Contig<PlacedRead>>();
    }

    
    public DefaultContigFileDataStore(File file) throws FileNotFoundException{
        this(new FileInputStream(file));
    }
    
    public DefaultContigFileDataStore(InputStream inputStream) {
        this();
        DefaultContigFileParser.parseInputStream(inputStream, this);
    }
    @Override
    protected void addContig(Contig contig) {
        contigs.put(contig.getId(), contig);
        
    }

    @Override
    public boolean contains(String contigId)throws DataStoreException {
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
        return contigs.containsKey(contigId);
    }
    
    private void throwExceptionIfNotInitialized() throws DataStoreException {
        if(!isInitialized()){
            throw new DataStoreException("DataStore not yet initialized");
        }
    }
    
    
    private void throwExceptionIfClosed() throws DataStoreException {
        if(isClosed){
            throw new DataStoreException("DataStore is closed");
        }
    }

    
    @Override
    public Contig<PlacedRead> get(String contigId)
            throws DataStoreException {
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
        return contigs.get(contigId);
    }

    @Override
    public void close() throws IOException {        
        isClosed = true;
        contigs.clear();
    }


    @Override
    public Iterator<String> getIds() {
        final List<String> sortedIds = new ArrayList<String>(contigs.keySet());
        Collections.sort(sortedIds);
        return sortedIds.iterator();
    }


    @Override
    public int size() {
        return contigs.size();
    }


    @Override
    public Iterator<Contig<PlacedRead>> iterator() {
        return new DataStoreIterator<Contig<PlacedRead>>(this);
    }
}
