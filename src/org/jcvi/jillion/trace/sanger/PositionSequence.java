package org.jcvi.jillion.trace.sanger;

import org.jcvi.jillion.core.Sequence;

public interface PositionSequence extends Sequence<Position>{

	short[] toArray();
}
