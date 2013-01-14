package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.trace.fastq.DefaultFastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;

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
