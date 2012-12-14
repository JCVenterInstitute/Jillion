package org.jcvi.common.core.seq.trace.sff;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStoreBuilder;

public interface SffFileVisitorDataStoreBuilder extends FlowgramDataStoreBuilder, SffFileVisitor{

	@Override
	SffFileVisitorDataStoreBuilder addFlowgram(Flowgram flowgram);
}
