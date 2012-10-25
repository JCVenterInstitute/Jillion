package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;

public class TestDefaultMultiLineFastqRecordsInDataStore extends TestAbstractMultiLineFastqRecordsInDataStore{

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec) throws IOException {

		return DefaultFastqFileDataStore.create(fastq, qualityCodec);
	}

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec, DataStoreFilter filter)
			throws IOException {
		return DefaultFastqFileDataStore.create(fastq, filter, qualityCodec);
	}

}
