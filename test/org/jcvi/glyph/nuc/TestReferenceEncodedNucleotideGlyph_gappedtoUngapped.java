/*
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import org.jcvi.Range;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestReferenceEncodedNucleotideGlyph_gappedtoUngapped {

    String reference       = "ACGTACGTTACGTTACGT";
    String gappedBasecalls = "ACGTACGT-ACGTACG-T";
    String ungappedBasecalls = "ACGTACGTACGTACGT";
    DefaultNucleotideGlyphCodec codec = DefaultNucleotideGlyphCodec.getInstance();
    NucleotideGlyphFactory factory = NucleotideGlyphFactory.getInstance();
    DefaultEncodedGlyphs<NucleotideGlyph> encodedReference = new DefaultEncodedGlyphs<NucleotideGlyph>(codec,factory.getGlyphsFor(reference));
    DefaultReferencedEncodedNucleotideGlyph sut = new DefaultReferencedEncodedNucleotideGlyph(encodedReference, gappedBasecalls,0, Range.buildRange(0, gappedBasecalls.length()-1));
    
    
    @Test
    public void convertGappedToUngapped_beforeGapsShouldReturnSameNumber(){
        assertEquals(0,sut.convertGappedValidRangeIndexToUngappedValidRangeIndex(0));
        assertEquals(7,sut.convertGappedValidRangeIndexToUngappedValidRangeIndex(7));
        
        assertEquals(0, sut.convertUngappedValidRangeIndexToGappedValidRangeIndex(0));
        assertEquals(7,sut.convertUngappedValidRangeIndexToGappedValidRangeIndex(7));
    }
    
    @Test
    public void convertGappedToUngappedGappedIndexShouldThrowIllegalArgumentException(){
        final int indexOfFirstGap = gappedBasecalls.indexOf('-');
        try{
            sut.convertGappedValidRangeIndexToUngappedValidRangeIndex(indexOfFirstGap);
            fail("should throw illegal argumentexception when passed in an index that is a gap");
        }
        catch(IllegalArgumentException e){
            assertEquals(indexOfFirstGap+" is a gap", e.getMessage());
        }
    }
    
    @Test
    public void convertGappedToUngappedOneGapShouldReturnIndexMinusOne(){
        final int indexOfFirstGap = gappedBasecalls.indexOf('-');
        assertEquals(indexOfFirstGap,sut.convertGappedValidRangeIndexToUngappedValidRangeIndex(indexOfFirstGap+1));
        assertEquals(indexOfFirstGap+1,sut.convertUngappedValidRangeIndexToGappedValidRangeIndex(indexOfFirstGap));
    }
    @Test
    public void convertGappedToUngappedLastIndexShouldReturnLengthMinusNumberOfGaps(){
        int numberOfGaps=2;
        int gappedLength = gappedBasecalls.length();
        int lastGappedIndex = gappedLength-1;
        final int lastUngappedIndex = lastGappedIndex-numberOfGaps;
        assertEquals(lastUngappedIndex, sut.convertGappedValidRangeIndexToUngappedValidRangeIndex(lastGappedIndex));
        assertEquals(lastGappedIndex, sut.convertUngappedValidRangeIndexToGappedValidRangeIndex(lastUngappedIndex));
    }
    
    
}
