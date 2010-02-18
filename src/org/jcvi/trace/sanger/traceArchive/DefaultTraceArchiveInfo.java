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

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;

public class DefaultTraceArchiveInfo<T extends TraceArchiveRecord> implements TraceArchiveInfo{
    private final Map<String, T> map;
    
    public DefaultTraceArchiveInfo(TraceArchiveInfoBuilder<T> builder){
        this.map = Collections.unmodifiableMap(builder.getTraceArchiveRecordMap());
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return map.containsKey(id);
    }

    @Override
    public TraceArchiveRecord get(String id) throws DataStoreException {
        if(!contains(id)){
            throw new DataStoreException(id + " does not exist");
        }
        return map.get(id);
    }

    @Override
    public Iterator<String> getIds() {
        return map.keySet().iterator();
    }

    @Override
    public int size() throws DataStoreException {
        return map.size();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Iterator<TraceArchiveRecord> iterator() {
        return new DataStoreIterator<TraceArchiveRecord>(this);
    }

   
}
