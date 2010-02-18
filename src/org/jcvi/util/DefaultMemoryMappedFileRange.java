/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.Range;

public class DefaultMemoryMappedFileRange implements MemoryMappedFileRange{

    Map<String, Range> ranges;
    
    public DefaultMemoryMappedFileRange(){
        ranges = new HashMap<String, Range>();
    }
    
    @Override
    public boolean contains(String id) {
        return ranges.containsKey(id);
    }

    @Override
    public Range getRangeFor(String id) {
        return ranges.get(id);
    }

    @Override
    public void put(String id, Range range) {
        ranges.put(id,range);
    }

    @Override
    public void close() {
        ranges.clear();
    }

    @Override
    public Iterator<String> getIds() {
        return ranges.keySet().iterator();
    }

    @Override
    public int size() {
        return ranges.size();
    }

    @Override
    public void remove(String id) {
        ranges.remove(id);
        
    }
    
    

}
