package org.jcvi.common.core.symbol.qual;

public class TestRunLengthEncodedQualitySequenceImpl extends AbstractTestQualitySequence{

	@Override
	protected QualitySequence create(byte[] qualities) {
		return new RunLengthEncodedQualitySequence(
				RunLengthEncodedQualityCodec.INSTANCE.encode(qualities));
	}

}
