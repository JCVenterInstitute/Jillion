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
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;

/**
 * {@code AceContigVisitorAdapter} is an {@link AceContigVisitor}
 * that delegates calls to another {@link AceContigVisitor}.  This class
 * can be subclassed to add,remove or change the visit calls to the delegate.
 * @author dkatzel
 *
 */
public class AceContigVisitorAdapter implements AceContigVisitor{

	private final AceContigVisitor delegate;
	/**
	 * Creates a new {@link AceContigVisitorAdapter} that delegates
	 * all calls to the given visitor.
	 * @param delegate the AceContigVisitor that will receive 
	 * all the visit method calls; can not be null.
	 * @throws NullPointerException if delegate is null.
	 */
	public AceContigVisitorAdapter(AceContigVisitor delegate) {
		if(delegate ==null){
			throw new NullPointerException("delegate can not be null");
		}
		this.delegate = delegate;
	}

	/**
	 * Get the {@link AceContigVisitor} delegate that is
	 * being wrapped.
	 * @return a {@link AceContigVisitor} will never be null.
	 */
	public final AceContigVisitor getDelegate() {
		return delegate;
	}


	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		delegate.visitBasesLine(mixedCaseBasecalls);		
	}

	@Override
	public void visitConsensusQualities(
			QualitySequence ungappedConsensusQualities) {
		delegate.visitConsensusQualities(ungappedConsensusQualities);		
	}

	@Override
	public void visitAlignedReadInfo(String readId, Direction dir,
			int gappedStartOffset) {
		delegate.visitAlignedReadInfo(readId, dir, gappedStartOffset);		
	}

	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		delegate.visitBaseSegment(gappedConsensusRange, readId);		
	}

	@Override
	public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
		return delegate.visitBeginRead(readId, gappedLength);
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
