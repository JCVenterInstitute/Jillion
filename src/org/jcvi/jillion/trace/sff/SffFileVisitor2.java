package org.jcvi.jillion.trace.sff;


public interface SffFileVisitor2 {

	void visitHeader(SffFileParserCallback callback, SffCommonHeader header);
	
	SffFileReadVisitor visitRead(SffFileParserCallback callback, SffReadHeader readHeader);
	
	void endSffFile();
}
