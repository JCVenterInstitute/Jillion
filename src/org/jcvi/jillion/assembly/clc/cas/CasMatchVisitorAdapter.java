package org.jcvi.jillion.assembly.clc.cas;
/**
 * {@code CasMatchVisitorAdapter} is a {@link CasMatchVisitor}
 * that wraps another {@link CasMatchVisitor} and delegates
 * all visitXXX methods to the wrapped instance.
 * 
 * Subclasses may override any visit methods to modify
 * the visit messages before the wrapped instance receives them.
 * 
 * @author dkatzel
 *
 */
public class CasMatchVisitorAdapter implements CasMatchVisitor{

	private final CasMatchVisitor delegate;
	/**
	 * Create a new instance of CasMatchVisitorAdapter
	 * which will wrap the given {@link CasMatchVisitor}.
	 * @param delegate the {@link CasMatchVisitor} to wrap;
	 * may not be null.
	 * @throws NullPointerException if delegate is null.
	 */
	public CasMatchVisitorAdapter(CasMatchVisitor delegate) {
		if(delegate ==null){
			throw new NullPointerException("delegate can not be null");
		}
		this.delegate = delegate;
	}
	/**
	 * Get the {@link CasMatchVisitor} instance
	 * that will is being wrapped.
	 * @return the delegate that was provided in the constructor;
	 * will never be null.
	 */
	protected CasMatchVisitor getDelegate() {
		return delegate;
	}

	@Override
	public void visitMatch(CasMatch match) {
		delegate.visitMatch(match);		
	}

	@Override
	public void visitEnd() {
		delegate.visitEnd();		
	}

	@Override
	public void halted() {
		delegate.halted();
	}
	
	
}
