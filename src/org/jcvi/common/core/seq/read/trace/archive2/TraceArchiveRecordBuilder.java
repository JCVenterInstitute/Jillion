package org.jcvi.common.core.seq.read.trace.archive2;

import java.util.Map;

import org.jcvi.common.core.util.Builder;


public interface TraceArchiveRecordBuilder extends Builder<TraceArchiveRecord>{

	/**
	 * Puts an attribute with the given key and value.  If 
	 * an attribute already exists with the given
	 * key, it will be overwritten with the new value.
	 * @param key the key to add.
	 * @param value the value associated with the given key.
	 * @return {@code this}
	 */
	TraceArchiveRecordBuilder put(TraceInfoField traceInfoField, String value);

	TraceArchiveRecordBuilder putExtendedData(String key, String value);

	TraceArchiveRecordBuilder putAll(Map<TraceInfoField, String> map);
	@Override
	TraceArchiveRecord build();

}