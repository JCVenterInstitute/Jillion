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
 * {@code AbstractAceContigVisitor} is an {@link AceContigVisitor}
 * that implements all the methods as stubs.  By default
 * all non-void methods return {@code null}.  Users should
 * create subclasses that override the methods they wish to handle.
 * @author dkatzel
 *
 */
public abstract class AbstractAceContigVisitor implements AceContigVisitor{

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		//no-op		
	}

	@Override
	public void visitConsensusQualities(
			QualitySequence ungappedConsensusQualities) {
		//no-op		
	}

	@Override
	public void visitAlignedReadInfo(String readId, Direction dir,
			int gappedStartOffset) {
		//no-op
	}

	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		//no-op
	}

	@Override
	public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
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
