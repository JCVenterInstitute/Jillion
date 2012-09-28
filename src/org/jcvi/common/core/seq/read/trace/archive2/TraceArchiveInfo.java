package org.jcvi.common.core.seq.read.trace.archive2;

import java.util.List;
import java.util.Map;


public interface TraceArchiveInfo {
	
	Map<TraceInfoField, String> getCommonFields();
	
	List<TraceArchiveRecord> getRecordList();
	
	TraceArchiveRecord get(String traceName);
}
