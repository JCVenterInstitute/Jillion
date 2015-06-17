package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;

public class TestReferenceEncodedSequenceIteratorSubRange extends AbstractTestSequenceIteratorSubRange{

	@Override
	protected NucleotideSequence createSequence(String seqString) {		
		
		
		NucleotideSequence ref = new NucleotideSequenceBuilder(seqString.length() + 100)
										.append(seqString)
										.append(NucleotideSequenceTestUtil.createRandom(100))
										.build();
		
		return new NucleotideSequenceBuilder(seqString)
												.setReferenceHint(ref, 0)
												.buildReferenceEncodedNucleotideSequence();
	}
}
