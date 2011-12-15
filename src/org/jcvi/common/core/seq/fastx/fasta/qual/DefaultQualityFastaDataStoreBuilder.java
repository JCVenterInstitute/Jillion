package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code DefaultQualityFastaDataStoreBuilder} is a {@link QualityFastaDataStoreBuilder}
 * that stores all {@link QualityFastaRecord} added to it via the {@link #addFastaRecord(QualityFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public class DefaultQualityFastaDataStoreBuilder implements QualityFastaDataStoreBuilder{

	private final Map<String, QualityFastaRecord> map = new LinkedHashMap<String, QualityFastaRecord>();
	@Override
	public QualityFastaDataStore build() {
		return new DefaultQualityFastaDataStoreImpl(map);
	}

	@Override
	public QualityFastaDataStoreBuilder addFastaRecord(
			QualityFastaRecord fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
	
	private static class DefaultQualityFastaDataStoreImpl implements QualityFastaDataStore{
		private final DataStore<QualityFastaRecord> delegate;
		public DefaultQualityFastaDataStoreImpl(Map<String, QualityFastaRecord> map){
			delegate = new SimpleDataStore<QualityFastaRecord>(map);
		}
		@Override
		public CloseableIterator<String> getIds() throws DataStoreException {
			return delegate.getIds();
		}

		@Override
		public QualityFastaRecord get(String id) throws DataStoreException {
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
		public CloseableIterator<QualityFastaRecord> iterator() {
			return delegate.iterator();
		}
		
	}

}
