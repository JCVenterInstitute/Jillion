package org.jcvi.jillion.internal.sam;

import org.jcvi.jillion.sam.VirtualFileOffset;

public interface IndexerCallback{
	void encodedIndex(VirtualFileOffset start, VirtualFileOffset end);
}