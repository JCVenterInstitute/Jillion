package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code DefaultQualityFastaDataStoreBuilder} is a {@link QualitySequenceFastaDataStoreBuilder}
 * that stores all {@link QualitySequenceFastaRecord} added to it via the {@link #addFastaRecord(QualitySequenceFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public class DefaultQualityFastaDataStoreBuilder implements QualitySequenceFastaDataStoreBuilder{

	private final Map<String, QualitySequenceFastaRecord> map = new LinkedHashMap<String, QualitySequenceFastaRecord>();
	@Override
	public QualitySequenceFastaDataStore build() {
		return new DefaultQualityFastaDataStoreImpl(map);
	}

	@Override
	public QualitySequenceFastaDataStoreBuilder addFastaRecord(
			QualitySequenceFastaRecord fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
	
	private static class DefaultQualityFastaDataStoreImpl implements QualitySequenceFastaDataStore{
		private final DataStore<QualitySequenceFastaRecord> delegate;
		public DefaultQualityFastaDataStoreImpl(Map<String, QualitySequenceFastaRecord> map){
			delegate = MapDataStoreAdapter.adapt(map);
		}
		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
		}

		@Override
		public QualitySequenceFastaRecord get(String id) throws DataStoreException {
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
		public StreamingIterator<QualitySequenceFastaRecord> iterator() throws DataStoreException {
			return delegate.iterator();
		}
		
	}

}
