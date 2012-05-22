package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

public class TestDefaultFastqFileDataStoreGuessCodec extends AbstractTestFastQFileDataStore{
    @Override
    protected FastqDataStore createFastQFileDataStore(File file, FastqQualityCodec qualityCodec) throws IOException {
        return DefaultFastqFileDataStore.create(file);
    }

}
