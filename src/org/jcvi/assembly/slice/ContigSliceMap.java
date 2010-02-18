/*
 * Created on Apr 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;

public interface ContigSliceMap<T extends PlacedRead> extends Iterable<ContigSlice<T>>{

    Contig<T> getContig();
    ContigSlice<T> getContigSliceAt(int index);
    
    Iterable<ContigSlice<T>> getContigSlicesWithin(Range range);
    Iterable<ContigSlice<T>> getContigSlicesWhichIntersect(Range range);
}
