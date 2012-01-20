package org.jcvi.common.core.align;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.util.Builder;

public interface SequenceAlignmentBuilder<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R, S>> extends Builder<A> {

	SequenceAlignmentBuilder<R,S,A> addMatch(R match);
	SequenceAlignmentBuilder<R,S,A> addMatches(Iterable<R> matches);
	
	SequenceAlignmentBuilder<R, S,A> addMatches(String matchedSequence);
	
	SequenceAlignmentBuilder<R,S,A> addMismatch(R query, R subject);
	SequenceAlignmentBuilder<R,S,A> addGap(R query, R subject);
	SequenceAlignmentBuilder<R, S,A> addGap(char query, char subject);
	
	SequenceAlignmentBuilder<R,S,A> reverse();
	
	
	
}
