package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.FileNotFoundException;

public class TestLargeFastqFileDataStoreGuessCodec extends AbstractTestFastQFileDataStore{

    @Override
    protected FastqDataStore createFastQFileDataStore(File file,
            FastqQualityCodec qualityCodec) throws FileNotFoundException {
        return LargeFastqFileDataStore.create(file);
    }

}
