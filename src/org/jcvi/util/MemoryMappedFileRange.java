/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.Iterator;

import org.jcvi.Range;

public interface MemoryMappedFileRange {

    Range getRangeFor(String id);
    boolean contains(String id);
    void put(String id, Range range);
    void remove(String id);
    
    void close();
    int size();
    Iterator<String> getIds();
}
