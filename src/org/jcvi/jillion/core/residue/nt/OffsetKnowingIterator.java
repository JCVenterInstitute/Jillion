package org.jcvi.jillion.core.residue.nt;

import java.util.Iterator;

class OffsetKnowingIterator implements Iterator<Nucleotide>{
	private int increment;
	int nextOffset;
	private Iterator<Nucleotide> delegate;
	
	public static OffsetKnowingIterator createFwd(Iterator<Nucleotide> iter, int startOffset) {
		return new OffsetKnowingIterator(iter, startOffset, 1);
	}
	public static OffsetKnowingIterator createRev(Iterator<Nucleotide> iter, int startOffset) {
		return new OffsetKnowingIterator(iter, startOffset, -1);
	}
	private OffsetKnowingIterator(Iterator<Nucleotide> iter, int startOffset, int increment) {
		this.delegate = iter;
		this.nextOffset = startOffset;
		this.increment= increment;
	}
	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}
	@Override
	public Nucleotide next() {
		Nucleotide n = delegate.next();
		nextOffset+=increment;
		return n;
	}
	
	public int getNextOffset() {
		return nextOffset;
	}
}