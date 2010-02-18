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
