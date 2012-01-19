package org.jcvi.common.core.symbol.residue.aa;

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
	
}
