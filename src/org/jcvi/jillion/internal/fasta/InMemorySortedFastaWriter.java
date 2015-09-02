package org.jcvi.jillion.internal.fasta;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaWriter;

public abstract class InMemorySortedFastaWriter<S, T extends Sequence<S>, F extends FastaRecord<S,T>> implements FastaWriter<S,T,F>{

	private final FastaWriter<S,T,F> writer;
	
	private final Set<F> sorted;
	private volatile boolean closed;
	
	public InMemorySortedFastaWriter(FastaWriter<S,T,F> writer, Comparator<F> comparator){
		Objects.requireNonNull(writer);
		Objects.requireNonNull(comparator);
		this.writer = writer;
		sorted = new TreeSet<F>(comparator);
	}
	@Override
	public void close() throws IOException {
		if(closed){
			return;
		}
		closed=true;
		try{
			for(F record : sorted){
				writer.write(record);
			}
		}finally{
			sorted.clear();
			IOUtil.closeAndIgnoreErrors(writer);
		}
	}
	
	private void checkNotClosed() throws IOException{
		if(closed){
			throw new IOException("writer is closed");
		}
	}

	@Override
	public void write(F record) throws IOException {
		checkNotClosed();
		sorted.add(record);
		
	}

	@Override
	public void write(String id, T sequence) throws IOException {
		write(id, sequence, null);
		
	}

	@Override
	public void write(String id, T sequence,String optionalComment)
			throws IOException {
		write(createRecord(id, sequence, optionalComment));
	}
	
	protected abstract F createRecord(String id, T sequence,String optionalComment);

}
