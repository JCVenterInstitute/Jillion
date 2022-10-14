package org.jcvi.jillion.core.residue.aa;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.residue.aa.VariantProteinSequence.SNP;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

public class TestVariantRNA {


	@Test
	public void hasDnaInMainSequenceNoVariants() {
		NucleotideSequence seq = NucleotideSequence.of("ACGT");
		
		VariantNucleotideSequence sut = new VariantNucleotideSequence.Builder(seq).build();
		assertTrue(sut.isDna());
		assertFalse(sut.isRna());
	}
	@Test
	public void hasRnaInMainSequenceNoVariants() {
		NucleotideSequence seq = NucleotideSequence.of("ACGU");
		
		VariantNucleotideSequence sut = new VariantNucleotideSequence.Builder(seq).build();
		assertFalse(sut.isDna());
		assertTrue(sut.isRna());
	}
	
	@Test
	public void hasDnaInMainSequenceNoRnaVariants() {
		NucleotideSequence seq = NucleotideSequence.of("ACGT");
		
		VariantNucleotideSequence sut = new VariantNucleotideSequence.Builder(seq)
													.variant(2, Nucleotide.Adenine, .5)
													.build();
		assertTrue(sut.isDna());
		assertFalse(sut.isRna());
	}
	@Test
	public void hasDnaInMainSequenceHasRnaVariants() {
		NucleotideSequence seq = NucleotideSequence.of("ACGT");
		
		VariantNucleotideSequence sut = new VariantNucleotideSequence.Builder(seq)
													.variant(2, Nucleotide.Uracil, .5)
													.build();
		assertFalse(sut.isDna());
		assertTrue(sut.isRna());
	}

	@Test
	public void translateVariantWithMajorityUracil() {
		NucleotideSequence seq = NucleotideSequence.of("UUU");
		
		VariantNucleotideSequence sut = new VariantNucleotideSequence.Builder(seq).build();
		AminoAcid actual = IupacTranslationTables.STANDARD.translate(sut).getProteinSequence().get(0);
		assertEquals(AminoAcid.parse("F"), actual);
	}
	
	@Test
	public void translateVariantWithMinorityUracil() {
		NucleotideSequence seq = NucleotideSequence.of("UUG");
		
		VariantNucleotideSequence sut = new VariantNucleotideSequence.Builder(seq)
													.variant(2, Nucleotide.Uracil, .5)
													.build();
		VariantProteinSequence proteinSeq = IupacTranslationTables.STANDARD.translate(sut);
		assertEquals(AminoAcid.parse("L"), proteinSeq.getProteinSequence().get(0));
		Map<AminoAcid, SNP> snps = proteinSeq.getVariants().get(0);
		
		Map<AminoAcid, SNP> expected = Map.of(AminoAcid.parse("L"), 
													SNP.builder().aa(AminoAcid.parse("L"))
																		.isMajority(true)
																		.percent(.5)
																		.build(),
											AminoAcid.parse("F"), 
											SNP.builder().aa(AminoAcid.parse("F"))
																.isMajority(false)
																.percent(.5)
																.build()						
				);
		
		assertEquals(expected, snps);
	}
	
}
