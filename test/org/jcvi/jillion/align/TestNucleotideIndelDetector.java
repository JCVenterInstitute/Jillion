package org.jcvi.jillion.align;

import java.util.List;

import org.jcvi.jillion.align.IndelDetector.Indel;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class TestNucleotideIndelDetector extends DefaultIndelDetectorTest<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder> {

	public TestNucleotideIndelDetector(String ignored, String a, String b, List<Indel> expected) {
		super(ignored, a, b, expected);
	}


	@Override
	protected NucleotideSequence toSequence(String s) {
		return NucleotideSequence.of(s);
	}

}
