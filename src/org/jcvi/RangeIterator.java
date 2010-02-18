/*
 * Created on Apr 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi;

import java.util.Iterator;

public class RangeIterator implements Iterator<Long>{
    private final long from;
    private final long to;
    private long index;
    
    public RangeIterator(Range range){
        from = range.getStart();
        to = range.getEnd();
        index = from;
    }
    @Override
    public boolean hasNext() {
        return index<=to;
    }

    @Override
    public Long next() {
        return index++;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("can not remove from Range");
        
    }
    
}
