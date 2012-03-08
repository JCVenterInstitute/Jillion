package org.jcvi.common.core.seq.fastx.fasta.nuc;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code DefaultNucleotideFastaDataStoreBuilder} is a {@link NucleotideSequenceFastaDataStoreBuilder}
 * that stores all {@link NucleotideSequenceFastaRecord} added to it via the {@link #addFastaRecord(NucleotideSequenceFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public final class DefaultNucleotideSequenceFastaDataStoreBuilder implements NucleotideSequenceFastaDataStoreBuilder{

	private final Map<String, NucleotideSequenceFastaRecord> map = new LinkedHashMap<String, NucleotideSequenceFastaRecord>();
	@Override
	public NucleotideSequenceFastaDataStore build() {
		return new NucleotideFastaDataStoreImpl(map);
	}

	@Override
	public DefaultNucleotideSequenceFastaDataStoreBuilder addFastaRecord(
			NucleotideSequenceFastaRecord fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
	
	private static final class NucleotideFastaDataStoreImpl implements NucleotideSequenceFastaDataStore{
		private final DataStore<NucleotideSequenceFastaRecord> delegate;
		private NucleotideFastaDataStoreImpl(Map<String, NucleotideSequenceFastaRecord> map){
			delegate = new SimpleDataStore<NucleotideSequenceFastaRecord>(map);
		}
		@Override
		public CloseableIterator<String> getIds() throws DataStoreException {
			return delegate.getIds();
		}

		@Override
		public NucleotideSequenceFastaRecord get(String id)
				throws DataStoreException {
			return delegate.get(id);
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			return delegate.contains(id);
		}

		@Override
		public int size() throws DataStoreException {
			return delegate.size();
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
		public CloseableIterator<NucleotideSequenceFastaRecord> iterator() {
			return delegate.iterator();
		}
		
	}

}
