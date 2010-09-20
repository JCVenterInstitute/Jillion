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
 * Created on Dec 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.Arrays;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.annot.Exon;
import org.jcvi.assembly.annot.ref.CodingRegionState;
import org.jcvi.assembly.annot.ref.DefaultCodingRegion;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;

public class TestCodingRegion {

    Range range = createMock(Range.class);
    List<Exon> exons = Arrays.asList(createMock(Exon.class), createMock(Exon.class));
    DefaultCodingRegion sut = new DefaultCodingRegion(range, CodingRegionState.COMPLETE, CodingRegionState.INCOMPLETE, exons);
    
    @Test
    public void constructor(){
        assertEquals(range, sut.getRange());
        assertEquals(exons, sut.getExons());
        assertEquals(CodingRegionState.COMPLETE, sut.getStartCodingRegionState());
        assertEquals(CodingRegionState.INCOMPLETE, sut.getEndCodingRegionState());
    }
    @Test(expected =UnsupportedOperationException.class )
    public void testExonListUnmodifiable(){
            sut.getExons().remove(0);         
    }
    
    @Test
    public void nullRangeShouldThrowIllegalArgumentException(){
        try{
            new DefaultCodingRegion(null, CodingRegionState.COMPLETE, CodingRegionState.INCOMPLETE, exons);
            fail("should throw IllegalArgumentException when range is null");
        }catch(IllegalArgumentException e){
            assertEquals("range can not be null", e.getMessage());
        }
    }
    @Test
    public void nullStartStateShouldThrowIllegalArgumentException(){
        try{
            new DefaultCodingRegion(range, null, CodingRegionState.INCOMPLETE, exons);
            fail("should throw IllegalArgumentException when startState is null");
        }catch(IllegalArgumentException e){
            assertEquals("start state can not be null", e.getMessage());
        }
    }
    
    @Test
    public void nullEndStateShouldThrowIllegalArgumentException(){
        try{
            new DefaultCodingRegion(range, CodingRegionState.COMPLETE, null, exons);
            fail("should throw IllegalArgumentException when endState is null");
        }catch(IllegalArgumentException e){
            assertEquals("end state can not be null", e.getMessage());
        }
    }
    
    @Test
    public void nullExonsShouldThrowIllegalArgumentException(){
        try{
            new DefaultCodingRegion(range, CodingRegionState.COMPLETE, CodingRegionState.INCOMPLETE, null);
            fail("should throw IllegalArgumentException when exons is null");
        }catch(IllegalArgumentException e){
            assertEquals("exons can not be null", e.getMessage());
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
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("Not a DefaultCodingRegion"));
    }
    @Test
    public void equalsSameValues(){
        DefaultCodingRegion sameValues = new DefaultCodingRegion(range, CodingRegionState.COMPLETE, CodingRegionState.INCOMPLETE, exons);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void notEqualsDifferentRange(){
        DefaultCodingRegion differentRange = new DefaultCodingRegion(createMock(Range.class), CodingRegionState.COMPLETE, CodingRegionState.INCOMPLETE, exons);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRange);
    }
    @Test
    public void notEqualsDifferentStartState(){
        DefaultCodingRegion differentStartState = new DefaultCodingRegion(range, CodingRegionState.UNKNOWN, CodingRegionState.INCOMPLETE, exons);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentStartState);
    }
    @Test
    public void notEqualsDifferentEndState(){
        DefaultCodingRegion differentEndState = new DefaultCodingRegion(range, CodingRegionState.COMPLETE, CodingRegionState.NONE, exons);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentEndState);
    }
    @Test
    public void notEqualsDifferentExons(){
        DefaultCodingRegion differentExons = new DefaultCodingRegion(range, CodingRegionState.COMPLETE, CodingRegionState.NONE, Arrays.asList(createMock(Exon.class), createMock(Exon.class)));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentExons);
    }
}
