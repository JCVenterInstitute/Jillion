package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.core.qual.DefaultQualitySymbolCodec;
import org.jcvi.jillion.core.qual.EncodedQualitySequence;
import org.jcvi.jillion.core.qual.QualitySequence;


public class TestDefaultQualitySequence extends AbstractTestQualitySequence{

	@Override
	protected QualitySequence create(byte[] qualities) {
		return new EncodedQualitySequence(DefaultQualitySymbolCodec.INSTANCE, qualities);
	}

}
