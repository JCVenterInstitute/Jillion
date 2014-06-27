package org.jcvi.jillion.assembly.util;


public class CoverageStatsCombiner2 {

	int minCoverage = Integer.MAX_VALUE;
	int maxCoverage = Integer.MIN_VALUE;
	
	long totalLength=0;
	long totalCoverage = 0;
	;
	
	
	public CoverageStatsCombiner2 add(CoverageRegion<?> region){
		long regionLength = region.getLength();
		totalLength+=regionLength;
		
		int coverageDepth = region.getCoverageDepth();
		
		totalCoverage+= coverageDepth * regionLength;
		minCoverage = Math.min(minCoverage, coverageDepth);
		maxCoverage = Math.max(maxCoverage, coverageDepth);
		
		
		return this;
	}
	
	public CoverageStatsCombiner2 merge(CoverageStatsCombiner2 other){
		totalLength +=other.totalLength;
		totalCoverage+=other.totalCoverage;
		
		minCoverage = Math.min(minCoverage, other.minCoverage);
		maxCoverage = Math.max(maxCoverage, other.maxCoverage);
		
		return this;
	}
	
	public CoverageMapStats build(){
		if(totalLength ==0){
			return new CoverageMapStats(0, 0, 0D);
		}
		return new CoverageMapStats(minCoverage, maxCoverage, 
				totalCoverage/(double)totalLength);
	}
}
