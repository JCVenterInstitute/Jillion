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
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.core.util.iter.StreamingIteratorAdapter;

public class DefaultIndexedFileRange implements IndexedFileRange{

    private final Map<String, Range> ranges;
    private volatile  boolean closed=false;
    public DefaultIndexedFileRange(){
        //preserves insertion order
        ranges = new LinkedHashMap<String, Range>();
    }
    public DefaultIndexedFileRange(int initialSize){
        //preserves insertion order
    	int initialCapacity = MapUtil.computeMinHashMapSizeWithoutRehashing(initialSize);
        ranges = new LinkedHashMap<String, Range>(initialCapacity);
    }
    
    @Override
    public boolean contains(String id) {
        checkIfClosed();
        return ranges.containsKey(id);
    }

    @Override
    public Range getRangeFor(String id) {
        checkIfClosed();
        return ranges.get(id);
    }

    private void checkIfClosed(){
        if(closed){
            throw new IllegalStateException("closed");
        }
    }
    @Override
    public void put(String id, Range range) {
        checkIfClosed();
        ranges.put(id,range);
    }

    @Override
    public void close() {        
        closed = true;
    }
    

    @Override
    public boolean isClosed() {
        return closed;
    }
    @Override
    public StreamingIterator<String> getIds() {
        checkIfClosed();
        return new StreamingIteratorImpl();
    }

    @Override
    public int size() {
        checkIfClosed();
        return ranges.size();
    }

    
    private class StreamingIteratorImpl implements StreamingIterator<String>{

    	private final StreamingIterator<String> delegate;
    	
    	public StreamingIteratorImpl(){
    		this.delegate = StreamingIteratorAdapter.adapt(ranges.keySet().iterator());
    	}
		@Override
		public boolean hasNext() {
			boolean delegateHasNext = delegate.hasNext();
			if(DefaultIndexedFileRange.this.isClosed() && delegateHasNext){
				IOUtil.closeAndIgnoreErrors(this);
				throw new IllegalStateException("IndexedFileRange is closed");
			}
			return delegateHasNext;
		}

		@Override
		public void close() throws IOException {
			delegate.close();
			
		}

		@Override
		public String next() {
			String next = delegate.next();
			if(DefaultIndexedFileRange.this.isClosed()){
				IOUtil.closeAndIgnoreErrors(this);
				throw new IllegalStateException("IndexedFileRange is closed");
			}
			return next;
		}

		@Override
		public void remove() {
			delegate.remove();
			
		}
    	
    }
    

}
