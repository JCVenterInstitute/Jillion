package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code DefaultNucleotideFastaDataStoreBuilder} is a {@link NucleotideSequenceFastaDataStoreBuilder}
 * that stores all {@link DefaultNucleotideSequenceFastaRecord} added to it via the {@link #addFastaRecord(DefaultNucleotideSequenceFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public final class DefaultNucleotideSequenceFastaDataStoreBuilder implements NucleotideSequenceFastaDataStoreBuilder{

	private final Map<String, DefaultNucleotideSequenceFastaRecord> map = new LinkedHashMap<String, DefaultNucleotideSequenceFastaRecord>();
	@Override
	public NucleotideSequenceFastaDataStore build() {
		return new NucleotideFastaDataStoreImpl(map);
	}

	@Override
	public DefaultNucleotideSequenceFastaDataStoreBuilder addFastaRecord(
			DefaultNucleotideSequenceFastaRecord fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
	
	private static final class NucleotideFastaDataStoreImpl implements NucleotideSequenceFastaDataStore{
		private final DataStore<DefaultNucleotideSequenceFastaRecord> delegate;
		private NucleotideFastaDataStoreImpl(Map<String, DefaultNucleotideSequenceFastaRecord> map){
			delegate = MapDataStoreAdapter.adapt(map);
		}
		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
		}

		@Override
		public DefaultNucleotideSequenceFastaRecord get(String id)
				throws DataStoreException {
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
		public boolean isClosed() throws DataStoreException {
			return delegate.isClosed();
		}

		@Override
		public void close() throws IOException {
			delegate.close();
			
		}

		@Override
		public StreamingIterator<DefaultNucleotideSequenceFastaRecord> iterator() throws DataStoreException {
			return delegate.iterator();
		}
		
	}

}
