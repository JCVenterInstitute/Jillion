package org.jcvi.jillion.assembly.util.slice;

import org.jcvi.jillion.core.Sequence;

public interface VariableWidthSliceMap<T, S extends Sequence<T>> {

	VariableWidthSlice<T, S> getSlice(int offset);

	int getConsensusLength();
	
	int getNumberOfSlices();
}
