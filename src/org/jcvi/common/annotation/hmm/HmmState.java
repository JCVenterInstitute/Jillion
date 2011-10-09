package org.jcvi.common.annotation.hmm;

import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.common.core.symbol.Symbol;

public interface HmmState<S extends Symbol> extends Comparable<HmmState<S>>{

	int getIndex();
	
	double getProbabilityOf(S symbol);
	
	Set<Entry<S, Double>> getEntrySet();
}
