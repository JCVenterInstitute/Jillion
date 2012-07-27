package org.jcvi.common.core.assembly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.util.MapUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;

public final class DefaultScaffoldDataStore {

	private DefaultScaffoldDataStore(){
		//private constructor.
	}
	public static ScaffoldDataStoreBuilder createBuilder(){
		return new DefaultScaffoldDataStoreBuilder();
	}
	
	private static final class DefaultScaffoldDataStoreBuilder implements ScaffoldDataStoreBuilder{
		private final Map<String, ScaffoldBuilder> builders = new HashMap<String, ScaffoldBuilder>();
		
		@Override
		public synchronized ScaffoldDataStore build() {
			int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(builders.size());
			Map<String, Scaffold> scaffolds = new HashMap<String, Scaffold>(mapSize);
			for(Entry<String, ScaffoldBuilder> entry : builders.entrySet()){
				scaffolds.put(entry.getKey(), entry.getValue().build());
			}
			return new ScaffoldDataStoreImpl(scaffolds);
		}

		@Override
		public synchronized ScaffoldDataStoreBuilder addScaffold(Scaffold scaffold) {
			String scaffoldId = scaffold.getId();
			if(!builders.containsKey(scaffoldId)){
				builders.put(scaffoldId, DefaultScaffold.createBuilder(scaffoldId));
			}
			for(PlacedContig placedContig: scaffold.getPlacedContigs()){
				builders.get(scaffoldId).add(placedContig);
			}
			return this;
		}

		@Override
		public synchronized ScaffoldDataStoreBuilder addPlacedContig(String scaffoldId,
				String contigId, DirectedRange directedRange) {
			if(!builders.containsKey(scaffoldId)){
				builders.put(scaffoldId, DefaultScaffold.createBuilder(scaffoldId));
			}
			builders.get(scaffoldId).add(contigId, directedRange.asRange(), directedRange.getDirection());
			return this;
		}
		
	}
	
	private static class ScaffoldDataStoreImpl implements ScaffoldDataStore{
		private final DataStore<Scaffold> delegate;
		
		public ScaffoldDataStoreImpl(Map<String, Scaffold> scaffolds){
			delegate = MapDataStoreAdapter.adapt(scaffolds);
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
		}

		@Override
		public Scaffold get(String id) throws DataStoreException {
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
		public StreamingIterator<Scaffold> iterator() throws DataStoreException {
			return delegate.iterator();
		}
		
		
	}
}
