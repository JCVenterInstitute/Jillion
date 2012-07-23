package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStoreBuilder;
import org.jcvi.common.core.util.iter.CloseableIterator;

public final class DefaultSffDataStoreBuilder implements FlowgramDataStoreBuilder{

	
	private final Map<String, Flowgram> map;
	
	public DefaultSffDataStoreBuilder(){
		map = new LinkedHashMap<String, Flowgram>();
	}
	
	@Override
	public FlowgramDataStore build() {
		return new DefaultSffDataStoreImpl(MapDataStoreAdapter.adapt(map));
	}

	@Override
	public FlowgramDataStoreBuilder addFlowgram(Flowgram flowgram) {
		map.put(flowgram.getId(), flowgram);
		return this;
	}
	
	private static final class DefaultSffDataStoreImpl implements FlowgramDataStore{

		private final DataStore<Flowgram> delegate;
		
		public DefaultSffDataStoreImpl(DataStore<Flowgram> delegate) {
			this.delegate = delegate;
		}

		@Override
		public CloseableIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
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
		public CloseableIterator<Flowgram> iterator() throws DataStoreException {
			return delegate.iterator();
		}
		
	}

}
