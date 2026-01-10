package org.jcvi.jillion.align;

import java.util.List;

import org.jcvi.jillion.align.IndelDetector.Indel;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;

public class TestProteinIndelDetector extends DefaultIndelDetectorTest<AminoAcid, ProteinSequence, ProteinSequenceBuilder> {

	public TestProteinIndelDetector(String ignored, String a, String b, List<Indel> expected) {
		super(ignored, a, b, expected);
	}

	@Override
	protected ProteinSequence toSequence(String s) {
		return ProteinSequence.of(s);
	}
}
