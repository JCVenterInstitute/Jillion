package org.jcvi.common.core.seq.read.trace.pyro;

import org.jcvi.common.core.util.Builder;

public interface FlowgramDataStoreBuilder extends Builder<FlowgramDataStore> {

	FlowgramDataStoreBuilder addFlowgram(Flowgram flowgram);
}
