package org.jcvi.jillion.sam;

interface IndexerCallback{
	void encodedIndex(VirtualFileOffset start, VirtualFileOffset end);
}