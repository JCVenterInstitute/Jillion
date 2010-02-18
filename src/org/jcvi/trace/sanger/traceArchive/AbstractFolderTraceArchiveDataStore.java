/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.util.Iterator;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.trace.TraceDecoderException;

public abstract class AbstractFolderTraceArchiveDataStore implements TraceArchiveDataStore<TraceArchiveTrace> {

    private final String rootDirPath;
    private final TraceArchiveInfo traceArchiveInfo;
    
    
    public String getRootDirPath() {
        return rootDirPath;
    }

    public TraceArchiveInfo getTraceArchiveInfo() {
        return traceArchiveInfo;
    }

    /**
     * @param rootDirPath
     * @param traceArchiveInfo
     */
    public AbstractFolderTraceArchiveDataStore(String rootDirPath,
            TraceArchiveInfo traceArchiveInfo) {
        if(rootDirPath ==null || traceArchiveInfo ==null){
            throw new NullPointerException("arguments must not be null");
        }
        this.rootDirPath = rootDirPath;
        this.traceArchiveInfo = traceArchiveInfo;
    }

    @Override
    public boolean contains(String id) throws DataStoreException{       
            return traceArchiveInfo.contains(id);        
    }

    

    @Override
    public int size() throws DataStoreException{
        return traceArchiveInfo.size();
       
    }


    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return traceArchiveInfo.getIds();
    }
    
    

    @Override
    public Iterator<TraceArchiveTrace> iterator() {
        return new DataStoreIterator<TraceArchiveTrace>(this);
    }

    protected abstract TraceArchiveTrace createTraceArchiveTrace(String id)
                                                throws DataStoreException;
}
