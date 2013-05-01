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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;

public class MultipleAceContigVisitor implements AceContigVisitor{

	private final List<AceContigVisitor> delegates;
	
	public MultipleAceContigVisitor(AceContigVisitor...delegates){
		this(Arrays.asList(delegates));
	}
	public MultipleAceContigVisitor(List<? extends AceContigVisitor> delegates) {
		this.delegates = new ArrayList<AceContigVisitor>(delegates.size());
		for(AceContigVisitor visitor : delegates){
			if(visitor !=null){
				this.delegates.add(visitor);
			}
		}
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitBasesLine(mixedCaseBasecalls);
		}
		
	}

	@Override
	public void visitConsensusQualities(
			QualitySequence ungappedConsensusQualities) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitConsensusQualities(ungappedConsensusQualities);
		}
		
	}

	@Override
	public void visitAlignedReadInfo(String readId, Direction dir,
			int gappedStartPosition) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitAlignedReadInfo(readId, dir, gappedStartPosition);
		}
		
	}

	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitBaseSegment(gappedConsensusRange, readId);
		}
		
	}

	@Override
	public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
		List<AceContigReadVisitor> readVisitors = new ArrayList<AceContigReadVisitor>(delegates.size());
		for(AceContigVisitor visitor : delegates){
			AceContigReadVisitor readVisitor = visitor.visitBeginRead(readId, gappedLength);
			if(readVisitor !=null){
				readVisitors.add(readVisitor);
			}
		}
		if(readVisitors.isEmpty()){
			//all delegates say skip
			return null;
		}
		return new MultipleAceContigReadVisitor(readVisitors);
	}

	@Override
	public void visitEnd() {
		for(AceContigVisitor visitor : delegates){
			visitor.visitEnd();
		}
		
	}

	@Override
	public void halted() {
		for(AceContigVisitor visitor : delegates){
			visitor.halted();
		}
		
	}

}
