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
/*
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Date;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.assembly.consed.ace.AceAssembledReadAdapter;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.junit.Before;
import org.junit.Test;

public class TestAcePlacedReadAdapter {

    AssembledRead mockPlacedRead;
    String id = "readId";
    Date date = new Date(123456789L);
    PhdInfo phdInfo = new PhdInfo(id, id+".phd.1", date);
    AceAssembledReadAdapter sut;
    long referenceIndex = 1234;
    long validRangeIndex = 7;
    Range validRange = Range.of(1,10);
    ReadInfo readInfo = new ReadInfo(validRange, (int)validRange.getLength()+1);
    @Before
    public void setup(){
        mockPlacedRead = createMock(AssembledRead.class);
        expect(mockPlacedRead.getId()).andReturn(id);
        replay(mockPlacedRead);
        sut = new AceAssembledReadAdapter(mockPlacedRead, date,null);
        reset(mockPlacedRead);
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);
    }
    
    @Test
    public void constructor(){
        assertEquals(phdInfo,sut.getPhdInfo());
    }
    @Test
    public void convertReferenceIndexToValidRangeIndex() {
        
        expect(mockPlacedRead.toGappedValidRangeOffset(referenceIndex)).andReturn(validRangeIndex);
        replay(mockPlacedRead);
        assertEquals(validRangeIndex, sut.toGappedValidRangeOffset(referenceIndex));
        verify(mockPlacedRead);
    }
    @Test
    public void convertValidRangeIndexToReferenceIndex() {
        expect(mockPlacedRead.toReferenceOffset(validRangeIndex)).andReturn(referenceIndex);
        replay(mockPlacedRead);
        assertEquals(referenceIndex, sut.toReferenceOffset(validRangeIndex));
        verify(mockPlacedRead);
    }
    @Test
    public void getSequenceDirection() {
        Direction direction = Direction.REVERSE;
        expect(mockPlacedRead.getDirection()).andReturn(direction);
        replay(mockPlacedRead);
        assertEquals(direction, sut.getDirection());
        verify(mockPlacedRead);
    }
    
    @Test
    public void getReadInfo() {
        replay(mockPlacedRead);
        assertEquals(readInfo, sut.getReadInfo());
        verify(mockPlacedRead);
    }
    @Test
    public void getSequence() {
    	ReferenceMappedNucleotideSequence sequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(sequence);
        replay(mockPlacedRead);
        assertEquals(sequence, sut.getNucleotideSequence());
        verify(mockPlacedRead);
        
    }
    @Test
    public void getId() {
        expect(mockPlacedRead.getId()).andReturn(id);
        replay(mockPlacedRead);
        assertEquals(id, sut.getId());
        verify(mockPlacedRead);
    }
    @Test
    public void getLength() {

        expect(mockPlacedRead.getGappedLength()).andReturn(validRange.getLength());
        replay(mockPlacedRead);
        assertEquals(validRange.getLength(), sut.getGappedLength());
        verify(mockPlacedRead);
    }
    @Test
    public void getEnd() {
        expect(mockPlacedRead.getGappedEndOffset()).andReturn(validRange.getEnd());
        replay(mockPlacedRead);
        assertEquals(validRange.getEnd(), sut.getGappedEndOffset());
        verify(mockPlacedRead);
    }
    @Test
    public void getStart() {
        expect(mockPlacedRead.getGappedStartOffset()).andReturn(validRange.getBegin());
        replay(mockPlacedRead);
        assertEquals(validRange.getBegin(), sut.getGappedStartOffset());
        verify(mockPlacedRead);
    } 
    
}
