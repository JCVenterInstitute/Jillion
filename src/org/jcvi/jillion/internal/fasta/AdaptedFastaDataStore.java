package org.jcvi.jillion.internal.fasta;

import java.io.IOException;
import java.util.Map;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.fasta.FastaRecord;
/**
 * Wrapper class that converts a Map of FastaRecords or a plain DataStore 
 * of FastaRecords into a {@link FastaDataStore} object.  This lets
 * proxy classes work with FastaDataStore specific methods that
 * aren't normally accessible with a plain DataStore object.
 * 
 * @author dkatzel
 *
 * @param <S>
 * @param <T>
 * @param <F>
 * 
 * @since 5.1
 */
public class AdaptedFastaDataStore<S,  T extends Sequence<S>, F extends FastaRecord<S,T>> implements FastaDataStore<S, T, F>{

	private final DataStore<F> delegate;
	
	
	public AdaptedFastaDataStore(Map<String, F> map) {
		this(DataStoreUtil.adapt(map));
	}
	public AdaptedFastaDataStore(DataStore<F> delegate) {
		this.delegate = delegate;
	}

	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return delegate.idIterator();
	}

	@Override
	public F get(String id) throws DataStoreException {
		return delegate.get(id);
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		return delegate.contains(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		return delegate.getNumberOfRecords();
	}

	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}

	@Override
	public StreamingIterator<F> iterator() throws DataStoreException {
		return delegate.iterator();
	}

	@Override
	public StreamingIterator<DataStoreEntry<F>> entryIterator() throws DataStoreException {
		return delegate.entryIterator();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

}
