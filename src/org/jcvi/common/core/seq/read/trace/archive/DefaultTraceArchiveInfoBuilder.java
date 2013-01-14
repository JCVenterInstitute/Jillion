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
 * Created on Jun 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.archive;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreUtil;

public class DefaultTraceArchiveInfoBuilder implements TraceArchiveInfoBuilder<TraceArchiveRecord>{
    private final Map<String, TraceArchiveRecord> map = new LinkedHashMap<String, TraceArchiveRecord>();
    
    @Override
    public DefaultTraceArchiveInfoBuilder put(String key, TraceArchiveRecord record){
        map.put(key, record);
        return this;
    }
    @Override
    public DefaultTraceArchiveInfoBuilder putAll(Map<String, TraceArchiveRecord> map){
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
    public Map<String, TraceArchiveRecord> getTraceArchiveRecordMap() {
        return new LinkedHashMap<String,TraceArchiveRecord>(map);
    }
	@Override
	public TraceArchiveInfo build() {
		return DataStoreUtil.adapt(TraceArchiveInfo.class,map);
	}


}
