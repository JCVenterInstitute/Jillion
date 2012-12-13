package org.jcvi.common.core.seq.trace.fastq;

import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.seq.trace.fastq.DefaultFastqFileDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.FastqUtil;

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
