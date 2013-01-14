package org.jcvi.common.core.seq.trace.sff;

import org.jcvi.jillion.core.util.Builder;

public interface FlowgramDataStoreBuilder extends Builder<FlowgramDataStore> {

	FlowgramDataStoreBuilder addFlowgram(Flowgram flowgram);
}
