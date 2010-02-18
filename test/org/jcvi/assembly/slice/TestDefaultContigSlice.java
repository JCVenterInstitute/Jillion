/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.Arrays;
import java.util.List;

import org.jcvi.TestUtil;
import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.DefaultContigSlice;
import org.jcvi.assembly.slice.SliceLocation;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
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
