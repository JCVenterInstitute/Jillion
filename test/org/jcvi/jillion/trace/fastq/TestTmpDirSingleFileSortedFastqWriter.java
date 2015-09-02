package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.util.Comparator;

public class TestTmpDirSingleFileSortedFastqWriter extends AbstractTestSortedFastqWriter{

    @Override
    protected void addSortStrategy(FastqWriterBuilder builder,
            Comparator<FastqRecord> comparator) throws IOException {
        builder.sort(comparator, 75, tmpDir.newFolder());
        
    }


}
