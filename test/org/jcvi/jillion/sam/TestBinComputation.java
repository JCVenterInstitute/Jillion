/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.junit.Test;
public class TestBinComputation {

	@Test
	public void rangeCoversSingleBin(){
		assertRangeComputedCorrectly(120, 264, 4681);
		assertRangeComputedCorrectly(975, 1071, 4681);
		assertRangeComputedCorrectly(103, 237, 4681);
		assertRangeComputedCorrectly(1304, 1699, 4681);
		
		assertRangeComputedCorrectly(0, 10, 4681);
		assertRangeComputedCorrectly(16001, 16010, 4681);
		assertRangeComputedCorrectly(16500, 17000, 4682);
		assertRangeComputedCorrectly(30000, 32000, 4682);
		assertRangeComputedCorrectly(40000, 44000, 4683);
		
		
		assertRangeComputedCorrectly(129000, 130000, 4688);
		assertRangeComputedCorrectly(160001, 160010, 4690);
		
	}
	
	@Test
	public void rangeCoversMultipleLeafBinsShouldReturnParentBin(){
		assertRangeComputedCorrectly(0, 17000, 585);
		assertRangeComputedCorrectly(18000, 40000, 585);	
		
		assertRangeComputedCorrectly(160000, 170000, 586);	
		
	}
	
	@Test
	public void rangeCoversMultipleMiddleLevelBinsShouldReturnParentBin(){

		assertRangeComputedCorrectly(18000, 200000, 73);
		//length almost 2 MB
		assertRangeComputedCorrectly(0, 2000000, 9);
		//length 9MB
		assertRangeComputedCorrectly(0, 9437184, 1);
		//length > 64MB
		assertRangeComputedCorrectly(0, 67108864, 0);
	}
	
	
	private void assertRangeComputedCorrectly(int begin, int end, int expectedBin){
		assertEquals(expectedBin, SamUtil.computeBinFor(Range.of(begin, end)));
		//end needs to be exlusive
		assertEquals(expectedBin, SamUtil.computeBinFor(begin, end+1));
	}
	
	private void assertRangeComputedCorrectly(int begin, int end, int[] expectedBin){
		int[] candidateOverlappingBins = SamUtil.getCandidateOverlappingBins(Range.of(begin, end));
		assertArrayEquals(expectedBin, candidateOverlappingBins);
		//end needs to be exlusive
		assertArrayEquals(expectedBin, SamUtil.getCandidateOverlappingBins(begin, end+1));
	}
	
	
	
	@Test
	public void computeCandiateOverlappingBins(){
		assertRangeComputedCorrectly(65000, 70000, new int[]{0, 1, 9, 73, 585, 4684, 4685});
		assertRangeComputedCorrectly(65000, 129000, new int[]{0, 1, 9, 73, 585, 4684, 4685, 4686, 4687, 4688});
		
		int _1mb = 1024*1024;
		int _1mbPlus128kb = _1mb + 1024 * 128;
		
		assertRangeComputedCorrectly(_1mb, _1mbPlus128kb, new int[]{0, 1, 9, 74, 593, 594, 4745, 4746, 4747, 4748, 4749, 4750, 4751, 4752, 4753});
		
				
		
	}
}
