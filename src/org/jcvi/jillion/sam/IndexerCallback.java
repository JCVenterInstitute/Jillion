package org.jcvi.jillion.sam;

public interface IndexerCallback{
	void encodedIndex(long compressedStart, int uncompressedStart,
					  long compressedEnd, int uncompressedEnd);
}