/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.DefaultPlacedRead;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.symbol.residue.nt.ReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultPlacedRead {

    /**
     * 
     */
    private static final int ungappedLength = 500;
    ReferenceEncodedNucleotideSequence sequence;
    Direction dir = Direction.FORWARD;
    long start = 100;
    long length = 200L;
    Range validRange = Range.create(start, length);
    DefaultPlacedRead sut ;
    		String id = "id";
    @Before
    public void setup(){
        sequence = createMock(ReferenceEncodedNucleotideSequence.class);
        expect(sequence.getLength()).andStubReturn(length);
        replay(sequence);
        sut = new DefaultPlacedRead(id,sequence, start,dir,ungappedLength,validRange);
    
    }
    @Test
    public void constructor(){
    	
    	
        assertEquals(dir,sut.getDirection());
        assertEquals(start, sut.getGappedStartOffset());
        assertEquals(id, sut.getId());
        assertEquals(sequence, sut.getNucleotideSequence());
        assertEquals(length, sut.getGappedLength());
        assertEquals(start+ length-1 , sut.getGappedEndOffset());
        assertEquals(validRange, sut.getReadInfo().getValidRange());
        verify(sequence);        
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a DefaultPlacedRead"));
    }
    @Test
    public void sameRefIsEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesAreEqual(){
        AssembledRead sameValues =  new DefaultPlacedRead(id, sequence, start,dir,500,validRange);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentReadIsNotEqual(){
        ReferenceEncodedNucleotideSequence differentSequence = createMock(ReferenceEncodedNucleotideSequence.class);
        AssembledRead hasDifferentRead =  new DefaultPlacedRead(id, differentSequence, start,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentIdIsNotEqual(){
         AssembledRead hasDifferentRead =  new DefaultPlacedRead("different"+id, sequence, start,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentStartIsNotEqual(){
        AssembledRead hasDifferentStart =  new DefaultPlacedRead(id,sequence, start-1,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentStart);
    }
    
    
}
