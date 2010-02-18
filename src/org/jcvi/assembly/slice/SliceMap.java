/*
 * Created on Jun 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

public interface SliceMap extends Iterable<Slice>{

    Slice getSlice(long offset);
    long getSize();
}
