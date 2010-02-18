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
