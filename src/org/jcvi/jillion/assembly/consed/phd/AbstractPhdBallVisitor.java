package org.jcvi.jillion.assembly.consed.phd;
/**
 * {@code AbstractPhdBallVisitor} is a {@link PhdBallVisitor}
 * that implements all the methods with default
 * implementations that don't do anything.  This code
 * is meant to be extended so users don't have to implement
 * any methods that they do not care about (users must override
 * any method that want to handle).
 *  
 * @author dkatzel
 *
 */
public abstract class AbstractPhdBallVisitor implements PhdBallVisitor {
	/**
	 * Ignores the file comment.
	 * {@inheritDoc}
	 */
	@Override
	public void visitFileComment(String comment) {
		//no-op

	}
	/**
	 * Always skips this read.
	 * @return null 
	 * {@inheritDoc}
	 */
	@Override
	public PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id,
			Integer version) {
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
