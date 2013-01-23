package org.jcvi.jillion.trace.sff;


public interface SffFileReadVisitor {

	void visitReadData(SffFileParserCallback callback, SffReadData readData);
	void visitEndOfRead(SffFileParserCallback callback);
}
