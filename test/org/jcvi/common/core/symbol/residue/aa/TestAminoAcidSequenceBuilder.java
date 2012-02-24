package org.jcvi.common.core.symbol.residue.aa;

import java.util.Arrays;

import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.common.core.symbol.residue.aa.AminoAcids;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAminoAcidSequenceBuilder {

	@Test
	public void emptyConstructorShouldStartHaveNoResidues(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		assertTrue(sut.build().asList().isEmpty());
		assertEquals(0, sut.getNumGaps());
		assertEquals(0L, sut.getLength());
	}
	
	@Test
	public void stringConstructorShouldStartWithResiduesGiven(){
		String expected = "IKFTW";
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder(expected);
		assertEquals(expected, AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void appendSingleAminoAcid(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		sut.append(AminoAcid.Isoleucine);
		assertEquals("I", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(1L, sut.getLength());
	}
	@Test
	public void appendSingleAminoAcidAsString(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		sut.append("I");
		assertEquals("I", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(1L, sut.getLength());
	}
	
	@Test
	public void appendMultipleAminoAcidsAsString(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		sut.append("IKFTW");
		assertEquals("IKFTW", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void reverse(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.reverse();
		assertEquals("WTFKI", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void multipleAppendsString(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.append("IHH");
		sut.append("F");
		assertEquals("IKFTWIHHF", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(9L, sut.getLength());
	}
	@Test
	public void multipleAppendsAminoAcids(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.append(AminoAcid.Glutamic_Acid);
		sut.append(AminoAcid.Methionine);
		assertEquals("IKFTWEM", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(7L, sut.getLength());
	}
	@Test
	public void noGapsInSequence(){
		AminoAcidSequence sut = new AminoAcidSequenceBuilder("IKFTW")
								.build();
		assertTrue((sut instanceof UngappedAminoAcidSequence));
		assertEquals(0, sut.getNumberOfGaps());
		assertEquals(5, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(sut.asList(), sut.asUngappedList());
		assertEquals(4, sut.getGappedOffsetFor(4));
		assertEquals(4, sut.getUngappedOffsetFor(4));
	}
	
	@Test
	public void gapsInSequence(){
		AminoAcidSequence sut = new AminoAcidSequenceBuilder("IKF-TW")
								.build();
		assertFalse((sut instanceof UngappedAminoAcidSequence));
		assertEquals(1, sut.getNumberOfGaps());
		assertEquals(6, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertEquals(Arrays.asList(3),sut.getGapOffsets());
		assertEquals(AminoAcids.parse("IKFTW"), sut.asUngappedList());
		
		assertEquals("before gap",2, sut.getGappedOffsetFor(2));
		assertEquals("after gap",4, sut.getGappedOffsetFor(3));
		
		assertEquals("before gap", 2, sut.getUngappedOffsetFor(2));
		assertEquals("on gap", 2, sut.getUngappedOffsetFor(3));
		assertEquals("after gap", 4, sut.getUngappedOffsetFor(5));
	}
}
