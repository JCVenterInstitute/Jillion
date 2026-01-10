package org.jcvi.jillion.align;

import org.jcvi.jillion.align.IndelDetector.Indel;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

@RunWith(Parameterized.class)
public class TestVariantNucleotideIndelDetector extends DefaultIndelDetectorTest<Nucleotide, VariantNucleotideSequence, VariantNucleotideSequence.Builder> {

	public TestVariantNucleotideIndelDetector(String ignored, String a, String b, List<Indel> expected) {
		super(ignored, a, b, expected);
	}


	@Override
	protected VariantNucleotideSequence toSequence(String s) {
		return VariantNucleotideSequence.of(NucleotideSequence.of(s));
	}

}
