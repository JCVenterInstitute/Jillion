package org.jcvi.common.core.seq.trace.sff;


public interface SffFileVisitorDataStoreBuilder extends FlowgramDataStoreBuilder, SffFileVisitor{

	@Override
	SffFileVisitorDataStoreBuilder addFlowgram(Flowgram flowgram);
}
