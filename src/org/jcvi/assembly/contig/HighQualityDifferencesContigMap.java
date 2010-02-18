/*
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface HighQualityDifferencesContigMap extends Iterable<List<DefaultQualityDifference>>{

    PhredQuality getQualityThreshold();
    /**
     * Get the list of {@link DefaultQualityDifference}s for the given placed read.
     * @param placedRead
     * @return Always returns a not-null List.  If there are no high quality
     * differences for the given read, this method should return an empty
     * list instead of null.
     */
    List<DefaultQualityDifference> getHighQualityDifferencesFor(PlacedRead placedRead);
    /**
     * Gets the number of reads with highQuality differences.
     * @return
     */
    int getNumberOfReadsWithHighQualityDifferences();

    Set<Entry<PlacedRead, List<DefaultQualityDifference>>> entrySet();

}