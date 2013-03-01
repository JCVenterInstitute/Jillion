package org.jcvi.common.examples;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class UngappingSequence {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NucleotideSequence originalSequence = null;
				
		NucleotideSequence sequence = new NucleotideSequenceBuilder(originalSequence)
											.reverseComplement()
											.build();

	}

}
