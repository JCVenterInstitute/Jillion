package org.jcvi.common.annotation.hmm;

import java.util.Set;

import org.jcvi.common.core.symbol.Symbol;

public interface Hmm<S extends Symbol> {

	int getNumberOfStates();
	/**
	 * Get the probability of transistioning
	 * from state "from" to the state "to".
	 * @param from
	 * @param to
	 * @return the probability as a double between 0 and 1 inclusive.
	 */
	double getTransitionProbabilityOf(int from, int to);
	
	HmmState<S> getState(int i);
	/**
	 * Get all the HmmStates we can transition from the given state.
	 * @param state
	 * @return
	 */
	Set<HmmState<S>> getTransitionStatesFrom(HmmState<S> state);
	/**
	 * Get all the HmmStates that can emit the given symbol.
	 * @param symbol
	 * @return
	 */
	Set<HmmState<S>> getEmissionStatesFor(S symbol);
}
