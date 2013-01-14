package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.trace.fastq.DefaultFastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqUtil;

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
