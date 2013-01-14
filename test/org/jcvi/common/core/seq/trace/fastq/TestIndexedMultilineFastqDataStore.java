package org.jcvi.common.core.seq.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.trace.fastq.FastqDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.IndexedFastqFileDataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;

public class TestIndexedMultilineFastqDataStore extends TestAbstractMultiLineFastqRecordsInDataStore{

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec) throws IOException {

		return IndexedFastqFileDataStore.create(fastq, qualityCodec);
	}

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec, DataStoreFilter filter)
			throws IOException {
		return IndexedFastqFileDataStore.create(fastq, qualityCodec, filter);
	}

}
