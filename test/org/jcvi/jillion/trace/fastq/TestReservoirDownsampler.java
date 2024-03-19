package org.jcvi.jillion.trace.fastq;

public class TestReservoirDownsampler extends AbstractTestDownsampler {

	@Override
	protected FastqDownsampler createSut(int reserviorSize) {
		return FastqDownsamplers.reservoir(reserviorSize);
	}

}
