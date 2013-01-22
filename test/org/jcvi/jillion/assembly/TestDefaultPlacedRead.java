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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.DefaultAssembledRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultPlacedRead {

    /**
     * 
     */
    private static final int ungappedLength = 500;
    ReferenceMappedNucleotideSequence sequence;
    Direction dir = Direction.FORWARD;
    long start = 100;
    long length = 200L;
    Range validRange = Range.of(start, length);
    DefaultAssembledRead sut ;
    		String id = "id";
    @Before
    public void setup(){
        sequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(sequence.getLength()).andStubReturn(length);
        replay(sequence);
        sut = new DefaultAssembledRead(id,sequence, start,dir,ungappedLength,validRange);
    
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
        AssembledRead sameValues =  new DefaultAssembledRead(id, sequence, start,dir,500,validRange);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentReadIsNotEqual(){
        ReferenceMappedNucleotideSequence differentSequence = createMock(ReferenceMappedNucleotideSequence.class);
        AssembledRead hasDifferentRead =  new DefaultAssembledRead(id, differentSequence, start,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentIdIsNotEqual(){
         AssembledRead hasDifferentRead =  new DefaultAssembledRead("different"+id, sequence, start,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentStartIsNotEqual(){
        AssembledRead hasDifferentStart =  new DefaultAssembledRead(id,sequence, start-1,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentStart);
    }
    
    
}
