package org.jcvi.jillion.assembly.util.slice;

import java.util.List;
import java.util.stream.Stream;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

public interface VariableWidthSlice<T> {

	Stream<VariableWidthSliceElement<T>> elements();
	
	int getCoverageDepth();
	
	int getSliceLength();
	
	int getCountFor(List<Nucleotide> sliceElementSeq);
}
