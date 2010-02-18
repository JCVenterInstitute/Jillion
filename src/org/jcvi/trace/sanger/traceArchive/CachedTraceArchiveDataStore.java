/*
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.util.LRUCache;

public class CachedTraceArchiveDataStore implements TraceArchiveDataStore<TraceArchiveTrace>{

    private final Map<String, TraceArchiveTrace> lruCahce;
    private final TraceArchiveDataStore<TraceArchiveTrace> traceArchiveMultiTrace;
    
    public CachedTraceArchiveDataStore(TraceArchiveDataStore<TraceArchiveTrace> traceArchiveMultiTrace, int cacheSize) {
        this.traceArchiveMultiTrace = traceArchiveMultiTrace;
        lruCahce = new LRUCache<String, TraceArchiveTrace>(cacheSize);
    }

    @Override
    public TraceArchiveTrace get(String id) throws DataStoreException {
        if(lruCahce.containsKey(id)){
            return lruCahce.get(id);
        }
        TraceArchiveTrace trace = traceArchiveMultiTrace.get(id);
        lruCahce.put(id, trace);
        return trace;
    }

    @Override
    public void close() throws IOException {
        lruCahce.clear();
        traceArchiveMultiTrace.close();
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return traceArchiveMultiTrace.contains(id);
    }

    @Override
    public int size() throws DataStoreException {
        return traceArchiveMultiTrace.size();
    }

    @Override
    public Iterator<TraceArchiveTrace> iterator() {
        return traceArchiveMultiTrace.iterator();
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return traceArchiveMultiTrace.getIds();
    }

}
