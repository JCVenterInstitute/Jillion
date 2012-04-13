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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.datastore;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;

public class SimpleDataStore<T> extends AbstractDataStore<T> {

    private final Map<String, T> map = new LinkedHashMap<String, T>();
    public SimpleDataStore(Map<String, T> map){
        this.map.putAll(map);
    }
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return map.containsKey(id);
    }
    @Override
    public synchronized T get(String id) throws DataStoreException {
        super.get(id);
        return map.get(id);
    }
    @Override
    public synchronized CloseableIterator<String> idIterator() throws DataStoreException {
        super.idIterator();
        return CloseableIteratorAdapter.adapt(map.keySet().iterator());
    }
    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        super.getNumberOfRecords();
        return map.size();
    }
	@Override
	protected void handleClose() throws IOException {
		 map.clear();
		
	}

    
    
}
