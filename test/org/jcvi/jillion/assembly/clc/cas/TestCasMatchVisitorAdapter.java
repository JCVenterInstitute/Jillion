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
