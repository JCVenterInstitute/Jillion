package org.jcvi.jillion.internal.core.residue.nt;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfInt;

import org.jcvi.jillion.core.residue.nt.INucleotideSequence;

public class DefaultLeftFlankingNucleotideIterator implements OfInt{

	private final INucleotideSequence<?, ?> seq;
	private int nextOffset;
	
	public DefaultLeftFlankingNucleotideIterator(INucleotideSequence<?, ?> seq, int gappedStartOffset) {
		this.seq= Objects.requireNonNull(seq);
		nextOffset = seq.getLeftFlankingNonGapOffsetFor(gappedStartOffset);
	}
	@Override
	public boolean hasNext() {
		return nextOffset>=0;
	}

	@Override
	public int nextInt() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		int ret = nextOffset;
		nextOffset = seq.getLeftFlankingNonGapOffsetFor(nextOffset-1);
		return ret;
	}

}
