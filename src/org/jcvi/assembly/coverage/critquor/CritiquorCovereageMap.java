/*
 * Created on May 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.util.Set;

import org.jcvi.assembly.Placed;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;

public interface CritiquorCovereageMap {

    Set<String> getKeySet();
    
    CoverageMap<CoverageRegion<Placed>> getTargetCoverageMap(String key);
    
}
