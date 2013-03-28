/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
