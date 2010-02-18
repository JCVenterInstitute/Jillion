/*
 * Created on May 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.assembly.ace.AbstractAceFileDataStore;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AcePlacedRead;

public class DefaultAceFileDataStore extends AbstractAceFileDataStore implements ContigDataStore<AcePlacedRead, AceContig>{

    private Map<String, AceContig> contigMap = new LinkedHashMap<String, AceContig>();

    private boolean isClosed;
   
    private void throwExceptionIfClosed() throws DataStoreException {
        if(isClosed){
            throw new DataStoreException("DataStore is closed");
        }
    }
    @Override
    protected void visitContig(AceContig contig) {
       contigMap.put(contig.getId(), contig);
        
    }
    @Override
    public boolean contains(String contigId) throws DataStoreException {
        throwExceptionIfClosed();
        return contigMap.containsKey(contigId);
    }
    @Override
    public AceContig get(String contigId) throws DataStoreException {
        throwExceptionIfClosed();
        return contigMap.get(contigId);
    }
    @Override
    public Iterator<String> getIds() {       
        return contigMap.keySet().iterator();
    }
    @Override
    public int size() {
        return contigMap.size();
    }
    @Override
    public void close() throws IOException {
        isClosed = true;
        contigMap.clear();
    }
    @Override
    public Iterator<AceContig> iterator() {
        return contigMap.values().iterator();
    }

}
