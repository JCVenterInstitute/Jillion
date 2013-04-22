package org.jcvi.jillion.trace.sanger.phd;

public interface PhdBallVisitor2 {
	/**
	 * Optional comment at the beginning
	 * of new versions of phd.ball files.
	 * The comment is often the path to the 
	 * corresponding fastq file.
	 * @param comment
	 */
	void visitFileComment(String comment);
	
	PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id);
	
	PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id, int version);
	
	PhdWholeReadItemVisitor visitReadTag();
	
	void visitEnd();
	
	void halted();
}
