/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import static org.easymock.EasyMock.createMock;

import org.jcvi.TestUtil;
import org.jcvi.assembly.DefaultLocation;
import org.jcvi.assembly.slice.DefaultSliceLocation;
import org.jcvi.assembly.slice.SliceLocation;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Read;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultSliceLocation {

    private final Read<NucleotideEncodedGlyphs> aRead = createMock(Read.class);
    private final int index = 1234;
    private final PhredQuality quality = PhredQuality.valueOf((byte)30);
    private final PhredQuality differentQuality = PhredQuality.valueOf((byte)60);
    DefaultLocation<Read<NucleotideEncodedGlyphs>> defaultLocation = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(aRead, index);
    
    private SliceLocation<Read<NucleotideEncodedGlyphs>> sut;
    @Before
    public void setup(){
        sut = new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(aRead, index, quality);
  }
    @Test
    public void LocationConstructor(){
        SliceLocation<Read<NucleotideEncodedGlyphs>> sut = new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(defaultLocation, quality);
        
        assertEquals(aRead, sut.getSource());
        assertEquals(index, sut.getIndex());
        assertEquals(quality, sut.getQuality());
    }
    
    @Test
    public void constructor(){
        assertEquals(aRead, sut.getSource());
        assertEquals(index, sut.getIndex());
        assertEquals(quality, sut.getQuality());
    }
    
    @Test
    public void nullSourceShouldThrowIllegalArgumentExcetion(){
        try{
            new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(null, index,quality);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("source can not be null", e.getMessage());
        }
    }
    @Test
    public void nullQualityShouldThrowIllegalArgumentExcetion(){
        try{
            new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(aRead, index,null);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("quality can not be null", e.getMessage());
        }
    }
    @Test
    public void nullNotEquals(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void differentClassNotEquals(){
        assertFalse(sut.equals("not a SliceLocation"));
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        DefaultSliceLocation<Read<NucleotideEncodedGlyphs>> sameValues = new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(aRead, index,quality);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentIndexShouldNotBeEqual(){
        DefaultSliceLocation<Read<NucleotideEncodedGlyphs>> differentIndex = new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(aRead, index+1,quality);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentIndex);
    }
    @Test
    public void differentSourceShouldNotBeEqual(){
        DefaultSliceLocation<Read<NucleotideEncodedGlyphs>> differentSource = new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(
                createMock(Read.class), index,quality);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentSource);
    }
    @Test
    public void differentQualityShouldNotBeEqual(){
        DefaultSliceLocation<Read<NucleotideEncodedGlyphs>> hasDifferentQuality = new DefaultSliceLocation<Read<NucleotideEncodedGlyphs>>(
                aRead, index,differentQuality);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentQuality);
    }
    
    @Test
    public void notASliceLocationShouldNotBeEqual(){
        assertTrue(defaultLocation.equals(sut));
        assertFalse(sut.equals(defaultLocation));
    }
}
