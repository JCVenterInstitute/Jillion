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
 * Created on Jan 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.coverage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.assembly.Placed;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageRegion;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultCoverageRegion {

    Placed seq1 = createMock(Placed.class);
    Placed seq2 = createMock(Placed.class);
    int start = 100;
    int length = 200;
    int end = start+length-1;
    DefaultCoverageRegion<Placed> sut = new DefaultCoverageRegion.Builder<Placed>(start,Arrays.asList(seq1,seq2))
                                        .end(end)
                                        .build();
    
    @Test
    public void builder(){

        
        assertEquals(start, sut.getStart());
        assertEquals(end, sut.getEnd());
        assertEquals(length, sut.getLength());
        assertEquals(Arrays.asList(seq1,seq2), getElements(sut));
        assertEquals(2, sut.getCoverage());
    }
    @Test
    public void add(){

        DefaultCoverageRegion<Placed> region = new DefaultCoverageRegion.Builder<Placed>(start,Arrays.asList(seq1))
                                    .add(seq2)
                                    .end(end)
                                        .build();
        
        assertEquals(start, region.getStart());
        assertEquals(end, region.getEnd());
        assertEquals(length, region.getLength());
        assertEquals(Arrays.asList(seq1,seq2), getElements(region));
        assertEquals(2, region.getCoverage());
    }
    
    private List<Placed> getElements(CoverageRegion<Placed> region){
        List<Placed> actual = new ArrayList<Placed>();
        for(Placed p : region){
            actual.add(p);
        }
        return actual;
    }
    @Test
    public void remove(){
        DefaultCoverageRegion<Placed> region = new DefaultCoverageRegion.Builder<Placed>(start,Arrays.asList(seq1,seq2))
                    .end(end)
                    .remove(seq2)
                    .build();
        
        assertEquals(start, region.getStart());
        assertEquals(end, region.getEnd());
        assertEquals(length, region.getLength());
        assertEquals(Arrays.asList(seq1), getElements(region));
        assertEquals(1, region.getCoverage());
    }
    
    @Test
    public void endNotSetShouldThowIllegalStateException(){
        
        try{
            new DefaultCoverageRegion.Builder<Placed>(start,Arrays.asList(seq1,seq2)).build();
            fail("calling build() before end() should throw illegalState Exception");
        }
        catch(IllegalStateException e){
            assertEquals("end must be set", e.getMessage());
        }
    }
    
    @Test
    public void nullElementsShouldThrowIllegalArgumentException(){
        try{
            new DefaultCoverageRegion.Builder<Placed>(start,null);
            fail("passing in null elements should throw illegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("elements can not be null" , e.getMessage());
        }
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void equalsSameRValues(){
        DefaultCoverageRegion sameValues = new DefaultCoverageRegion.Builder<Placed>(start,Arrays.asList(seq1,seq2))
                                        .end(end)
                                        .build();
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a coverageRegion"));
    }
    
    @Test
    public void notEqualsDifferentStart(){
        DefaultCoverageRegion differentStart = new DefaultCoverageRegion.Builder<Placed>(
                                    start+1,
                                    Arrays.asList(seq1,seq2))
                                        .end(end+1)
                                        .build();
        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentStart);
    }
    
    @Test
    public void notEqualsDifferentLength(){
        DefaultCoverageRegion differentLength = new DefaultCoverageRegion.Builder<Placed>(
                                    start,
                                    Arrays.asList(seq1,seq2))
                                        .end(end+1)
                                        .build();
        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentLength);
    }
    @Test
    public void notEqualsDifferentElements(){
        DefaultCoverageRegion differentElements = new DefaultCoverageRegion.Builder<Placed>(
                                    start,
                                    Arrays.asList(seq1))
                                        .end(end)
                                        .build();
        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentElements);
    }
    
    @Test
    public void sameElementsDifferentOrderIsNotEqual(){
        DefaultCoverageRegion differentElementOrder = new DefaultCoverageRegion.Builder<Placed>(
                                    start,
                                    Arrays.asList(seq2,seq1))
                                        .end(end)
                                        .build();
        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentElementOrder);
    }
    
}
