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
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.Collections;
import java.util.List;

import org.junit.Test;


import static org.junit.Assert.*;
public class TestDefaultNucleotideGlyphCodec {
    List<NucleotideGlyph> evenBases = NucleotideGlyph.getGlyphsFor("ACGTACGTWS-NACGT");
    List<NucleotideGlyph> oddBases =  NucleotideGlyph.getGlyphsFor("ACGTACGTWS-NACGTA");
    
    DefaultNucleotideGlyphCodec sut = DefaultNucleotideGlyphCodec.getInstance();
    
    @Test
    public void evenEncodesAndDecodes(){
        byte[] encoded =sut.encode(evenBases);
        assertEquals(evenBases, sut.decode(encoded));
    }
    
    @Test
    public void oddEncodesAndDecodes(){
        byte[] encoded =sut.encode(oddBases);
        assertEquals(oddBases, sut.decode(encoded));
    }
    /**
     * this is a regression test for a bug where I was getting
     * the sign wrong for final odd byte values.  C is the base
     * that caused this error in production.
     */
    @Test
    public void oddNumberOfBasesEndsWithC(){
        assertFinalBaseInOddLengthSequenceCorrectlyDecoded("C");
    }
   
    private void assertFinalBaseInOddLengthSequenceCorrectlyDecoded(
            String finalBase) {
        String basesString = "ATTTGCTATCCATA"+finalBase;
        List<NucleotideGlyph> expectedGlyphs = NucleotideGlyph.getGlyphsFor(basesString);
        byte[] encoded =sut.encode(expectedGlyphs);
        assertEquals("did not decode final base of "+finalBase + " correctly",
                expectedGlyphs, sut.decode(encoded));
    }
    /**
     * to go along with {@link #oddNumberOfBasesEndsWithC()}
     * try every possible base ending
     */
    @Test
    public void testOddNumberOfBasesEveryPossibleFinalBase(){
        for(NucleotideGlyph nuc : NucleotideGlyph.values()){
            assertFinalBaseInOddLengthSequenceCorrectlyDecoded(nuc.toString());
        }
    }
    
    @Test
    public void noBases(){
        byte[] encoded = sut.encode(Collections.<NucleotideGlyph>emptyList());
        assertTrue(sut.decode(encoded).isEmpty());
    }
}
