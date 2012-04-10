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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.DefaultPlacedRead;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
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
    Read<ReferenceEncodedNucleotideSequence> read;
    Direction dir = Direction.FORWARD;
    long start = 100;
    long length = 200L;
    Range validRange = Range.create(start, length);
    DefaultPlacedRead sut ;
    @Before
    public void setup(){
        read = createMock(Read.class);
        sut = new DefaultPlacedRead(read, start,dir,ungappedLength,validRange);
    }
    @Test
    public void constructor(){
        String id = "id";
        
        ReferenceEncodedNucleotideSequence glyphs = createMock(ReferenceEncodedNucleotideSequence.class);
   
        Map<Integer,Nucleotide> snpMap = new HashMap<Integer, Nucleotide>();
        snpMap.put(Integer.valueOf(1), Nucleotide.Adenine);
        snpMap.put(Integer.valueOf(3), Nucleotide.Cytosine);
        snpMap.put(Integer.valueOf(5), Nucleotide.Guanine);
        
        List<Integer> snps = new ArrayList<Integer>(snpMap.keySet());
        for(Entry<Integer,Nucleotide> entry : snpMap.entrySet()){
            expect(glyphs.get(entry.getKey().intValue())).andReturn(entry.getValue());
        }
        expect(read.getId()).andReturn(id);
        expect(read.getNucleotideSequence()).andStubReturn(glyphs);
        expect(read.getLength()).andReturn(length).times(2);
        expect(glyphs.getSnpOffsets()).andReturn(snps);
        replay(read, glyphs);
        assertEquals(dir,sut.getDirection());
        assertEquals(start, sut.getBegin());
        assertEquals(read, sut.getRead());
        assertEquals(id, sut.getId());
        assertEquals(glyphs, sut.getNucleotideSequence());
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
        PlacedRead sameValues =  new DefaultPlacedRead(read, start,dir,500,validRange);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentReadIsNotEqual(){
        Read differentRead = createMock(Read.class);
        PlacedRead hasDifferentRead =  new DefaultPlacedRead(differentRead, start,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentStartIsNotEqual(){
        PlacedRead hasDifferentStart =  new DefaultPlacedRead(read, start-1,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentStart);
    }
    @Test
    public void nullReadThrowsIllegalArgumentException(){
        try{
            new DefaultPlacedRead(null, start,dir,500,validRange);
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
