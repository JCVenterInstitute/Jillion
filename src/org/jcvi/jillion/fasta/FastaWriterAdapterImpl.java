package org.jcvi.jillion.fasta;

import java.io.IOException;
import java.util.Objects;

import org.jcvi.jillion.core.Sequence;

class FastaWriterAdapterImpl<S, T extends Sequence<S>, F extends FastaRecord<S, T>> implements FastaWriter<S, T, F> {

    private final FastaWriter<S, T, F> delegate;
    private final FastaRecordAdapter<S, T, F> adapter;
    
    public FastaWriterAdapterImpl(FastaWriter<S, T, F> delegate,
            FastaRecordAdapter<S, T, F> adapter) {
        this.delegate = Objects.requireNonNull(delegate);
        this.adapter = Objects.requireNonNull(adapter);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void write(String id, T sequence, String optionalComment)
            throws IOException {
        F adapted = adapter.adapt(id, sequence, optionalComment);
        if(adapted !=null){
            delegate.write(adapted);
        }
    }
    
}
