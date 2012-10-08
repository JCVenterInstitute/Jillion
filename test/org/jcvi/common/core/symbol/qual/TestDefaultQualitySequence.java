package org.jcvi.common.core.symbol.qual;


public class TestDefaultQualitySequence extends AbstractTestQualitySequence{

	@Override
	protected QualitySequence create(byte[] qualities) {
		return new EncodedQualitySequence(DefaultQualitySymbolCodec.INSTANCE, qualities);
	}

}
