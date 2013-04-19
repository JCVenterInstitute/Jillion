package org.jcvi.jillion.trace.sanger.phd;

public class AbstractPhdBallVisitor2 implements PhdBallVisitor2 {

	@Override
	public void visitFileComment(String comment) {
		//no-op

	}

	@Override
	public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id) {
		//always skip
		return null;
	}

	@Override
	public PhdVisitor2 visitPhd(PhdBallVisitorCallback callback, String id,
			int version) {
		//always skip
		return null;
	}

	@Override
	public PhdWholeReadTagVisitor visitReadTag() {
		//always skip
		return null;
	}

	@Override
	public void visitEnd() {
		//no-op
	}

	@Override
	public void halted() {
		//no-op
	}

}
