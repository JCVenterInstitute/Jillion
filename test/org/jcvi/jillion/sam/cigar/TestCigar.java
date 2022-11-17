/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;
public class TestCigar {

	@Test
	public void emptyCigar(){
		assertEquals(Cigar.EMPTY_CIGAR, new Cigar.Builder(0).build());
		assertEquals(0, Cigar.EMPTY_CIGAR.getNumberOfElements());
	}
	
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
        public void withWhiteSpace(){
                Cigar cigar1 = Cigar.parse("8 M 2 I 4 0 M 1 D 3 M");
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
	public void splicedAlignment(){
		NucleotideSequence read = NucleotideSequenceTestUtil.create("GTGTAACCCTCAGAATA");
		Cigar cigar = Cigar.parse("9M32N8M");
		
		assertEquals(NucleotideSequenceTestUtil.create(
				"GTGTAACCC"+
				NucleotideSequenceTestUtil.create("*", 32)
				+ "TCAGAATA"),
				cigar.toGappedTrimmedSequence(read)
				);
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
	
	@Test
	public void onlyClippedOnOneEnd(){
		String cigarString = "8H3S40M";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								.addElement(CigarOperation.HARD_CLIP, 8)
								.addElement(CigarOperation.SOFT_CLIP, 3)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 40)
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	@Test
	public void onlyClippedOnOneEndOtherEnd(){
		String cigarString = "40M3S8H";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 40)
								.addElement(CigarOperation.SOFT_CLIP, 3)
								.addElement(CigarOperation.HARD_CLIP, 8)
								
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	@Test
	public void noClips(){
		String cigarString = "40M";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 40)
								
								
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	
	@Test
	public void softClipNoHardOnlyOnOneEnd(){
		String cigarString = "23S121M";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								
								.addElement(CigarOperation.SOFT_CLIP, 23)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 121)
								
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	@Test
	public void softClipNoHardOnlyOnOneEndOtherSide(){
		String cigarString = "121M23S";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 121)
								.addElement(CigarOperation.SOFT_CLIP, 23)
								
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	
	@Test
	public void softClipOneSideHardClipOntheOther(){
		String cigarString = "22S50M77H";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								.addElement(CigarOperation.SOFT_CLIP, 22)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 50)
								.addElement(CigarOperation.HARD_CLIP, 77)
								
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	@Test
	public void softClipOneSideHardClipOntheOtherReverseSide(){
		String cigarString = "77H50M22S";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								.addElement(CigarOperation.HARD_CLIP, 77)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 50)
								
								.addElement(CigarOperation.SOFT_CLIP, 22)
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	
	@Test
	public void hardClipSoftClipOneSideHardClipOntheOther(){
		String cigarString = "20H3S50M75H";
		Cigar cigar = Cigar.parse(cigarString);
		Cigar expected = new Cigar.Builder(3)
								.addElement(CigarOperation.HARD_CLIP, 20)
								.addElement(CigarOperation.SOFT_CLIP, 3)
								.addElement(CigarOperation.ALIGNMENT_MATCH, 50)
								
								.addElement(CigarOperation.HARD_CLIP, 75)
								
								.build();
		
		assertEquals(expected, cigar);
		assertEquals(cigarString, cigar.toCigarString());
	}
	
	@Test
	public void toBuilderCopy() {
		String cigarString = "20H3S50M75H";
		
		Cigar cigar = Cigar.parse(cigarString);
		Cigar copy = cigar.toBuilder().build();
		assertEquals(cigarString, copy.toCigarString());
	}
	
	
}
