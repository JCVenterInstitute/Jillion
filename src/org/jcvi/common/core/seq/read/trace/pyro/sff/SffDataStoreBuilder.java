package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.util.Builder;

public interface SffDataStoreBuilder extends Builder<SffDataStore> {

	SffDataStoreBuilder addFlowgram(Flowgram flowgram);
}
