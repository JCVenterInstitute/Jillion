/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.List;

import org.jcvi.Range;


public interface CoverageMap <T extends CoverageRegion<?>> extends Iterable<T>{

    int getSize();

    T getRegion(int i);
    List<T> getRegions();
    
    List<T> getRegionsWithin(Range range);
    List<T> getRegionsWhichIntersect(Range range);
    T getRegionWhichCovers(long consensusIndex);
    
    int getRegionIndexWhichCovers(long consensusIndex);
    
    CoverageMap<T> shiftLeft(int units);
    CoverageMap<T> shiftRight(int units);
    
    double getAverageCoverage();
    int getMaxCoverage();
    int getMinCoverage();

}
