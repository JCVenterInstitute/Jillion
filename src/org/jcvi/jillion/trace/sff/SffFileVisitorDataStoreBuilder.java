package org.jcvi.jillion.trace.sff;


public interface SffFileVisitorDataStoreBuilder extends FlowgramDataStoreBuilder, SffFileVisitor{

	@Override
	SffFileVisitorDataStoreBuilder addFlowgram(Flowgram flowgram);
}
