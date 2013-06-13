package org.jcvi.jillion.assembly.util.consensus;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.TestSliceUtil;
import org.jcvi.jillion.assembly.util.consensus.NextGenReferenceConsensusRecaller;
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
	
	
}
