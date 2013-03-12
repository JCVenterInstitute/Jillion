package org.jcvi.jillion.assembly.clc.cas;

public class CasMatchVisitorAdapter implements CasMatchVisitor{

	private final CasMatchVisitor delegate;

	public CasMatchVisitorAdapter(CasMatchVisitor delegate) {
		if(delegate ==null){
			throw new NullPointerException("delegate can not be null");
		}
		this.delegate = delegate;
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
