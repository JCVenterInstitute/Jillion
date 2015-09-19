/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
