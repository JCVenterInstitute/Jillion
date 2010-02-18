/*
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.util.Collection;
import java.util.Map;

public interface TraceArchiveInfoBuilder<T extends TraceArchiveRecord>{

    TraceArchiveInfoBuilder put(String id, T record);
    
    TraceArchiveInfoBuilder putAll(Map<String, T> map);
    
    TraceArchiveInfoBuilder remove(String id);
    
    TraceArchiveInfoBuilder removeAll(Collection<String> id);
    
    Map<String, T> getTraceArchiveRecordMap();
    
}
