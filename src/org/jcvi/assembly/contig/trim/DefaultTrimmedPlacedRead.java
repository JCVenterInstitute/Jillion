/*
 * Created on Oct 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;

public class DefaultTrimmedPlacedRead<T extends PlacedRead> implements TrimmedPlacedRead<T> {

    private final Range newTrimRange;
    private final T read;
    
    
    /**
     * @param read
     * @param newTrimRange
     */
    public DefaultTrimmedPlacedRead(T read, Range newTrimRange) {
        this.read = read;
        this.newTrimRange = newTrimRange;
    }

    @Override
    public Range getNewTrimRange() {
        return newTrimRange;
    }

    @Override
    public T getRead() {
        return read;
    }

    @Override
    public String toString() {
        return "DefaultTrimmedPlacedRead [read=" + read + ", newTrimRange="
                + newTrimRange + "]";
    }
    
    

}
