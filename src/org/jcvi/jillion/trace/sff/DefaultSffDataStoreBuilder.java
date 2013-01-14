package org.jcvi.jillion.trace.sff;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreUtil;

final class DefaultSffDataStoreBuilder implements FlowgramDataStoreBuilder{

	
	private final Map<String, Flowgram> map;
	
	public DefaultSffDataStoreBuilder(){
		map = new LinkedHashMap<String, Flowgram>();
	}
	
	@Override
	public FlowgramDataStore build() {
		return DataStoreUtil.adapt(FlowgramDataStore.class,map);
	}

	@Override
	public FlowgramDataStoreBuilder addFlowgram(Flowgram flowgram) {
		map.put(flowgram.getId(), flowgram);
		return this;
	}
}
