package org.jcvi.common.core.seq.trace.sanger;

import org.jcvi.jillion.core.Sequence;

public interface PositionSequence extends Sequence<Position>{

	short[] toArray();
}
