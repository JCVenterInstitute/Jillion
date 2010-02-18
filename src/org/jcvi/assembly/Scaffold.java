/*
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Set;

import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.Range;

public interface Scaffold {

    String getId();
    PlacedContig getPlacedContig(String id);

    Set<PlacedContig> getPlacedContigs();
    CoverageMap<CoverageRegion<PlacedContig>> getContigMap();
    int getNumberOfContigs();
    long getLength();

    /* converts contig based coordinates into scaffold coordinates */
    Range convertContigRangeToScaffoldRange(String placedContigId, Range placedContigRange);
}
