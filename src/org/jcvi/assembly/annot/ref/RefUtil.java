/*
 * Created on Feb 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import org.jcvi.Range;

public final class RefUtil {

    private RefUtil(){}
    
    public static Range convertOnesBasedToSpacedBased(final Range rangeAsOnesbased) {
        Range spacedBasedRange = Range.buildRange(rangeAsOnesbased.getStart() -1,
                                        rangeAsOnesbased.getEnd());
        return spacedBasedRange;
    }
}
