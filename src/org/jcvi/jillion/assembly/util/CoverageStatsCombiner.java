package org.jcvi.jillion.assembly.util;

import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class CoverageStatsCombiner {

	
	LongAccumulator minCoverage = new LongAccumulator(
						(current, min) -> current < min ? current : min
				, Integer.MAX_VALUE);
	LongAccumulator maxCoverage = new LongAccumulator(
							(current, min) -> current > min ? current : min
					, Integer.MIN_VALUE);
	
	LongAdder totalLength = new LongAdder();
	LongAdder totalCoverage = new LongAdder();
	
	
	public CoverageStatsCombiner add(CoverageRegion<?> region){
		long regionLength = region.getLength();
		totalLength.add(regionLength);
		
		int coverageDepth = region.getCoverageDepth();
		
		totalCoverage.add(coverageDepth * regionLength);
		minCoverage.accumulate(coverageDepth);
		maxCoverage.accumulate(coverageDepth);
		
		
		return this;
	}
	
	public CoverageStatsCombiner merge(CoverageStatsCombiner other){
		totalLength.add(other.totalLength.longValue());
		totalCoverage.add(other.totalCoverage.longValue());
		
		minCoverage.accumulate(other.minCoverage.intValue());
		maxCoverage.accumulate(other.maxCoverage.intValue());
		
		return this;
	}
	
	public CoverageMapStats build(){
		long totalLength = this.totalLength.longValue();
		if(totalLength ==0){
			return new CoverageMapStats(0, 0, 0D);
		}
		return new CoverageMapStats(minCoverage.intValue(), maxCoverage.intValue(), 
				totalCoverage.longValue()/this.totalLength.doubleValue());
	}
}
