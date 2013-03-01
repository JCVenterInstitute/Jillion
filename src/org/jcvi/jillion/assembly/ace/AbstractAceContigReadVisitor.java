package org.jcvi.jillion.assembly.ace;

import java.util.Date;
/**
 * {@code AbstractAceContigReadVisitor} is an {@link AceContigReadVisitor}
 * that implements all the methods as stubs.  By default
 * all non-void methods return {@code null}.  Users should
 * create subclasses that override the methods they wish to handle.
 * @author dkatzel
 *
 */
public abstract class AbstractAceContigReadVisitor implements AceContigReadVisitor{

	@Override
	public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
			int alignRight) {
		//no-op		
	}

	@Override
	public void visitTraceDescriptionLine(String traceName, String phdName,
			Date date) {
		//no-op
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		//no-op
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
