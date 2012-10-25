package org.jcvi.common.core.seq.fastx.fastq;

import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;

public class TestDefaultFastqFileDataStoreMultilineGuessCodec extends TestAbstractMultiLineFastqRecordsInDataStore{

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec) throws IOException {
		FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(fastq);
    	assertSame(codec ,qualityCodec);
		return DefaultFastqFileDataStore.create(fastq,codec);
	}

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec, DataStoreFilter filter)
			throws IOException {
		FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(fastq);
    	assertSame(codec ,qualityCodec);
		return DefaultFastqFileDataStore.create(fastq, filter, codec);
	}

}
