package org.jcvi.jillion.internal.core.residue.nt;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;

public class DefaultRightFlankingNoGapIterator implements OfInt{

	private int currentOffset;
	private final int lastOffset;
	
	public DefaultRightFlankingNoGapIterator(int startOffset, int lastOffset) {
		this.currentOffset = startOffset;
		this.lastOffset = lastOffset;
	}

	@Override
	public boolean hasNext() {
		return currentOffset <=lastOffset;
	}

	@Override
	public int nextInt() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		return currentOffset++;
	}

}
