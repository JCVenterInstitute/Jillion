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
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.Arrays;
import java.util.List;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.DefaultContigSlice;
import org.jcvi.assembly.slice.SliceLocation;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultContigSlice {

    Location<EncodedGlyphs<NucleotideGlyph>> consensus = createMock(Location.class);
    SliceLocation<PlacedRead> slice_1 = createMock(SliceLocation.class);
    SliceLocation<PlacedRead> slice_2 = createMock(SliceLocation.class);
    SliceLocation<PlacedRead> slice_3 = createMock(SliceLocation.class);
    
    
    List<SliceLocation<PlacedRead>> underlyingReads = Arrays.asList(slice_1, slice_2, slice_3);
    
    DefaultContigSlice<PlacedRead> sut = new DefaultContigSlice<PlacedRead>(consensus, underlyingReads);
    
    
    @Test
    public void constructor(){
        assertEquals(consensus, sut.getConsensus());
        assertEquals(underlyingReads, sut.getUnderlyingSliceLocations());
    }
    
    @Test
    public void nullConsensusShouldThrowIllegalArgumentException(){
        try{
            new DefaultContigSlice<PlacedRead>(null, underlyingReads);     
            fail("should throw IllegalArgumentException when consensus is null");
        }catch(IllegalArgumentException e){
            assertEquals("consensus can not be null", e.getMessage());
        }
    }
    @Test
    public void nullUnderlyingReadsShouldThrowIllegalArgumentException(){
        try{
            new DefaultContigSlice<PlacedRead>(consensus, null);     
            fail("should throw IllegalArgumentException when underlying reads is null");
        }catch(IllegalArgumentException e){
            assertEquals("underlyingReads can not be null", e.getMessage());
        }
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a contigSlice"));
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        DefaultContigSlice<PlacedRead> sameValues = new DefaultContigSlice<PlacedRead>(consensus, underlyingReads);
        
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentConsensusShouldNotBeEqual(){
        Location<EncodedGlyphs<NucleotideGlyph>> differentConsensus = createMock(Location.class);
        DefaultContigSlice<PlacedRead> hasdifferentConsensus = new DefaultContigSlice<PlacedRead>(differentConsensus, underlyingReads);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasdifferentConsensus);
    }
    
    @Test
    public void differentUnderlyingReadsShouldNotBeEqual(){
        DefaultContigSlice<PlacedRead> hasdifferentReads = new DefaultContigSlice<PlacedRead>(consensus, 
                underlyingReads.subList(0, 1));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasdifferentReads);
    }
}
