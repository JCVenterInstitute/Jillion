package org.jcvi.jillion.assembly.util.slice;

import java.util.List;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Sequence;

public interface VariableWidthSlice<T, S extends Sequence<T>> {

	Stream<VariableWidthSliceElement<T>> elements();
	
	int getCoverageDepth();
	
	int getSliceLength();
	
	int getCountFor(List<T> sliceElementSeq);
	
	S getGappedReferenceSequence();
}
