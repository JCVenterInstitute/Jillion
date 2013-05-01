package org.jcvi.common.examples;

import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageRegion;

public class FindLowCoverageRegions<T> {

	/**
	 * @param args
	 */
	public static  void main(String[] args) {
		CoverageMap<?> coverageMap = null;
		
		int lowCoverageThreshold = 10;
		
		for(CoverageRegion<?> region : coverageMap){
			if(region.getCoverageDepth() < lowCoverageThreshold){
				//found low coverage region
			}
		}

	}

}
