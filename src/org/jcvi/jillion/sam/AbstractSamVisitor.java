package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.header.SamHeader;

/**
 * {@code AbstractSamVisitor} is an implementation
 * of {@link SamVisitor} that implements all the methods
 * of {@link SamVisitor} as empty methods.
 * 
 * Users may subclass {@code AbstractSamVisitor} and 
 * override the methods they want to implement.
 * @author dkatzel
 *
 */
public abstract class AbstractSamVisitor implements SamVisitor{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record,
			VirtualFileOffset start, VirtualFileOffset end) {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record) {
		//no-op
	}
	
	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEnd() {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void halted() {
		//no-op
	}
}
