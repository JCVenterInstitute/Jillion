/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.TestUtil;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.Read;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultLocation {

    Read<NucleotideEncodedGlyphs> aRead = createMock(Read.class);
    int index = 1234;
    
    DefaultLocation<Read<NucleotideEncodedGlyphs>> sut = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(aRead, index);
    
    @Test
    public void constructor(){
        assertEquals(aRead ,sut.getSource());
        assertEquals(index, sut.getIndex());
    }
    
    @Test
    public void nullSourceShouldThrowIllegalArgumentExcetion(){
        try{
            new DefaultLocation<Read<NucleotideEncodedGlyphs>>(null, index);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("source can not be null", e.getMessage());
        }
    }
    @Test
    public void nullNotEquals(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void differentClassNotEquals(){
        assertFalse(sut.equals("not a defaultLocation"));
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        DefaultLocation<Read<NucleotideEncodedGlyphs>> sameValues = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(aRead, index);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentIndexShouldNotBeEqual(){
        DefaultLocation<Read<NucleotideEncodedGlyphs>> differentIndex = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(aRead, index+1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentIndex);
    }
    @Test
    public void differentSourceShouldNotBeEqual(){
        DefaultLocation<Read<NucleotideEncodedGlyphs>> differentSource = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(
                createMock(Read.class), index);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentSource);
    }
}
