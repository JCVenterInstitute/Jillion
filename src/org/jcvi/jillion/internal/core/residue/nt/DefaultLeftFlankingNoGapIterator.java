package org.jcvi.jillion.internal.core.residue.nt;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;

public class DefaultLeftFlankingNoGapIterator implements OfInt{

	private int currentOffset;
	
	public DefaultLeftFlankingNoGapIterator(int startOffset) {
		this.currentOffset = startOffset;
	}

	@Override
	public boolean hasNext() {
		return currentOffset >=0;
	}

	@Override
	public int nextInt() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		return currentOffset--;
	}

}
