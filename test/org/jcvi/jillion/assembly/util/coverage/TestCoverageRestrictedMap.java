/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.coverage;

import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.assembly.util.coverage.CoverageMap;
import org.jcvi.jillion.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.jillion.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCoverageRestrictedMap {

	@Test
	public void maxCoverageEqualsLimitShouldMakeFullCoverageMap(){
		List<Range> ranges = Arrays.asList(
				Range.of(0,5),
				Range.of(2,7),
				Range.of(6,10)
		);
		
		CoverageMap<Range> coverageMap = 
							CoverageMapFactory.create(ranges, 2);
		assertEquals(4,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.of(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverageDepth());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.of(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverageDepth());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.of(6,7));
		assertEquals(2, coverageMap.getRegion(2).getCoverageDepth());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.of(8,10));
		assertEquals(1, coverageMap.getRegion(3).getCoverageDepth());
	}
	
	@Test
	public void coverageLessThanLimitShouldMakeFullCoverageMap(){
		List<Range> ranges = Arrays.asList(
				Range.of(0,5),
				Range.of(2,7),
				Range.of(6,10)
		);
		
		CoverageMap<Range> coverageMap = 
							CoverageMapFactory.create(ranges, 3);
		assertEquals(4,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.of(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverageDepth());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.of(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverageDepth());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.of(6,7));
		assertEquals(2, coverageMap.getRegion(2).getCoverageDepth());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.of(8,10));
		assertEquals(1, coverageMap.getRegion(3).getCoverageDepth());
	}
	
	@Test
	public void maxCoverageMoreThanLimitShouldMakeIgnoreReadsThatEnterOverLimit(){
		List<Range> ranges = Arrays.asList(
				Range.of(0,5),
				Range.of(2,7),
				Range.of(4,8),
				Range.of(6,10)
		);
		
		CoverageMap<Range> coverageMap = 
							CoverageMapFactory.create(ranges, 2);
		assertEquals(4,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.of(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverageDepth());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.of(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverageDepth());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.of(6,7));
		assertEquals(2, coverageMap.getRegion(2).getCoverageDepth());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.of(8,10));
		assertEquals(1, coverageMap.getRegion(3).getCoverageDepth());
	}
	
	@Test
	public void maxCoverageMoreThanLimitCauses0xRegion(){
		List<Range> ranges = Arrays.asList(
				Range.of(0,5),
				Range.of(2,7),
				Range.of(4,9),
				Range.of(9,10)
		);
		
		CoverageMap<Range> coverageMap = 
							CoverageMapFactory.create(ranges, 2);
		assertEquals(5,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.of(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverageDepth());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.of(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverageDepth());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.of(6,7));
		assertEquals(1, coverageMap.getRegion(2).getCoverageDepth());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.of(8,8));
		assertEquals(0, coverageMap.getRegion(3).getCoverageDepth());
		assertEquals(coverageMap.getRegion(4).asRange(), Range.of(9,10));
		assertEquals(1, coverageMap.getRegion(4).getCoverageDepth());
	}
	
	@Test
	public void multipleLimitedCoverageRegions(){
		List<Range> ranges = Arrays.asList(
				Range.of(0,5),
				Range.of(2,7),
				Range.of(4,9),
				Range.of(8,15),
				Range.of(9,10),
				Range.of(10,15)
		);
		
		CoverageMap<Range> coverageMap = 
							CoverageMapFactory.create(ranges, 2);
		assertEquals(6,coverageMap.getNumberOfRegions());
		assertEquals(coverageMap.getRegion(0).asRange(), Range.of(0,1));
		assertEquals(1, coverageMap.getRegion(0).getCoverageDepth());
		assertEquals(coverageMap.getRegion(1).asRange(), Range.of(2,5));
		assertEquals(2, coverageMap.getRegion(1).getCoverageDepth());
		assertEquals(coverageMap.getRegion(2).asRange(), Range.of(6,7));
		assertEquals(1, coverageMap.getRegion(2).getCoverageDepth());
		assertEquals(coverageMap.getRegion(3).asRange(), Range.of(8,8));
		assertEquals(1, coverageMap.getRegion(3).getCoverageDepth());
		assertEquals(coverageMap.getRegion(4).asRange(), Range.of(9,10));
		assertEquals(2, coverageMap.getRegion(4).getCoverageDepth());
		assertEquals(coverageMap.getRegion(5).asRange(), Range.of(11,15));
		assertEquals(1, coverageMap.getRegion(5).getCoverageDepth());
	}
	
	@Test
    public void moreElementsThanRequiredInFirstRegionShouldLimitAllRegions(){
        List<Range> ranges = Arrays.asList(
                Range.of(0,5),
                Range.of(2,7),
                Range.of(6,10),                
                Range.of(0,5)
        );
        
        CoverageMap<Range> coverageMap = 
                            CoverageMapFactory.create(ranges, 2);
        assertEquals(2,coverageMap.getNumberOfRegions());
        assertEquals(coverageMap.getRegion(0).asRange(), Range.of(0,5));
        assertEquals(2, coverageMap.getRegion(0).getCoverageDepth());
        assertEquals(coverageMap.getRegion(1).asRange(), Range.of(6,10));
        assertEquals(1, coverageMap.getRegion(1).getCoverageDepth());
    }
}
