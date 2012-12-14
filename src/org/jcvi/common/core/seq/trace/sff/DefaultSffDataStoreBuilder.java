package org.jcvi.common.core.seq.trace.sff;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStoreUtil;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStoreBuilder;

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