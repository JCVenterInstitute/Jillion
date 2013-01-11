package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.RunLengthEncodedQualityCodec;
import org.jcvi.jillion.core.qual.RunLengthEncodedQualitySequence;

public class TestRunLengthEncodedQualitySequenceImpl extends AbstractTestQualitySequence{

	@Override
	protected QualitySequence create(byte[] qualities) {
		return new RunLengthEncodedQualitySequence(
				RunLengthEncodedQualityCodec.INSTANCE.encode(qualities));
	}

}
