package org.jcvi.jillion.core.residue.nt;

public class CompressedNucleotidePercentNTest extends AbstractTestNucleotidePercentNTests{

	@Override
	protected NucleotideSequence create(NucleotideSequenceBuilder builder) {
		return builder.turnOffDataCompression(false).build();
	}

}
