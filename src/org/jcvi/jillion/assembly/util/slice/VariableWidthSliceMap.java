package org.jcvi.jillion.assembly.util.slice;

public interface VariableWidthSliceMap<T> {

	VariableWidthSlice<T> getSlice(int offset);

	int getConsensusLength();
	
	int getNumberOfSlices();
}
