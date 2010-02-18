/*
 * Created on Oct 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Iterator;

import org.jcvi.Range;
import org.jcvi.RangeIterator;

public class PlacedIterable implements Iterable<Long>{

    private final Placed placed;
    public PlacedIterable(Placed placed){
       this.placed = placed;
    }
    
    @Override
    public Iterator<Long> iterator() {
        return  new RangeIterator(Range.buildRange(placed.getStart(), placed.getEnd()));
    }

}
