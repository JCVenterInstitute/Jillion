package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence.Variant;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence.Variant.VariantBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestVariantNucleotideSequence {

	private static final double ONE_THIRD = 1/3D;
	@Test
	public void noSequence() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().build();
		assertTrue(sut.getVariants().isEmpty());
		assertTrue(sut.getNucleotideSequence().isEmpty());
		assertFalse(sut.variantIterator().hasNext());
		assertFalse(sut.reverseComplementVariantIterator().hasNext());
		assertEquals("", sut.toString());
	}
	@Test
	public void noSequenceUngap() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().ungap().build();
		assertTrue(sut.getVariants().isEmpty());
		assertTrue(sut.getNucleotideSequence().isEmpty());
		assertFalse(sut.variantIterator().hasNext());
		assertFalse(sut.reverseComplementVariantIterator().hasNext());
		assertEquals("", sut.toString());
	}
	
	@Test
	public void noSequenceWrapper() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.of(NucleotideSequence.of(""));
		assertTrue(sut.getVariants().isEmpty());
		assertTrue(sut.getNucleotideSequence().isEmpty());
		assertFalse(sut.variantIterator().hasNext());
		assertFalse(sut.reverseComplementVariantIterator().hasNext());
		assertEquals("", sut.toString());
	}
	
	@Test
	public void sequenceNoVariants() {
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.of(seq);
		assertEquals(seq, sut.getNucleotideSequence());
		assertTrue(sut.getVariants().isEmpty());
		assertIteratorsEquals(createVariantIterator("T", "A", "C", "G"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("C", "G", "T", "A"), sut.reverseComplementVariantIterator());
		assertEquals("TACG", sut.toString());
	}@Test
	public void sequenceNoVariantsUngap() {
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq).ungap().build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertTrue(sut.getVariants().isEmpty());
		assertIteratorsEquals(createVariantIterator("T", "A", "C", "G"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("C", "G", "T", "A"), sut.reverseComplementVariantIterator());
		assertEquals("TACG", sut.toString());
	}
	
	@Test
	public void oneVariant() {
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(1, Nucleotide.Cytosine, .5)
												.build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("T", "AC", "C", "G"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("C", "G", "TG", "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T{A/C}CG", sut.toString());
	}
	@Test
	public void oneVariantUnGap() {
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(1, Nucleotide.Cytosine, .5)
												.ungap()
												.build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("T", "AC", "C", "G"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("C", "G", "TG", "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T{A/C}CG", sut.toString());
	}
	@Test
	public void multipleVariantsSameOffset() {
		
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(1, Nucleotide.Cytosine, ONE_THIRD)
												.variant(1, Nucleotide.Thymine, ONE_THIRD)
												.build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, ONE_THIRD)
				.addAllele(Nucleotide.Thymine, ONE_THIRD).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("T", "ACT", "C", "G"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("C", "G", "TGA", "A"), sut.reverseComplementVariantIterator());
		assertEquals("T{A/C/T}CG", sut.toString());
	}
	@Test
	public void multipleVariantsSameOffsetUngap() {
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(1, Nucleotide.Cytosine, ONE_THIRD)
												.variant(1, Nucleotide.Thymine, ONE_THIRD)
												.ungap()
												.build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, ONE_THIRD)
				.addAllele(Nucleotide.Thymine, ONE_THIRD).build()),
				sut.getVariants());
		
		assertIteratorsEquals(createVariantIterator("T", "ACT", "C", "G"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("C", "G", "TGA", "A"), sut.reverseComplementVariantIterator());
		assertEquals("T{A/C/T}CG", sut.toString());
	}
	@Test
	public void multipleVariantsDifferentOffsets() {
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(1, Nucleotide.Cytosine, .5)
												.variant(3, Nucleotide.Thymine, .5)
												.build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
			 3, new VariantBuilder(Nucleotide.Guanine)
			.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		
		assertIteratorsEquals(createVariantIterator("T", "AC", "C", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "G", "TG", "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T{A/C}C{G/T}", sut.toString());
	}
	@Test
	public void multipleVariantsDifferentOffsetsNoGapUnGap() {
		NucleotideSequence seq = NucleotideSequence.of("TACG");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(1, Nucleotide.Cytosine, .5)
												.variant(3, Nucleotide.Thymine, .5)
												.ungap()
												.build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
									.addAllele(Nucleotide.Cytosine, .5).build(),
								 3, new VariantBuilder(Nucleotide.Guanine)
								.addAllele(Nucleotide.Thymine, .5).build()),
				
				sut.getVariants());
		
		assertIteratorsEquals(createVariantIterator("T", "AC", "C", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "G", "TG", "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T{A/C}C{G/T}", sut.toString());
	}
	
	@Test
	public void multipleVariantsDifferentOffsetsWithGaps() {
		NucleotideSequence seq = NucleotideSequence.of("T-AC-G");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(5, Nucleotide.Thymine, .5)
												.build();
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(Map.of(2, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				5, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("T","-", "AC", "C","-", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "-","G", "TG","-", "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T-{A/C}C-{G/T}", sut.toString());
	}
	@Test
	public void multipleVariantsDifferentOffsetsWithGapsUnGap() {
		NucleotideSequence seq = NucleotideSequence.of("T-AC-G");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(5, Nucleotide.Thymine, .5)
												.ungap()
												.build();
		assertEquals(seq.toBuilder().ungap().build(), sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				3, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("T", "AC", "C", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "G", "TG", "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T{A/C}C{G/T}", sut.toString());
	}
	@Test
	public void multipleVariantsDifferentOffsetsWithLotsOfGapsUnGap() {
		NucleotideSequence seq = NucleotideSequence.of("T-A--C-G");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(7, Nucleotide.Thymine, .5)
												.ungap()
												.build();
		assertEquals(seq.toBuilder().ungap().build(), sut.getNucleotideSequence());
		assertEquals(Map.of(1, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				3, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("T", "AC", "C", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "G", "TG", "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T{A/C}C{G/T}", sut.toString());
	}
	private static <T> void  assertIteratorsEquals(Iterator<T> expected, Iterator<T> actual) {
		
		while(expected.hasNext()) {
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}

	private Iterator<Variant> createVariantIterator(String...cols){
		List<Variant> list = new ArrayList<>();
		for(String c : cols) {
			char[] cs = c.toCharArray();
			List<Nucleotide> l = new ArrayList<>();
			for(int i=0; i< cs.length; i++) {
				Nucleotide n = Nucleotide.parseOrNull(cs[i]);
				if(n !=null) {
					l.add(n);
				}
				
			}
			Iterator<Nucleotide> iter = l.iterator();
			Variant.VariantBuilder builder = new Variant.VariantBuilder(iter.next());
			double percent = 1D/l.size();
			while(iter.hasNext()) {
				builder.addAllele(iter.next(), percent);
			}
			list.add(builder.build());
		}
		return list.iterator();
		
	}
	
	@Test
	public void toBuilderKeepsVariants() {
		NucleotideSequence seq = NucleotideSequence.of("T-A--C-G");
		VariantNucleotideSequence varSeq = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(7, Nucleotide.Thymine, .5)
												.build();
		VariantNucleotideSequence sut = varSeq.toBuilder().build();
		assertEquals(Map.of(2, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				7, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("T", "-", "AC","-", "-", "C", "-", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "-", "G","-", "-",  "TG","-",  "A"), sut.reverseComplementVariantIterator());
		
		assertEquals("T-{A/C}--C-{G/T}", sut.toString());
	}
	@Test
	public void toBuilderTrimKeepsVariantsWithAdjustedStart() {
		NucleotideSequence seq = NucleotideSequence.of("T-A--C-G");
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(7, Nucleotide.Thymine, .5)
												.trim(Range.of(2,7))
												.build();
		
		assertEquals(Map.of(0, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				5, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("AC","-", "-", "C", "-", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "-", "G","-", "-",  "TG"), sut.reverseComplementVariantIterator());
		
		assertEquals("{A/C}--C-{G/T}", sut.toString());
	}
	@Test
	public void toBuilderRangeKeepsVariantsWithAdjustedStart() {
		NucleotideSequence seq = NucleotideSequence.of("T-A--C-G");
		VariantNucleotideSequence varSeq = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(7, Nucleotide.Thymine, .5)
												.build();
		VariantNucleotideSequence sut = varSeq.toBuilder(Range.of(2,7)).build();
		assertEquals(Map.of(0, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				5, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("AC","-", "-", "C", "-", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "-", "G","-", "-",  "TG"), sut.reverseComplementVariantIterator());
		
		assertEquals("{A/C}--C-{G/T}", sut.toString());
	}
	
	@Test
	public void toBuilderListOfRangesKeepsVariantsWithAdjustedStart() {
		NucleotideSequence seq = NucleotideSequence.of("T-A--C-G");
		VariantNucleotideSequence varSeq = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(7, Nucleotide.Thymine, .5)
												.build();
		VariantNucleotideSequence sut = varSeq.toBuilder(List.of(Range.of(2,2), Range.of(5,7)) ).build();
		assertEquals(Map.of(0, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				3, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("AC", "C", "-", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "-", "G",  "TG"), sut.reverseComplementVariantIterator());
		
		assertEquals("{A/C}C-{G/T}", sut.toString());
	}
	
	@Test
	public void toBuilderRangesListOfOneKeepsVariantsWithAdjustedStart() {
		NucleotideSequence seq = NucleotideSequence.of("T-A--C-G");
		VariantNucleotideSequence varSeq = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(7, Nucleotide.Thymine, .5)
												.build();
		VariantNucleotideSequence sut = varSeq.toBuilder(List.of(Range.of(2,7))).build();
		assertEquals(Map.of(0, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				5, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("AC","-", "-", "C", "-", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "-", "G","-", "-",  "TG"), sut.reverseComplementVariantIterator());
		
		assertEquals("{A/C}--C-{G/T}", sut.toString());
	}
	@Test
	public void toBuilderRangesOverlappingRangesKeepsVariantsWithAdjustedStart() {
		NucleotideSequence seq = NucleotideSequence.of("T-A--C-G");
		VariantNucleotideSequence varSeq = VariantNucleotideSequence.builder(seq)
												.variant(2, Nucleotide.Cytosine, .5)
												.variant(7, Nucleotide.Thymine, .5)
												.build();
		VariantNucleotideSequence sut = varSeq.toBuilder(List.of(Range.of(2,7), Range.of(5,7))).build();
		assertEquals(Map.of(0, new VariantBuilder(Nucleotide.Adenine)
				.addAllele(Nucleotide.Cytosine, .5).build(),
				5, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build(),
				8, new VariantBuilder(Nucleotide.Guanine)
				.addAllele(Nucleotide.Thymine, .5).build()
				),
				sut.getVariants());
		assertIteratorsEquals(createVariantIterator("AC","-", "-", "C", "-", "GT","C", "-", "GT"), sut.variantIterator());
		assertIteratorsEquals(createVariantIterator("CA", "-", "G", "CA", "-", "G","-", "-",  "TG"), sut.reverseComplementVariantIterator());
		
		assertEquals("{A/C}--C-{G/T}C-{G/T}", sut.toString());
	}
}
