/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.archive2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DefaultTraceArchiveInfo implements TraceArchiveInfo{
	private final Map<String,TraceArchiveRecord> records;
	private final Map<TraceInfoField, String> commonFields;
	
	
	private DefaultTraceArchiveInfo(Map<String,TraceArchiveRecord> records,
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
		//defensive copy
		return new ArrayList<TraceArchiveRecord>(records.values());
	}

	@Override
	public TraceArchiveRecord get(String traceName) {
		return records.get(traceName);
	}

	public static class Builder implements org.jcvi.jillion.core.util.Builder<TraceArchiveInfo>{

		private final Map<String,TraceArchiveRecord> records = new LinkedHashMap<String,TraceArchiveRecord>();
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
			String traceName = r.getAttribute(TraceInfoField.TRACE_NAME);
			if(traceName ==null){
				throw new IllegalArgumentException("trace archive record must have a value for 'trace_name'");
			}
			records.put(traceName,r);
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
