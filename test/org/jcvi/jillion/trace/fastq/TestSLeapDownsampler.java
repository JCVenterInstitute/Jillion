package org.jcvi.jillion.trace.fastq;

public class TestSLeapDownsampler extends AbstractTestDownsampler {

	@Override
	protected FastqDownsampler createSut(int reserviorSize) {
		return FastqDownsamplers.sLeap(reserviorSize, 350);
	}

}
