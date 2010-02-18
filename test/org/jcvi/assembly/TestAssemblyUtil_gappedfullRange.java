/*
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;


import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestAssemblyUtil_gappedfullRange {

    List<NucleotideGlyph> gappedValidRange = NucleotideGlyph.getGlyphsFor("ACGT-ACGT");
    PlacedRead mockPlacedRead;
    
    @Before
    public void setup(){
        mockPlacedRead = createMock(PlacedRead.class);
    }
    @Test
    public void entireSequenceIsValid(){
        
        List<NucleotideGlyph> ungappedUnComplimentedFullRange = NucleotideGlyph.convertToUngapped(gappedValidRange);
        Range validRange = Range.buildRange(0, ungappedUnComplimentedFullRange.size());
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getSequenceDirection()).andReturn(SequenceDirection.FORWARD);
        expect(mockPlacedRead.getEncodedGlyphs()).andReturn(new DefaultNucleotideEncodedGlyphs(gappedValidRange));
        replay(mockPlacedRead);
        List<NucleotideGlyph> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void entireSequenceIsValidButComplimented(){
        
        List<NucleotideGlyph> ungappedUnComplimentedFullRange = NucleotideGlyph.reverseCompliment(
                                            NucleotideGlyph.convertToUngapped(gappedValidRange));
        Range validRange = Range.buildRange(0, ungappedUnComplimentedFullRange.size());
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getSequenceDirection()).andReturn(SequenceDirection.REVERSE);
        expect(mockPlacedRead.getEncodedGlyphs()).andReturn(new DefaultNucleotideEncodedGlyphs(gappedValidRange));
        replay(mockPlacedRead);
        List<NucleotideGlyph> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void hasInvalidRange(){
        List<NucleotideGlyph> ungappedUnComplimentedFullRange = NucleotideGlyph.getGlyphsFor("RRACGTACGTKKK");
        Range validRange = Range.buildRange(2, 9);
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getSequenceDirection()).andReturn(SequenceDirection.FORWARD);
        expect(mockPlacedRead.getEncodedGlyphs()).andReturn(new DefaultNucleotideEncodedGlyphs(gappedValidRange));
        replay(mockPlacedRead);
        List<NucleotideGlyph> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        List<NucleotideGlyph> expectedGappedComplimentedFullRange = NucleotideGlyph.getGlyphsFor("RRACGT-ACGTKKK");
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
    @Test
    public void hasInvalidRangeAndUngapped(){
        List<NucleotideGlyph> ungappedUnComplimentedFullRange = NucleotideGlyph.getGlyphsFor("RRACGTACGTKKK");
        Range validRange = Range.buildRange(3, 10);
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getSequenceDirection()).andReturn(SequenceDirection.REVERSE);
        expect(mockPlacedRead.getEncodedGlyphs()).andReturn(new DefaultNucleotideEncodedGlyphs(gappedValidRange));
        replay(mockPlacedRead);
        List<NucleotideGlyph> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        List<NucleotideGlyph> expectedGappedComplimentedFullRange = NucleotideGlyph.reverseCompliment(
                                                        NucleotideGlyph.getGlyphsFor("RRACGT-ACGTKKK"));
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
}
