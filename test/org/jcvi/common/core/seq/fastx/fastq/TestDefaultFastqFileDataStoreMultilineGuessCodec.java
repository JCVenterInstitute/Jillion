package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.fastx.FastXFilter;

public class TestDefaultFastqFileDataStoreMultilineGuessCodec extends TestAbstractMultiLineFastqRecordsInDataStore{

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec) throws IOException {

		return DefaultFastqFileDataStore.create(fastq);
	}

	@Override
	protected FastqDataStore createFastqDataStoreFor(File fastq,
			FastqQualityCodec qualityCodec, FastXFilter filter)
			throws IOException {
		return DefaultFastqFileDataStore.create(fastq, filter, null);
	}

}
