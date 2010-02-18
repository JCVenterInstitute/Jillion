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
    NucleotideGlyphFactory glyphFactory = NucleotideGlyphFactory.getInstance();
    List<NucleotideGlyph> evenBases = glyphFactory.getGlyphsFor("ACGTACGTWS-NACGT");
    List<NucleotideGlyph> oddBases =  glyphFactory.getGlyphsFor("ACGTACGTWS-NACGTA");
    
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
        List<NucleotideGlyph> expectedGlyphs = glyphFactory.getGlyphsFor(basesString);
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
