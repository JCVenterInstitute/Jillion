package org.jcvi.common.core.seq.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.trace.fastq.FastqDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.FastqUtil;
import org.jcvi.common.core.seq.trace.fastq.LargeFastqFileDataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;

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
			FastqQualityCodec qualityCodec, DataStoreFilter filter)
			throws IOException {
		FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(fastq);
		assertSame(codec,qualityCodec);
		return LargeFastqFileDataStore.create(fastq, filter,codec);
	}

}
