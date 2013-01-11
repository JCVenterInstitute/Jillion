package org.jcvi.common.annotation.hmm;

import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.core.Symbol;

public interface HmmState<S extends Symbol> extends Comparable<HmmState<S>>{
	/**
	 * Get the state index this HmmState is in for its model.
	 * An index of 0 is the initial AND final state.
	 * @return
	 */
	int getIndex();
	/**
	 * Get the probability of emitting the given
	 * sybmol
	 * @param symbol
	 * @return
	 */
	double getProbabilityOfEmitting(S symbol);
	
	Set<Entry<S, Double>> getEntrySet();
}
