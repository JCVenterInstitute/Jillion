package org.jcvi.jillion.trace.sanger.phd;

public interface PhdWholeReadTagVisitor {

	void visitLine(String line);
	
	void visitEnd();

	void halted();
}
