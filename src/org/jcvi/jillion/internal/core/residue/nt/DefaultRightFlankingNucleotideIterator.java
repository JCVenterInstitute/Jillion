package org.jcvi.jillion.internal.core.residue.nt;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfInt;

import org.jcvi.jillion.core.residue.nt.INucleotideSequence;

public class DefaultRightFlankingNucleotideIterator implements OfInt{

	private final INucleotideSequence<?, ?> seq;
	private int nextOffset;
	private final int lastOffset;
	
	public DefaultRightFlankingNucleotideIterator(INucleotideSequence<?, ?> seq, int gappedStartOffset) {
		this.seq= Objects.requireNonNull(seq);
		this.lastOffset = seq.getLeftFlankingNonGapOffsetFor((int) seq.getLength()-1);
		nextOffset = seq.getRightFlankingNonGapOffsetFor(gappedStartOffset);
	}
	@Override
	public boolean hasNext() {
		return nextOffset<=lastOffset;
	}

	@Override
	public int nextInt() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		int ret = nextOffset;
		nextOffset = seq.getRightFlankingNonGapOffsetFor(nextOffset+1);
		return ret;
	}

}
