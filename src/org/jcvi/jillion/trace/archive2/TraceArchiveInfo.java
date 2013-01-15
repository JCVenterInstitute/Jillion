package org.jcvi.jillion.trace.archive2;

import java.util.List;
import java.util.Map;


public interface TraceArchiveInfo {
	
	Map<TraceInfoField, String> getCommonFields();
	
	List<TraceArchiveRecord> getRecordList();
	
	TraceArchiveRecord get(String traceName);
}
