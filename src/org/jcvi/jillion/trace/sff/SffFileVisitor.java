package org.jcvi.jillion.trace.sff;


public interface SffFileVisitor {

	void visitHeader(SffFileParserCallback callback, SffCommonHeader header);
	
	SffFileReadVisitor visitRead(SffFileParserCallback callback, SffReadHeader readHeader);
	
	void end();
}
