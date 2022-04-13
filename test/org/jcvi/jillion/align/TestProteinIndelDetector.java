package org.jcvi.jillion.align;

import java.util.List;

import org.jcvi.jillion.align.IndelDetector.Indel;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;

public class TestProteinIndelDetector extends AbstractIndelDetectorTest<ProteinSequence>{

	public TestProteinIndelDetector(String ignored, String a, String b, List<Indel> expected) {
		super(ignored, a, b, expected);
	}

	@Override
	protected ProteinSequence toSequence(String s) {
		return ProteinSequence.of(s);
	}

	@Override
	protected IndelDetector<ProteinSequence> getDetectorInstance() {
		return new ProteinIndelDetector();
	}
}
