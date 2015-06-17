package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;

public class TestDefaultNucleotideSequenceIteratorSubRange extends AbstractTestSequenceIteratorSubRange{

	@Override
	protected NucleotideSequence createSequence(String seqString) {
		return NucleotideSequenceTestUtil.create(seqString);
	}

}
