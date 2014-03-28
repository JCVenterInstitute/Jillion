package org.jcvi.jillion.sam;

interface IndexerCallback{
	void encodedIndex(long compressedStart, int uncompressedStart,
					  long compressedEnd, int uncompressedEnd);
}