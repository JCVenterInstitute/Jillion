package org.jcvi.jillion.trace.sanger.phd;

public interface PhdWholeReadItemVisitor {

	void visitLine(String line);
	
	void visitEnd();

	void halted();
}
