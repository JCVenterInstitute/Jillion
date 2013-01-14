package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.util.Builder;

public interface FlowgramDataStoreBuilder extends Builder<FlowgramDataStore> {

	FlowgramDataStoreBuilder addFlowgram(Flowgram flowgram);
}
