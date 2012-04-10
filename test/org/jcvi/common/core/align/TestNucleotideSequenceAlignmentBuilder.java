package org.jcvi.common.core.align;

import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideSequenceAlignmentBuilder {

	NucleotideSequenceAlignmentBuilder sut;
	
	@Before
	public void setup(){
		sut = new NucleotideSequenceAlignmentBuilder();
	}
	@Test
	public void noAlignmentShouldHave0PercentIdent(){
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(0, alignment.getAlignmentLength());
		assertEquals(0, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(0D, alignment.getPercentIdentity(),0D);
		assertTrue(alignment.getGappedQueryAlignment().toString().isEmpty());
		assertTrue(alignment.getGappedSubjectAlignment().toString().isEmpty());
	}
	
	@Test
	public void onlyOneMatch(){
		sut.addMatch(Nucleotide.Adenine);
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(1, alignment.getAlignmentLength());
		assertEquals(0, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(1D, alignment.getPercentIdentity(),0D);
		assertEquals("A", alignment.getGappedQueryAlignment().toString());
		assertEquals("A", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void onlyOneMisMatch(){
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(1, alignment.getAlignmentLength());
		assertEquals(1, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(0D, alignment.getPercentIdentity(),0D);
		assertEquals("G", alignment.getGappedQueryAlignment().toString());
		assertEquals("A", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void manymatchesAndMismatches(){
		sut.addMatches(Nucleotides.parse("ACGT"));
		
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		sut.addMismatch(Nucleotide.Thymine,Nucleotide.Adenine);
		sut.addMatches(Nucleotides.parse("ACGT"));
		
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(10, alignment.getAlignmentLength());
		assertEquals(2, alignment.getNumberOfMismatches());
		assertEquals(0, alignment.getNumberOfGapOpenings());
		assertEquals(.8D, alignment.getPercentIdentity(),0D);
		assertEquals("ACGTGTACGT", alignment.getGappedQueryAlignment().toString());
		assertEquals("ACGTAAACGT", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void manymatchesAndMismatchesAndGap(){
		sut.addMatches(Nucleotides.parse("ACGT"));
		
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		sut.addGap(Nucleotide.Thymine,Nucleotide.Gap);
		sut.addMatches(Nucleotides.parse("ACGT"));
		
		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(10, alignment.getAlignmentLength());
		assertEquals(1, alignment.getNumberOfMismatches());
		assertEquals(1, alignment.getNumberOfGapOpenings());
		assertEquals(.8D, alignment.getPercentIdentity(),0D);
		assertEquals("ACGTGTACGT", alignment.getGappedQueryAlignment().toString());
		assertEquals("ACGTA-ACGT", alignment.getGappedSubjectAlignment().toString());
	}
	
	@Test
	public void buildFromTraceback(){
		sut = new NucleotideSequenceAlignmentBuilder(true);
		sut.addMatches(Nucleotides.parse("ACGT"));
		
		sut.addMismatch(Nucleotide.Guanine,Nucleotide.Adenine);
		sut.addGap(Nucleotide.Thymine,Nucleotide.Gap);
		sut.addMatches(Nucleotides.parse("ACGT"));

		NucleotideSequenceAlignment alignment = sut.build();
		assertEquals(10, alignment.getAlignmentLength());
		assertEquals(1, alignment.getNumberOfMismatches());
		assertEquals(1, alignment.getNumberOfGapOpenings());
		assertEquals(.8D, alignment.getPercentIdentity(),0D);

		assertEquals("TGCATGTGCA", alignment.getGappedQueryAlignment().toString());		
		assertEquals("TGCA-ATGCA", alignment.getGappedSubjectAlignment().toString());
	}
	
}
