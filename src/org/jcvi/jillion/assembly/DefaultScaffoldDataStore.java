package org.jcvi.jillion.assembly;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.MapUtil;

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
			return DataStoreUtil.adapt(ScaffoldDataStore.class, scaffolds);
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
}
