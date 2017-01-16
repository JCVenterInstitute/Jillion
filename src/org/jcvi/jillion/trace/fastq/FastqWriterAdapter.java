package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

/**
 * FastqWriter implementation that adapts each fastq record
 * before passing it to the delegate writer.
 * 
 * Used by {@link FastqWriter#adapt(FastqWriter, Function)}
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
final class FastqWriterAdapter implements FastqWriter{

    private final FastqWriter delegate;
    private final Function<FastqRecord, FastqRecord> adapterFunction;
    
    public FastqWriterAdapter(FastqWriter delegate,
            Function<FastqRecord, FastqRecord> adapterFunction) {
        this.delegate = Objects.requireNonNull(delegate);
        this.adapterFunction = Objects.requireNonNull(adapterFunction);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void write(FastqRecord record) throws IOException {
        FastqRecord newRecord = adapterFunction.apply(record);
        if(newRecord !=null){
            delegate.write(record);
        }
        
    }
    

}
