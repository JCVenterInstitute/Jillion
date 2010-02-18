/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.slice.SliceMap;

public interface ContigStruct<R extends PlacedRead> {

    Contig<R> getContig();
    
    CoverageMap<CoverageRegion<R>> getSequenceCoverageMap();
    
    SliceMap getSliceMap();
}
