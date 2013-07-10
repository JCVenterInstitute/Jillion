/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
package org.jcvi.jillion.assembly.util.consensus;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.TestSliceUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.Test;
public class TestNextGenReferenceConsensusRecaller {

	NextGenReferenceConsensusRecaller sut = new NextGenReferenceConsensusRecaller();
	@Test(expected = NullPointerException.class)
	public void nullDelegateCallerShouldThrowNPE(){
		new NextGenReferenceConsensusRecaller(null);
	}
	
	@Test
	public void majorityMatchesNonGapConsensusShouldMatchMajority(){
		Slice slice = TestSliceUtil.createSliceFrom("A", "AA",
											new byte[]{20,20}, 
											Direction.FORWARD,Direction.REVERSE);
		
		assertEquals(Nucleotide.Adenine, sut.callConsensus(slice).getConsensus());
	}
	@Test
	public void majorityMatchesGapConsensusShouldMatchMajority(){
		Slice slice = TestSliceUtil.createSliceFrom("-", "--",
											new byte[]{20,20}, 
											Direction.FORWARD,Direction.REVERSE);
		
		assertEquals(Nucleotide.Gap, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void majorityIsNonGapButDifferentThanReferenceShouldMatchMajority(){
		Slice slice = TestSliceUtil.createSliceFrom("A", "GG",
											new byte[]{20,20}, 
											Direction.FORWARD,Direction.REVERSE);
		
		assertEquals(Nucleotide.Guanine, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void deletionMajorityInBothDirectionsShouldCallDeletion(){
		Slice slice = TestSliceUtil.createSliceFrom("A", "--",
											new byte[]{20,20}, 
											Direction.FORWARD,Direction.REVERSE);
		
		assertEquals(Nucleotide.Gap, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void deletionMajorityInOnlyForwardShouldNotCallDeletion(){
		Slice slice = TestSliceUtil.createSliceFrom("A", "---"+"AA",
											new byte[]{20,20, 15, 30,20}, 
											Direction.FORWARD,Direction.FORWARD,Direction.FORWARD,
											Direction.REVERSE, Direction.REVERSE);
		
		assertEquals(Nucleotide.Adenine, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void deletionMajorityInOnlyReverseShouldNotCallDeletion(){
		Slice slice = TestSliceUtil.createSliceFrom("A", "---"+"AA",
											new byte[]{20,20, 15, 30,20}, 
											Direction.REVERSE,Direction.REVERSE,Direction.REVERSE,
											Direction.FORWARD, Direction.FORWARD);
		
		assertEquals(Nucleotide.Adenine, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void insertionMajorityInBothDirectionsShouldCallInsertion(){
		Slice slice = TestSliceUtil.createSliceFrom("-", "AA",
											new byte[]{20,20}, 
											Direction.FORWARD,Direction.REVERSE);
		
		assertEquals(Nucleotide.Adenine, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void insertionMajorityInOnlyForwardShouldCallNotInsertion(){
		Slice slice = TestSliceUtil.createSliceFrom("-", "AA"+"--",
											new byte[]{20,20, 15,20}, 
											Direction.FORWARD,Direction.FORWARD, 
											Direction.REVERSE,Direction.REVERSE);
		
		assertEquals(Nucleotide.Gap, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void insertionMajorityInOnlyReverseShouldCallNotInsertion(){
		Slice slice = TestSliceUtil.createSliceFrom("-", "AA"+"--",
											new byte[]{20,20, 15,20}, 
											Direction.REVERSE,Direction.REVERSE, 
											Direction.FORWARD,Direction.FORWARD);
		
		assertEquals(Nucleotide.Gap, sut.callConsensus(slice).getConsensus());
	}
	@Test
	public void insertionMajorityButOnlyReadsInReverseShouldCallInsertion(){
		Slice slice = TestSliceUtil.createSliceFrom("-", "AA",
											new byte[]{20,20}, 
											Direction.REVERSE,Direction.REVERSE);
		
		assertEquals(Nucleotide.Adenine, sut.callConsensus(slice).getConsensus());
	}
	@Test
	public void insertionMajorityButOnlyReadsInForwardShouldCallInsertion(){
		Slice slice = TestSliceUtil.createSliceFrom("-", "AA",
											new byte[]{20,20}, 
											Direction.FORWARD,Direction.FORWARD);
		
		assertEquals(Nucleotide.Adenine, sut.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void deletionMajorityButOnlyReadsInReverseShouldCallInsertion(){
		Slice slice = TestSliceUtil.createSliceFrom("A", "--",
											new byte[]{20,20}, 
											Direction.REVERSE,Direction.REVERSE);
		
		assertEquals(Nucleotide.Gap, sut.callConsensus(slice).getConsensus());
	}
	@Test
	public void deletionMajorityButOnlyReadsInForwardShouldCallInsertion(){
		Slice slice = TestSliceUtil.createSliceFrom("A", "--",
											new byte[]{20,20}, 
											Direction.FORWARD,Direction.FORWARD);
		
		assertEquals(Nucleotide.Gap, sut.callConsensus(slice).getConsensus());
	}
	/**
	 * Regression test of actual bug found in consensus recall
	 * of sanger-only low coverage data where
	 * there were gaps in each direction to make a majority of gap
	 * but each underlying strand had a different non-gap majority.
	 */
	@Test
	public void deletionEachStrandCallsSomethingElseShouldGoWithDirectionThatMatches(){
		Slice slice = TestSliceUtil.createSliceFrom("C", "C-a-",
				new byte[]{44,10,10, 7}, 
				Direction.REVERSE,Direction.REVERSE, Direction.FORWARD, Direction.FORWARD);
		
		assertEquals(Nucleotide.Cytosine, sut.callConsensus(slice).getConsensus());
	}
	
	
}
