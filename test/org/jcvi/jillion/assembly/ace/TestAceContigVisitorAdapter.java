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
package org.jcvi.jillion.assembly.ace;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.junit.Test;
public class TestAceContigVisitorAdapter {

	@Test
	public void delegatesVisitAlignedReadInfo(){
		AceContigVisitor delegate = createMock(AceContigVisitor.class);
		String readId = "readId";
		Direction dir = Direction.FORWARD;
		int offset = 12345;
		delegate.visitAlignedReadInfo(readId, dir, offset);
		
		replay(delegate);
		AceContigVisitor sut = new AceContigVisitorAdapter(delegate);
		sut.visitAlignedReadInfo(readId, dir, offset);
		verify(delegate);
	}
	
	@Test
	public void delegatesVisitBaseSegment(){
		AceContigVisitor delegate = createMock(AceContigVisitor.class);
		Range range = Range.of(5,10);
		String readId = "readId";
		delegate.visitBaseSegment(range, readId);
		
		replay(delegate);
		AceContigVisitor sut = new AceContigVisitorAdapter(delegate);
		sut.visitBaseSegment(range, readId);
		verify(delegate);
	}
	
	@Test
	public void delegatesVisitBasesLine(){
		AceContigVisitor delegate = createMock(AceContigVisitor.class);

		String mixedCaseBases = "acgtACGT";
		delegate.visitBasesLine(mixedCaseBases);
		
		replay(delegate);
		AceContigVisitor sut = new AceContigVisitorAdapter(delegate);
		sut.visitBasesLine(mixedCaseBases);
		verify(delegate);
	}
	@Test
	public void delegatesVisitEnd(){
		AceContigVisitor delegate = createMock(AceContigVisitor.class);
		delegate.visitEnd();
		
		replay(delegate);
		AceContigVisitor sut = new AceContigVisitorAdapter(delegate);
		sut.visitEnd();
		verify(delegate);
	}
	@Test
	public void delegatesHalted(){
		AceContigVisitor delegate = createMock(AceContigVisitor.class);
		delegate.halted();
		
		replay(delegate);
		AceContigVisitor sut = new AceContigVisitorAdapter(delegate);
		sut.halted();
		verify(delegate);
	}
	
	@Test
	public void delegatesVisitConsensusQualities(){
		AceContigVisitor delegate = createMock(AceContigVisitor.class);
		QualitySequence quals = new QualitySequenceBuilder(new byte[]{20,30,40,50}).build();
		delegate.visitConsensusQualities(quals);
		
		replay(delegate);
		AceContigVisitor sut = new AceContigVisitorAdapter(delegate);
		sut.visitConsensusQualities(quals);
		verify(delegate);
	}
	
	@Test
	public void delegateVisitRead(){
		AceContigVisitor delegate = createMock(AceContigVisitor.class);
		AceContigReadVisitor readVisitor = createMock(AceContigReadVisitor.class);
		
		String readId = "readId";
		int gappedLength = 123;
		expect(delegate.visitBeginRead(readId, gappedLength)).andReturn(readVisitor);
		replay(delegate, readVisitor);
		AceContigVisitor sut = new AceContigVisitorAdapter(delegate);
		assertEquals(readVisitor, sut.visitBeginRead(readId, gappedLength));
		verify(delegate, readVisitor);
	}
}
