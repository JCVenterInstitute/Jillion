package org.jcvi.jillion.core.residue.nt;


public class TestReferenceEncodedSequenceIteratorSubRange extends AbstractTestSequenceIteratorSubRange{

	@Override
	protected NucleotideSequence createSequence(String seqString) {		
		
		
		NucleotideSequence ref = new NucleotideSequenceBuilder(seqString.length() + 100)
										.append(seqString)
										.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
										.build();
		
		return new NucleotideSequenceBuilder(seqString)
												.setReferenceHint(ref, 0)
												.buildReferenceEncodedNucleotideSequence();
	}
}
