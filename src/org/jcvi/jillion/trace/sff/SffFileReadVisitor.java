package org.jcvi.jillion.trace.sff;


public interface SffFileReadVisitor {

	void visitReadData(SffReadData readData);
	void visitEnd();
}
