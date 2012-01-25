package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;

public interface PairwiseSequenceAlignment<R extends Residue, S extends Sequence<R>> extends SequenceAlignment<R, S> {

	float getScore();
}
