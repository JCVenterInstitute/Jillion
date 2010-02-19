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
package org.jcvi.assembly.contig;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Read;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;
public class TestQualityDifference {

    PhredQuality quality = PhredQuality.valueOf((byte) 20);
    Location<EncodedGlyphs<NucleotideGlyph>> reference = createMock(Location.class);
    Location<PlacedRead> read = createMock(Location.class);
    
    DefaultQualityDifference sut = new DefaultQualityDifference(reference, read, quality);
    
    @Test
    public void constructor(){
        assertEquals(quality, sut.getQuality());
        assertEquals(read, sut.getReadLocation());
        assertEquals(reference, sut.getReferenceLocation());
    }
    
    @Test
    public void nullReferenceThrowsIllegalArgumentException(){
        try{
            new DefaultQualityDifference(null, read, quality);
            fail("should throw illegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("can not have a null reference", e.getMessage());
        }
    }
    
    @Test
    public void nullReadThrowsIllegalArgumentException(){
        try{
            new DefaultQualityDifference(reference, null, quality);
            fail("should throw illegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("can not have a null read", e.getMessage());
        }
    }
    
    @Test
    public void equalsSameObjectReference(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a Quality Difference"));
    }
    
    @Test
    public void equalsSameValues(){
        DefaultQualityDifference sameValues = new DefaultQualityDifference(reference, read, quality);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentQualityShouldNotBeEqual(){
        DefaultQualityDifference differentQuality = new DefaultQualityDifference(reference, read, PhredQuality.valueOf((byte)30));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentQuality);
    }
    
    @Test
    public void differentReferenceShouldNotBeEqual(){
        DefaultQualityDifference differentReference = new DefaultQualityDifference(createMock(Location.class), read, quality);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentReference);
    }
    @Test
    public void differentReadShouldNotBeEqual(){
        DefaultQualityDifference differentRead = new DefaultQualityDifference(reference,createMock(Location.class), quality);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRead);
    }
    
    @Test
    public void testToString(){
        reset(read,reference);
        String readId = "readId";
        int readIndex = 5;
        NucleotideGlyph readGlyph = NucleotideGlyph.Adenine;
       
        setupRead(readId, readIndex, readGlyph);
        
        int referenceIndex = 1234;
        NucleotideGlyph referenceGlyph = NucleotideGlyph.Guanine;
        setupReference(referenceIndex, referenceGlyph);
        String expected =readId+"  quality = "+quality+" has a "+readGlyph+"@ "+readIndex
                        +" reference has a "+referenceGlyph+"@ "+referenceIndex;  
       
        assertEquals(expected, sut.toString());       
    }

    private void setupReference(int referenceIndex,
            NucleotideGlyph referenceGlyph) {
        expect(reference.getIndex()).andReturn(referenceIndex).anyTimes();
        EncodedGlyphs<NucleotideGlyph> mockReferenceGlyphs = createMock(EncodedGlyphs.class);
        expect(reference.getSource()).andReturn(mockReferenceGlyphs);
        expect(mockReferenceGlyphs.get(referenceIndex)).andReturn(referenceGlyph);
        replay(mockReferenceGlyphs);
        replay(read,reference);
    }

    private void setupRead(String readId, int readIndex,
            NucleotideGlyph readGlyph) {
        PlacedRead mockPlacedRead = createMock(PlacedRead.class);
        Read<ReferencedEncodedNucleotideGlyphs> mockRead = createMock(Read.class);
        ReferencedEncodedNucleotideGlyphs mockEncodedGlyphs = createMock(ReferencedEncodedNucleotideGlyphs.class);
        expect(read.getIndex()).andReturn(readIndex).anyTimes();
        
        expect(mockPlacedRead.getId()).andReturn(readId);
        
        expect(mockPlacedRead.getEncodedGlyphs()).andReturn(mockEncodedGlyphs);
        expect(mockEncodedGlyphs.get(readIndex)).andReturn(readGlyph);
        expect(read.getSource()).andReturn(mockPlacedRead).anyTimes();
        replay(mockPlacedRead,mockRead ,mockEncodedGlyphs);
    }
}
