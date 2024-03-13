package org.jcvi.jillion.core.residue.nt;

public class TestNucleotideFlankingSequence extends AbstractTestNucleotideFlankingSequence {

	@Override
	protected NucleotideSequence create(String seq) {
		return new NucleotideSequenceBuilder(seq).turnOffDataCompression(false).build();
	}

}
