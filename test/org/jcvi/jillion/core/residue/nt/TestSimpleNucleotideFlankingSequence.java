package org.jcvi.jillion.core.residue.nt;

public class TestSimpleNucleotideFlankingSequence extends AbstractTestNucleotideFlankingSequence {

	@Override
	protected NucleotideSequence create(String seq) {
		return new NucleotideSequenceBuilder(seq).turnOffDataCompression(true).build();
	}

}
