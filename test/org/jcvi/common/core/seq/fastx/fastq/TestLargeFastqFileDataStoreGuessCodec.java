package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

public class TestLargeFastqFileDataStoreGuessCodec extends AbstractTestFastQFileDataStore{

    @Override
    protected FastqDataStore createFastQFileDataStore(File file,
            FastqQualityCodec qualityCodec) throws IOException {
    	FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(file);
        return LargeFastqFileDataStore.create(file,codec);
    }

}
