package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code DefaultPositionFastaDataStoreBuilder} is a {@link PositionFastaDataStoreBuilder}
 * that stores all {@link PositionFastaRecord} added to it via the {@link #addFastaRecord(PositionFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public class DefaultPositionFastaDataStoreBuilder implements PositionFastaDataStoreBuilder{

	private final Map<String, PositionFastaRecord<Sequence<ShortSymbol>>> map = new LinkedHashMap<String, PositionFastaRecord<Sequence<ShortSymbol>>>();
	@Override
	public PositionFastaDataStore build() {
		return new PositionFastaDataStoreImpl(map);
	}

	@Override
	public DefaultPositionFastaDataStoreBuilder addFastaRecord(
			PositionFastaRecord<Sequence<ShortSymbol>> fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
	
	private static class PositionFastaDataStoreImpl implements PositionFastaDataStore{
		private final DataStore<PositionFastaRecord<Sequence<ShortSymbol>>> delegate;
		private PositionFastaDataStoreImpl(Map<String, PositionFastaRecord<Sequence<ShortSymbol>>> map){
			delegate = new SimpleDataStore<PositionFastaRecord<Sequence<ShortSymbol>>>(map);
		}
		@Override
		public CloseableIterator<String> getIds() throws DataStoreException {
			return delegate.getIds();
		}

		@Override
		public PositionFastaRecord<Sequence<ShortSymbol>> get(String id)
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
		public CloseableIterator<PositionFastaRecord<Sequence<ShortSymbol>>> iterator() {
			return delegate.iterator();
		}
		
	}
}
