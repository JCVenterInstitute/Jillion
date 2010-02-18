/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;

public class DefaultTraceArchiveInfo<T extends TraceArchiveRecord> implements TraceArchiveInfo{
    private final Map<String, T> map;
    
    public DefaultTraceArchiveInfo(TraceArchiveInfoBuilder<T> builder){
        this.map = Collections.unmodifiableMap(builder.getTraceArchiveRecordMap());
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return map.containsKey(id);
    }

    @Override
    public TraceArchiveRecord get(String id) throws DataStoreException {
        if(!contains(id)){
            throw new DataStoreException(id + " does not exist");
        }
        return map.get(id);
    }

    @Override
    public Iterator<String> getIds() {
        return map.keySet().iterator();
    }

    @Override
    public int size() throws DataStoreException {
        return map.size();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Iterator<TraceArchiveRecord> iterator() {
        return new DataStoreIterator<TraceArchiveRecord>(this);
    }

   
}
