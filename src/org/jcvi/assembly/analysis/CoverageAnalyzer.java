/*
 * Created on Mar 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;

public interface CoverageAnalyzer<P extends Placed, PR extends PlacedRead> extends ContigAnalyzer<PR>{
    @Override
    ContigCoverageAnalysis<P> analyize(ContigCheckerStruct<PR> struct);
}
