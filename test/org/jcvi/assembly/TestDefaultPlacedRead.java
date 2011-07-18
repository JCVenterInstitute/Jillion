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
package org.jcvi.assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.Range;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferenceEncodedNucleotideSequence;
import org.jcvi.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultPlacedRead {

    Read<ReferenceEncodedNucleotideSequence> read;
    SequenceDirection dir = SequenceDirection.FORWARD;
    long start = 100;
    
    DefaultPlacedRead sut ;
    @Before
    public void setup(){
        read = createMock(Read.class);
        sut = new DefaultPlacedRead(read, start,dir);
    }
    @Test
    public void constructor(){
        String id = "id";
        long length = 200L;
        Range validRange = Range.buildRange(start, length);
        ReferenceEncodedNucleotideSequence glyphs = createMock(ReferenceEncodedNucleotideSequence.class);
   
        Map<Integer,NucleotideGlyph> snpMap = new HashMap<Integer, NucleotideGlyph>();
        snpMap.put(Integer.valueOf(1), NucleotideGlyph.Adenine);
        snpMap.put(Integer.valueOf(3), NucleotideGlyph.Cytosine);
        snpMap.put(Integer.valueOf(5), NucleotideGlyph.Guanine);
        
        List<Integer> snps = new ArrayList<Integer>(snpMap.keySet());
        for(Entry<Integer,NucleotideGlyph> entry : snpMap.entrySet()){
            expect(glyphs.get(entry.getKey().intValue())).andReturn(entry.getValue());
        }
        expect(read.getId()).andReturn(id);
        expect(read.getEncodedGlyphs()).andReturn(glyphs).times(3);
        expect(read.getLength()).andReturn(length).times(2);
        expect(glyphs.getValidRange()).andReturn(validRange);
        expect(glyphs.getSnpOffsets()).andReturn(snps);
        replay(read, glyphs);
        assertEquals(dir,sut.getSequenceDirection());
        assertEquals(start, sut.getStart());
        assertEquals(read, sut.getRead());
        assertEquals(id, sut.getId());
        assertEquals(glyphs, sut.getEncodedGlyphs());
        assertEquals(length, sut.getLength());
        assertEquals(start+ length-1 , sut.getEnd());
        assertEquals(validRange, sut.getValidRange());
        assertEquals(snpMap, sut.getSnps());
        verify(read, glyphs);        
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
        PlacedRead sameValues =  new DefaultPlacedRead(read, start,dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentReadIsNotEqual(){
        Read differentRead = createMock(Read.class);
        PlacedRead hasDifferentRead =  new DefaultPlacedRead(differentRead, start,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentStartIsNotEqual(){
        PlacedRead hasDifferentStart =  new DefaultPlacedRead(read, start-1,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentStart);
    }
    @Test
    public void nullReadThrowsIllegalArgumentException(){
        try{
            new DefaultPlacedRead(null, start,dir);
            fail("should throw IllegalArgument exception when passed read is null");
        }catch(IllegalArgumentException e){
            assertEquals("read can not be null", e.getMessage());
        }
    }
    
    @Test
    public void testToString(){
        String expected = "offset = "+ start + " complimented? "+ dir+"  " + read;
        assertEquals(expected, sut.toString());
    }
}
