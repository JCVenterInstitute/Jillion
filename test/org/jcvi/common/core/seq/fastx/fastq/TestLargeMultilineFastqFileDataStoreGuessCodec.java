package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.fastx.FastXFilter;
import static org.junit.Assert.*;
public class TestLargeMultilineFastqFileDataStoreGuessCodec extends TestAbstractMultiLineFastqRecordsInDataStore{

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec) throws IOException {
		FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(fastq);
		assertSame(codec,qualityCodec);
		return LargeFastqFileDataStore.create(fastq, codec);
	}

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec, FastXFilter filter)
			throws IOException {
		FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(fastq);
		assertSame(codec,qualityCodec);
		return LargeFastqFileDataStore.create(fastq, filter,codec);
	}

}
