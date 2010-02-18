/*
 * Created on Oct 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;

public interface TrimmedPlacedRead<T extends PlacedRead> {

    T getRead();
    Range getNewTrimRange();
}
