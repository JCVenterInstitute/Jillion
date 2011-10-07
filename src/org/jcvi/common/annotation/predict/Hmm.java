package org.jcvi.common.annotation.predict;

import java.util.Set;

import org.jcvi.common.core.symbol.Symbol;

public interface Hmm<S extends Symbol> {

	int getNumberOfStates();
	
	double getTransitionProbabilityOf(int from, int to);
	
	HmmState<S> getState(int i);
	
	Set<HmmState<S>> getTransitionStatesFrom(HmmState<S> state);
	
	Set<HmmState<S>> getEmissionStatesFor(S symbol);
}
