package org.jcvi.jillion.align;

import org.jcvi.jillion.align.IndelDetector.Indel;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

/**
 * These tests make sure
 * that the indel detector works when
 * comparing 2 different types of nucleotide sequence
 * (prior to 6.1 this wouldn't compile)
 */
@RunWith(Parameterized.class)
public class TestVariantNucleotideWithRefIndelDetector extends DefaultIndelDetectorTest<Nucleotide, VariantNucleotideSequence, VariantNucleotideSequence.Builder> {

	public TestVariantNucleotideWithRefIndelDetector(String ignored, String a, String b, List<Indel> expected) {
		super(ignored, a, b, expected);
	}

	@Override
	protected List<Indel> consumeArguments(String a, String b) {
		return getDetectorInstance().findIndels(NucleotideSequence.of(a), VariantNucleotideSequence.of(NucleotideSequence.of(b)));
	}

	@Override
	protected VariantNucleotideSequence toSequence(String s) {
		return VariantNucleotideSequence.of(NucleotideSequence.of(s));
	}

}
