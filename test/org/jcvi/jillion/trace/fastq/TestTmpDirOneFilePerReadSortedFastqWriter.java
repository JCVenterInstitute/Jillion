package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.util.Comparator;

public class TestTmpDirOneFilePerReadSortedFastqWriter extends AbstractTestSortedFastqWriter{

    @Override
    protected void addSortStrategy(FastqWriterBuilder builder,
            Comparator<FastqRecord> comparator) throws IOException {
        builder.sort(comparator, 1, tmpDir.newFolder());
        
    }

}
