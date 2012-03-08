package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.util.iter.CloseableIterator;

public class DefaultSffDataStoreBuilder implements SffDataStoreBuilder{

	private final Map<String, Flowgram> map = new LinkedHashMap<String, Flowgram>();
	@Override
	public SffDataStore build() {
		return new DefaultSffDataStoreImpl(new SimpleDataStore<Flowgram>(map));
	}

	@Override
	public SffDataStoreBuilder addFlowgram(Flowgram flowgram) {
		map.put(flowgram.getId(), flowgram);
		return this;
	}
	
	private final class DefaultSffDataStoreImpl implements SffDataStore{

		private final DataStore<Flowgram> delegate;
		
		public DefaultSffDataStoreImpl(DataStore<Flowgram> delegate) {
			this.delegate = delegate;
		}

		@Override
		public CloseableIterator<String> getIds() throws DataStoreException {
			return delegate.getIds();
		}

		@Override
		public Flowgram get(String id) throws DataStoreException {
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
		public CloseableIterator<Flowgram> iterator() {
			return delegate.iterator();
		}
		
	}

}
