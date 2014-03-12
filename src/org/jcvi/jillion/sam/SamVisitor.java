package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.header.SamHeader;

public interface SamVisitor {

	void visitHeader(SamHeader header);
	
	void visitRecord(SamRecord record);
	
	void visitRecord(SamRecord record, VirtualFileOffset start, VirtualFileOffset end);

	void visitEnd();
}
