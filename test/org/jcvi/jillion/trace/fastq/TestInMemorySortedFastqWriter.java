package org.jcvi.jillion.trace.fastq;

import java.util.Comparator;

public class TestInMemorySortedFastqWriter extends AbstractTestSortedFastqWriter{

    @Override
    protected void addSortStrategy(FastqWriterBuilder builder, Comparator<FastqRecord> comparator) {
        builder.sortInMemoryOnly(comparator);
    }

}
