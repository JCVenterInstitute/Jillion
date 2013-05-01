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
