/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultTraceArchiveInfoBuilder<T extends TraceArchiveRecord> implements TraceArchiveInfoBuilder<T>{
    private final Map<String, T> map = new LinkedHashMap<String, T>();
    
    @Override
    public DefaultTraceArchiveInfoBuilder put(String key, T record){
        map.put(key, record);
        return this;
    }
    @Override
    public DefaultTraceArchiveInfoBuilder putAll(Map<String, T> map){
        this.map.putAll(map);
        return this;
    }
    
    @Override
    public TraceArchiveInfoBuilder remove(String key) {
        map.remove(key);
        return this;
    }
    @Override
    public TraceArchiveInfoBuilder removeAll(Collection<String> keys) {
        for(String key : keys){
            remove(key);
        }
        return this;
    }
    @Override
    public Map<String, T> getTraceArchiveRecordMap() {
        return new HashMap<String,T>(map);
    }


}
