package org.jcvi.common.core.seq.read.trace.archive2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DefaultTraceArchiveInfo implements TraceArchiveInfo{
	private final List<TraceArchiveRecord> records;
	private final Map<TraceInfoField, String> commonFields;
	
	
	private DefaultTraceArchiveInfo(List<TraceArchiveRecord> records,
			Map<TraceInfoField, String> commonFields) {
		this.records = records;
		this.commonFields = commonFields;
	}

	@Override
	public Map<TraceInfoField, String> getCommonFields() {
		return Collections.unmodifiableMap(commonFields);
	}

	@Override
	public List<TraceArchiveRecord> getRecordList() {
		return Collections.unmodifiableList(records);
	}

	public static class Builder implements org.jcvi.common.core.util.Builder<TraceArchiveInfo>{

		private final List<TraceArchiveRecord> records = new ArrayList<TraceArchiveRecord>();
		private final Map<TraceInfoField, String> commonFields = new LinkedHashMap<TraceInfoField, String>();
		
		public Builder addAllRecords(Collection<? extends TraceArchiveRecord> records){
			for(TraceArchiveRecord r : records){
				addRecord(r);
			}
			return this;
		}
		public Builder addRecord(TraceArchiveRecord r){
			if(r ==null){
				throw new NullPointerException("TraceArchiveRecord can not be null");
			}
			records.add(r);
			return this;
		}
		/**
		 * Add the given common field with the given value.
		 * @param key the TraceInfoField to add to all records; can not be null.
		 * @param value the value to assign to this common field; can not be null.
		 * @return this.
		 * @throws NullPointerException if either parameter is null.
		 */
		public Builder addCommonField(TraceInfoField key, String value){
			if(key==null){
				throw new NullPointerException("common field key can not be null");
			}
			if(value==null){
				throw new NullPointerException("common field value can not be null");
			}
			commonFields.put(key, value);
			return this;
		}
		@Override
		public TraceArchiveInfo build() {
			return new DefaultTraceArchiveInfo(records, commonFields);
		}
		
	}
}
