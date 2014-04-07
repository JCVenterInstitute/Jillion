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
package org.jcvi.jillion.sam.cigar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestCigar {

	@Test
	public void validCigarStrings(){
		String cigarString = "8M2I40M1D3M";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(5)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 8)
								.addElement(CigarOperation.INSERTION, 2)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 40)
								.addElement(CigarOperation.DELETION, 1)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 3)
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
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
		Cigar cigar = Cigar.parse("5H1M3H");
		assertEquals("5H1M3H", cigar.toCigarString());
	}
	@Test
	public void validCigarSoftClipsAtEnds(){
		Cigar cigar = Cigar.parse("5S1M3S");
		assertEquals("5S1M3S", cigar.toCigarString());
	}
	@Test
	public void validCigarSoftAndHardClipssAtEnds(){
		Cigar cigar = Cigar.parse("3H5S1M3S6H");
		assertEquals("3H5S1M3S6H", cigar.toCigarString());
	}
	
	@Test
	public void toGappedTrimmedSequence(){
		String cigarString = "3H5S8M2I4M1D3M3S6H";
		Cigar cigar = Cigar.parse(cigarString);
		
		NucleotideSequence rawSeq = new NucleotideSequenceBuilder(
							"NNNNNNNN"
						   + "AAAAAAAA"
							+ "CC"
						   +"AAAA"
							+"AAA"
						   +"NNNNNNNNN")
											.build();
		
		NucleotideSequence expected = new NucleotideSequenceBuilder("AAAAAAAA"
											+ "CC"
											   +"AAAA"
											   + "-"
												+"AAA")
										.build();
		
		assertEquals(expected, cigar.toGappedTrimmedSequence(rawSeq));
	}
	
	@Test(expected = NullPointerException.class)
	public void toGappedTrimmedSequenceNullSequenceShouldThrowNPE(){
		String cigarString = "3H5S8M2I4M1D3M3S6H";
		Cigar cigar = Cigar.parse(cigarString);
		cigar.toGappedTrimmedSequence(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void toGappedTrimmedSequenceSequenceHasGapsShouldThrowException(){
		String cigarString = "3H5S8M2I4M1D3M3S6H";
		Cigar cigar = Cigar.parse(cigarString);
		cigar.toGappedTrimmedSequence(new NucleotideSequenceBuilder("ACGTACGT--ACGTACGT").build());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void toGappedTrimmedSequenceSequenceTooShortShouldThrowException(){
		String cigarString = "3H5S8M2I4M1D3M3S6H";
		Cigar cigar = Cigar.parse(cigarString);
		cigar.toGappedTrimmedSequence(new NucleotideSequenceBuilder("ACGTACGTACGTACGT").build());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void toGappedTrimmedSequenceSequenceTooLongShouldThrowException(){
		String cigarString = "3H5S8M2I4M1D3M3S6H";
		Cigar cigar = Cigar.parse(cigarString);
		char[] longSeq = new char[500];
		Arrays.fill(longSeq, 'A');
		cigar.toGappedTrimmedSequence(new NucleotideSequenceBuilder(longSeq).build());
	}
}
