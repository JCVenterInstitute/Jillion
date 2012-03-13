package org.jcvi.common.core.assembly.asm.atac;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Range;

public interface AtacMatch {

	public enum MatchType{
		UNGAPPED,
		GAPPED,
		CLUSTERED
	}
	MatchType getMatchType();
	
	String getInstanceIndex();
	
	long getParentIndex();
	
	String getFirstAssemblyId();
	Range getFirstAssemblyRange();
	
	String getSecondAssemblyId();
	DirectedRange getSecondAssemblyDirectedRange();
}
