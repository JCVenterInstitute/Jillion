package org.jcvi.common.core.assembly.util.coverage;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCoverageRestrictedMap {

	@Test
	public void maxCoverageEqualsLimitShouldMakeFullCoverageMap(){
		List<Range> ranges = Arrays.asList(
				Range.create(0,5),
				Range.create(2,7),
				Range.create(6,10)
		);
		
		CoverageMap<Range> coverageMap = 
							DefaultCoverageMap.buildCoverageMap(ranges, 2);
		assertEquals(4,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.create(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverage());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.create(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverage());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.create(6,7));
		assertEquals(2, coverageMap.getRegion(2).getCoverage());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.create(8,10));
		assertEquals(1, coverageMap.getRegion(3).getCoverage());
	}
	
	@Test
	public void coverageLessThanLimitShouldMakeFullCoverageMap(){
		List<Range> ranges = Arrays.asList(
				Range.create(0,5),
				Range.create(2,7),
				Range.create(6,10)
		);
		
		CoverageMap<Range> coverageMap = 
							DefaultCoverageMap.buildCoverageMap(ranges, 3);
		assertEquals(4,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.create(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverage());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.create(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverage());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.create(6,7));
		assertEquals(2, coverageMap.getRegion(2).getCoverage());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.create(8,10));
		assertEquals(1, coverageMap.getRegion(3).getCoverage());
	}
	
	@Test
	public void maxCoverageMoreThanLimitShouldMakeIgnoreReadsThatEnterOverLimit(){
		List<Range> ranges = Arrays.asList(
				Range.create(0,5),
				Range.create(2,7),
				Range.create(4,8),
				Range.create(6,10)
		);
		
		CoverageMap<Range> coverageMap = 
							DefaultCoverageMap.buildCoverageMap(ranges, 2);
		assertEquals(4,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.create(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverage());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.create(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverage());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.create(6,7));
		assertEquals(2, coverageMap.getRegion(2).getCoverage());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.create(8,10));
		assertEquals(1, coverageMap.getRegion(3).getCoverage());
	}
	
	@Test
	public void maxCoverageMoreThanLimitCauses0xRegion(){
		List<Range> ranges = Arrays.asList(
				Range.create(0,5),
				Range.create(2,7),
				Range.create(4,9),
				Range.create(9,10)
		);
		
		CoverageMap<Range> coverageMap = 
							DefaultCoverageMap.buildCoverageMap(ranges, 2);
		assertEquals(5,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.create(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverage());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.create(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverage());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.create(6,7));
		assertEquals(1, coverageMap.getRegion(2).getCoverage());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.create(8,8));
		assertEquals(0, coverageMap.getRegion(3).getCoverage());
		assertEquals(coverageMap.getRegion(4).asRange(), Range.create(9,10));
		assertEquals(1, coverageMap.getRegion(4).getCoverage());
	}
	
	@Test
	public void multipleLimitedCoverageRegions(){
		List<Range> ranges = Arrays.asList(
				Range.create(0,5),
				Range.create(2,7),
				Range.create(4,9),
				Range.create(8,15),
				Range.create(9,10),
				Range.create(10,15)
		);
		
		CoverageMap<Range> coverageMap = 
							DefaultCoverageMap.buildCoverageMap(ranges, 2);
		assertEquals(6,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.create(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverage());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.create(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverage());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.create(6,7));
		assertEquals(1, coverageMap.getRegion(2).getCoverage());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.create(8,8));
		assertEquals(1, coverageMap.getRegion(3).getCoverage());
		assertEquals(coverageMap.getRegion(4).asRange(), Range.create(9,10));
		assertEquals(2, coverageMap.getRegion(4).getCoverage());
		assertEquals(coverageMap.getRegion(5).asRange(), Range.create(11,15));
		assertEquals(1, coverageMap.getRegion(5).getCoverage());
	}
	
	@Test
    public void moreElementsThanRequiredInFirstRegionShouldLimitAllRegions(){
        List<Range> ranges = Arrays.asList(
                Range.create(0,5),
                Range.create(2,7),
                Range.create(6,10),                
                Range.create(0,5)
        );
        
        CoverageMap<Range> coverageMap = 
                            DefaultCoverageMap.buildCoverageMap(ranges, 2);
        assertEquals(2,coverageMap.getNumberOfRegions());
        assertEquals(coverageMap.getRegion(0).asRange(), Range.create(0,5));
        assertEquals(2, coverageMap.getRegion(0).getCoverage());
        assertEquals(coverageMap.getRegion(1).asRange(), Range.create(6,10));
        assertEquals(1, coverageMap.getRegion(1).getCoverage());
    }
}
