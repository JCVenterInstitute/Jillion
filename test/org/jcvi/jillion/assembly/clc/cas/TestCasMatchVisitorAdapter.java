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

import static org.junit.Assert.assertSame;

import org.easymock.EasyMockSupport;
import org.junit.Test;
public class TestCasMatchVisitorAdapter extends EasyMockSupport{

	private final CasMatchVisitorAdapter sut;
	private final CasMatchVisitor delegate;
	
	public TestCasMatchVisitorAdapter(){
		delegate = createMock(CasMatchVisitor.class);
		sut = new CasMatchVisitorAdapter(delegate);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullDelegateThrowsNPE(){
		new CasMatchVisitorAdapter(null);
	}
	
	@Test
	public void getDelegate(){
		assertSame(delegate, sut.getDelegate());
	}
	
	@Test
	public void delegateHalted(){
		delegate.halted();
		replayAll();
		sut.halted();
		verifyAll();
	}
	
	@Test
	public void delegateEnd(){
		delegate.visitEnd();
		replayAll();
		sut.visitEnd();
		verifyAll();
	}
	
	@Test
	public void delegateMatch(){
		CasMatch match = createMock(CasMatch.class);
		delegate.visitMatch(match);
		replayAll();
		sut.visitMatch(match);
		verifyAll();
	}
	
	@Test
	public void adaptMatch(){
		CasMatch originalMatch = createMock(CasMatch.class);
		final CasMatch adaptedMatch = createMock(CasMatch.class);
		delegate.visitMatch(adaptedMatch);
		replayAll();
		CasMatchVisitorAdapter adapter = new CasMatchVisitorAdapter(delegate){

			@Override
			public void visitMatch(CasMatch match) {
				super.visitMatch(adaptedMatch);
			}
			
		};
		
		adapter.visitMatch(originalMatch);
		verifyAll();
	}
}
