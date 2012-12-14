package org.jcvi.common.core.seq.trace.sanger;

import org.jcvi.common.core.symbol.Sequence;

public interface PositionSequence extends Sequence<Position>{

	short[] toArray();
}
