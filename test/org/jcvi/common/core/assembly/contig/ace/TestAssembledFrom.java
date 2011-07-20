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
 * Created on Feb 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.assembly.contig.ace.AssembledFrom;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAssembledFrom {

    Direction dir = Direction.FORWARD;
    String id = "assembled from id";
    int offset = 12345;
    
    AssembledFrom sut = new AssembledFrom(id, offset, dir);
    
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(offset, sut.getStartOffset());
        assertEquals(dir, sut.getSequenceDirection());
    }
    @Test
    public void nullIdShouldThrowIllegalArgumentException(){
        try{
            new AssembledFrom(null, offset, dir);
            fail("should throw IllegalArgumentException when id is null");
        }catch(IllegalArgumentException e){
            assertEquals("id can not be null", e.getMessage());
        }
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void differentClassNotEquals(){
        assertFalse(sut.equals("not an AssembledFrom"));
    }
    
    @Test
    public void equalsSameValues(){
        AssembledFrom sameValues = new AssembledFrom(id, offset, dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentIdShouldNotBeEqual(){
        AssembledFrom differentId = new AssembledFrom("different"+id, offset, dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    /**
     * Only id is used to calculate equality.
     */
    @Test
    public void differentOffsetShouldStillBeEqual(){
        AssembledFrom differentOffset = new AssembledFrom(id, offset+1, dir);
        TestUtil.assertEqualAndHashcodeSame(sut, differentOffset);
    }
    /**
     * Only id is used to calculate equality.
     */
    @Test
    public void differentComlimentShouldStillBeEqual(){
        AssembledFrom differentCompliment = new AssembledFrom(id, offset, Direction.REVERSE);
        TestUtil.assertEqualAndHashcodeSame(sut, differentCompliment);
    }
    
    @Test
    public void testToString(){
        String expected = id + " " + offset + "is complimented? "+(dir ==Direction.REVERSE);
        assertEquals(expected, sut.toString());
    }
}
