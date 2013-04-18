package org.jcvi.jillion.trace.sanger.phd;

import java.util.Date;

import org.jcvi.jillion.core.Range;

public interface PhdReadTagVisitor2 {

	void visitType(String type);
	
	void visitSource(String source);
	
	void visitUngappedRange(Range ungappedRange);
	
	void visitDate(Date date);
	
	void visitComment(String comment);
	
	void visitFreeFormData(String data);
	
	void visitEnd();
	
	void halted();
}
