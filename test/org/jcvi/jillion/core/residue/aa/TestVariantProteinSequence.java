package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.aa.VariantProteinSequence.SNP;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
public class TestVariantProteinSequence {

	@Test
	public void noVariants() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq).build();
		assertEquals(seq, sut.getProteinSequence());
		assertEquals(Collections.emptyMap(),  sut.getVariants());
	}
	
	@Test
	public void oneVariant() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.build();
		
		assertEquals(seq, sut.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(0, "AK");
		
		assertEquals(expectedSnps,  sut.getVariants());
	}
	@Test
	public void twoVariantsSameOffset() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(0, AminoAcid.parse('R'))
				.build();
		
		assertEquals(seq, sut.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(0, "AKR");
		
		assertEquals(expectedSnps,  sut.getVariants());
	}
	@Test
	public void twoVariantsDiffOffset() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		assertEquals(seq, sut.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(0, "AK",
																	2, "GR");
		
		assertEquals(expectedSnps,  sut.getVariants());
	}
	
	@Test
	public void adaptNoGaps() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		VariantProteinSequence adapted = sut.adapt(seq);
		
		assertEquals(seq, adapted.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(0, "AK",
																	2, "GR");
		
		assertEquals(expectedSnps,  adapted.getVariants());
	}
	@Test
	public void adaptGapsDownstreamOnlyShouldNotShiftAnyVariants() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		ProteinSequence gappedSeq = ProteinSequence.of("ACGT--");
		VariantProteinSequence adapted = sut.adapt(gappedSeq);
		
		assertEquals(gappedSeq, adapted.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(0, "AK",
																	2, "GR");
		
		assertEquals(expectedSnps,  adapted.getVariants());
	}
	
	@Test
	public void adaptGapsUpstreamOnlyShouldShiftAllVariants() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		ProteinSequence gappedSeq = ProteinSequence.of("-ACGT");
		VariantProteinSequence adapted = sut.adapt(gappedSeq);
		
		assertEquals(gappedSeq, adapted.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(1, "AK",
																	3, "GR");
		
		assertEquals(expectedSnps,  adapted.getVariants());
	}
	@Test
	public void adaptGapsInMiddleShouldShiftAllDownstreamVariants() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		ProteinSequence gappedSeq = ProteinSequence.of("AC-GT");
		VariantProteinSequence adapted = sut.adapt(gappedSeq);
		
		assertEquals(gappedSeq, adapted.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(0, "AK",
																	3, "GR");
		
		assertEquals(expectedSnps,  adapted.getVariants());
	}
	@Test
	public void adaptGapsSeveralInMiddleShouldShiftAllDownstreamVariants() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		ProteinSequence gappedSeq = ProteinSequence.of("AC---GT");
		VariantProteinSequence adapted = sut.adapt(gappedSeq);
		
		assertEquals(gappedSeq, adapted.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(0, "AK",
																	5, "GR");
		
		assertEquals(expectedSnps,  adapted.getVariants());
	}
	@Test
	public void adaptGapsSeveralUpAndDownstream() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		ProteinSequence gappedSeq = ProteinSequence.of("-AC---GT--");
		VariantProteinSequence adapted = sut.adapt(gappedSeq);
		
		assertEquals(gappedSeq, adapted.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(1, "AK",
																	6, "GR");
		
		assertEquals(expectedSnps,  adapted.getVariants());
	}
	@Test
	public void adaptGapsMultipleUpstreamOnlyShouldShiftAllVariants() {
		ProteinSequence seq = ProteinSequence.of("ACGT");
		
		VariantProteinSequence sut = new VariantProteinSequence.Builder(seq)
				.variant(0, AminoAcid.parse('K'))
				.variant(2, AminoAcid.parse('R'))
				.build();
		
		ProteinSequence gappedSeq = ProteinSequence.of("--ACGT");
		VariantProteinSequence adapted = sut.adapt(gappedSeq);
		
		assertEquals(gappedSeq, adapted.getProteinSequence());
		Map<Integer, Map<AminoAcid, SNP>> expectedSnps = expected(2, "AK",
																	4, "GR");
		
		assertEquals(expectedSnps,  adapted.getVariants());
	}
	
	private static Map<AminoAcid, SNP> expectedSNPMap(int offset, ProteinSequence allVariants){
		Map<AminoAcid, SNP> map = new EnumMap<>(AminoAcid.class);
		double percent = 1D/ ((double) allVariants.getLength());
		Iterator<AminoAcid> iter = allVariants.iterator();
		AminoAcid majority = iter.next();
		map.put(majority, new SNP(majority, percent, true));
		while(iter.hasNext()) {
			AminoAcid v = iter.next();
			map.put(v, new SNP(v, percent, false));
		}
		
		return map;
	}
	private static Map<Integer, Map<AminoAcid, SNP>> expected(int offset, String allVariants){
		Map<Integer, Map<AminoAcid, SNP>> allSnps = new ConcurrentHashMap<>();
		allSnps.put(offset, expectedSNPMap(offset, ProteinSequence.of(allVariants)));
		
		return allSnps;
	}
	private static Map<Integer, Map<AminoAcid, SNP>> expected(int offset1, String allVariants1,
			int offset2, String allVariants2){
		Map<Integer, Map<AminoAcid, SNP>> allSnps = new ConcurrentHashMap<>();
		allSnps.put(offset1, expectedSNPMap(offset1, ProteinSequence.of(allVariants1)));
		allSnps.put(offset2, expectedSNPMap(offset2, ProteinSequence.of(allVariants2)));
		
		return allSnps;
	}
}
