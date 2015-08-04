package org.jcvi.jillion.core.residue.nt;


public class TestDefaultNucleotideSequenceIteratorSubRange extends AbstractTestSequenceIteratorSubRange{

	@Override
	protected NucleotideSequence createSequence(String seqString) {
		return new NucleotideSequenceBuilder(seqString).build();
	}

}
