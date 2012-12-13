package org.jcvi.common.core.seq.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.trace.fastq.FastqDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.FastqUtil;
import org.jcvi.common.core.seq.trace.fastq.LargeFastqFileDataStore;

public class TestLargeFastqFileDataStoreGuessCodec extends AbstractTestFastQFileDataStore{

    @Override
    protected FastqDataStore createFastQFileDataStore(File file,
            FastqQualityCodec qualityCodec) throws IOException {
    	FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(file);
        return LargeFastqFileDataStore.create(file,codec);
    }

}
