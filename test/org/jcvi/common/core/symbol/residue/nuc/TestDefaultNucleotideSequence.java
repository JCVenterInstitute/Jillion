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

package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultNucleotideSequence {

    private String gappedBasecalls = "ACGTACGT-ACGTACG-T";
    
    NucleotideSequence sut = new NucleotideSequenceBuilder(gappedBasecalls)
    								.build();
    
    @Test
    public void decode(){
        List<Nucleotide> expected = Nucleotides.parse(gappedBasecalls);
        assertEquals(expected, sut.asList());
    }
    
    @Test
    public void getNumberOfGaps(){
        assertEquals(2, sut.getNumberOfGaps());
    }
    
    @Test
    public void getNumberOfGapsUntil(){
        assertEquals("before any gaps", 0, sut.getNumberOfGapsUntil(5));
        assertEquals("on the gap", 1, sut.getNumberOfGapsUntil(8));
        assertEquals("after 1 gap", 1, sut.getNumberOfGapsUntil(9));
        assertEquals("after all gaps", 2, sut.getNumberOfGapsUntil((int)sut.getLength()-1));
    }
    
    @Test
    public void getGapOffsets(){
    	assertEquals(Arrays.asList(8, 16), sut.getGapOffsets());
    }
    
    @Test
    public void getLength(){
    	assertEquals(18, sut.getLength());
    }
    
    @Test
    public void getUngappedLength(){
    	assertEquals(16, sut.getUngappedLength());
    }
    
    @Test
    public void getUngappedOffsetFor(){
        assertEquals("before any gaps", 5, sut.getUngappedOffsetFor(5));
        assertEquals("on the gap", 7, sut.getUngappedOffsetFor(8));
        assertEquals("after 1 gap", 8, sut.getUngappedOffsetFor(9));
        assertEquals("after all gaps", sut.getLength()-3, sut.getUngappedOffsetFor((int)sut.getLength()-1));
    }
    
    @Test
    public void getGappedOffsetFor(){
        assertEquals("before any gaps", 5, sut.getGappedOffsetFor(5));
        assertEquals("after 1 gap", 9, sut.getGappedOffsetFor(8));
        assertEquals("after all gaps", 17, sut.getGappedOffsetFor(15));
    }
    
    @Test
    public void iterator(){
        Iterator<Nucleotide> expected = Nucleotides.parse(gappedBasecalls).iterator();
        Iterator<Nucleotide> actual = sut.iterator();
        assertTrue(actual.hasNext());
        while(actual.hasNext()){
            assertEquals(expected.next(), actual.next());
        }
        assertFalse(expected.hasNext());
    }
    
    @Test
    public void testToString(){
        assertEquals(gappedBasecalls, sut.toString());
    }
}
