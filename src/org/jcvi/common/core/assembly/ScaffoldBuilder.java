package org.jcvi.common.core.assembly;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.util.Builder;

public interface ScaffoldBuilder extends Builder<DefaultScaffold> {

	ScaffoldBuilder add(PlacedContig placedContig);

	ScaffoldBuilder add(String contigId, Range contigRange,
			Direction contigDirection);

	ScaffoldBuilder add(String contigId, Range contigRange);

	/**
	 * Shift all contigs in the scaffold so that the first
	 * contig will start at scaffold position 1.
	 * @param shiftContigs
	 * @return this
	 */
	ScaffoldBuilder shiftContigs(boolean shiftContigs);

	DefaultScaffold build();

}