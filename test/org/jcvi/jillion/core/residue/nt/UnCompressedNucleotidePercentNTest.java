package org.jcvi.jillion.core.residue.nt;

public class UnCompressedNucleotidePercentNTest extends AbstractTestNucleotidePercentNTests{

	@Override
	protected NucleotideSequence create(NucleotideSequenceBuilder builder) {
		return builder.turnOffDataCompression(true).build();
	}

}
