package org.jcvi.jillion.assembly.consed.phd;

public interface PhdWholeReadItemVisitor {

	void visitLine(String line);
	
	void visitEnd();

	void halted();
}
