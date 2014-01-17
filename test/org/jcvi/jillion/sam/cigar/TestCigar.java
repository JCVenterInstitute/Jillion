package org.jcvi.jillion.sam.cigar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestCigar {

	@Test
	public void validCigarStrings(){
		Cigar cigar = Cigar.parse("8M2I40M1D3M");
		Cigar expected = new Cigar.Builder(5)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 8)
								.addElement(CigarOperation.INSERTION, 2)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 40)
								.addElement(CigarOperation.DELETION, 1)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 3)
								.build();
		
		assertEquals(expected, cigar);
	}
	
	@Test
	public void asterikCigarStringShouldReturnNull(){
		assertNull(Cigar.parse("*"));
	}
	
	@Test
	public void sameValuesEqual(){
		Cigar cigar1 = Cigar.parse("8M2I40M1D3M");
		Cigar cigar2 = Cigar.parse("8M2I40M1D3M");
		TestUtil.assertEqualAndHashcodeSame(cigar1, cigar2);
	}
	@Test
	public void diffValuesEqual(){
		Cigar cigar1 = Cigar.parse("8M2I40M1D3M");
		Cigar cigar2 = Cigar.parse("6H5M");
		TestUtil.assertNotEqualAndHashcodeDifferent(cigar1, cigar2);
	}
	@Test
	public void sameRefEqual(){
		Cigar cigar = Cigar.parse("8M2I40M1D3M");
		TestUtil.assertEqualAndHashcodeSame(cigar, cigar);
	}
	
	@Test(expected = IllegalStateException.class)
	public void invalidCigarHardClipNotAtEdgeOfString(){
		Cigar.parse("5M6H5M");
	}
	@Test(expected = IllegalStateException.class)
	public void invalidCigarSoftClipHasSomethingOtherThanHardClipBetweenItAndLeftEdge(){
		Cigar.parse("5M6S5M1D6M");
	}
	@Test(expected = IllegalStateException.class)
	public void invalidCigarSoftClipHasSomethingOtherThanHardClipBetweenItAndRightEdge(){
		Cigar.parse("6S5M1D6S6M");
	}
	
	@Test
	public void validCigarHardClipsAtEnds(){
		Cigar.parse("5H1M3H");
	}
	@Test
	public void validCigarSoftClipsAtEnds(){
		Cigar.parse("5S1M3S");
	}
	@Test
	public void validCigarSoftAndHardClipssAtEnds(){
		Cigar.parse("3H5S1M3S6H");
	}
}
