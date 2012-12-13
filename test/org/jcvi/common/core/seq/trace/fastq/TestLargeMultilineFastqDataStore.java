package org.jcvi.common.core.seq.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.seq.trace.fastq.FastqDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.LargeFastqFileDataStore;

public class TestLargeMultilineFastqDataStore extends TestAbstractMultiLineFastqRecordsInDataStore{

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec) throws IOException {

		return LargeFastqFileDataStore.create(fastq, qualityCodec);
	}

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec, DataStoreFilter filter)
			throws IOException {
		return LargeFastqFileDataStore.create(fastq, filter, qualityCodec);
	}

}
