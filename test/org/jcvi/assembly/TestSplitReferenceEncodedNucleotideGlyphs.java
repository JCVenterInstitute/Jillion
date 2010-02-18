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
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestSplitReferenceEncodedNucleotideGlyphs {

    private static final String CONSENSUS_AS_STRING = "GGGGGTTTTTNNNNNNNNAAAAACCCCC";
            
    EncodedGlyphs<NucleotideGlyph> consensus = new DefaultNucleotideEncodedGlyphs(
            NucleotideGlyph.getGlyphsFor(CONSENSUS_AS_STRING),             
            Range.buildRangeOfLength(0, CONSENSUS_AS_STRING.length())
            );
    
    @Test
    public void nonNegativeOffsetShouldThrowIllegalArgumentException(){
        String bases = "AAA";
        Range range = Range.buildRange(0, 2);
        int positiveOffset = 5;
        try{
            new SplitReferenceEncodedNucleotideGlyphs(
                    consensus,bases,
                    positiveOffset,                
                    range);
            fail("should throw illegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("start offset must be < 0", e.getMessage());
        }
    }
    @Test
    public void noGaps(){
        String circularReadBases = "AAAAACCCCCGGGGGTTTTT";
        Range validRange = Range.buildRangeOfLength(0, circularReadBases.length());
        SplitReferenceEncodedNucleotideGlyphs sut = new SplitReferenceEncodedNucleotideGlyphs(
                consensus,circularReadBases,
                -10,validRange);
        
        assertEquals(circularReadBases, NucleotideGlyph.convertToString(sut.decode()));
        assertTrue(sut.getGapIndexes().isEmpty());
        assertTrue(sut.getSnps().isEmpty());
        assertEquals(circularReadBases.length(), sut.getLength());
        assertSame(validRange, sut.getValidRange());
        assertBasesDecodeCorrectly(circularReadBases, sut);
        
    }
    
    @Test
    public void leftSplitHasOneGap(){
        String circularReadBases = "AAAAA-CCCCGGGGGTTTTT";
        Range validRange = Range.buildRangeOfLength(0, circularReadBases.length()-1);
        SplitReferenceEncodedNucleotideGlyphs sut = new SplitReferenceEncodedNucleotideGlyphs(
                consensus,circularReadBases,
                -10,validRange);
        
        assertEquals(circularReadBases, NucleotideGlyph.convertToString(sut.decode()));
        assertEquals(1, sut.getGapIndexes().size());
        assertEquals(1, sut.getSnps().size());
        for(Integer index : sut.getGapIndexes()){
            assertEquals(NucleotideGlyph.Gap, sut.getSnps().get(index));
        }
        assertEquals(Integer.valueOf(5), sut.getGapIndexes().get(0));        
        assertEquals(circularReadBases.length(), sut.getLength());
        assertSame(validRange, sut.getValidRange());
        assertBasesDecodeCorrectly(circularReadBases, sut);
    }
    
    @Test
    public void leftSplitHasASnp(){
        String circularReadBases = "AAAAAMCCCCGGGGGTTTTT";
        Range validRange = Range.buildRangeOfLength(0, circularReadBases.length());
        SplitReferenceEncodedNucleotideGlyphs sut = new SplitReferenceEncodedNucleotideGlyphs(
                consensus,circularReadBases,
                -10,validRange);
        
        assertEquals(circularReadBases, NucleotideGlyph.convertToString(sut.decode()));
        assertTrue(sut.getGapIndexes().isEmpty());
        assertEquals(1, sut.getSnps().size());
        
        assertEquals(NucleotideGlyph.getGlyphFor('M'), sut.getSnps().get(Integer.valueOf(5)));       
        assertEquals(circularReadBases.length(), sut.getLength());
        assertSame(validRange, sut.getValidRange());
        assertBasesDecodeCorrectly(circularReadBases, sut);
    }
    
    @Test
    public void rightSplitHasASnp(){
        String circularReadBases = "AAAAACCCCCGGGMGTTTTT";
        Range validRange = Range.buildRangeOfLength(0, circularReadBases.length());
        SplitReferenceEncodedNucleotideGlyphs sut = new SplitReferenceEncodedNucleotideGlyphs(
                consensus,circularReadBases,
                -10,validRange);
        
        assertEquals(circularReadBases, NucleotideGlyph.convertToString(sut.decode()));
        assertTrue(sut.getGapIndexes().isEmpty());
        assertEquals(1, sut.getSnps().size());
        
        assertEquals(NucleotideGlyph.getGlyphFor('M'), sut.getSnps().get(Integer.valueOf(13)));       
        assertEquals(circularReadBases.length(), sut.getLength());
        assertSame(validRange, sut.getValidRange());
        assertBasesDecodeCorrectly(circularReadBases, sut);
    }
    
    @Test
    public void rightSplitHasOneGap(){
        String circularReadBases = "AAAAACCCCCGGG-GTTTTT";
        Range validRange = Range.buildRangeOfLength(0, circularReadBases.length()-1);
        SplitReferenceEncodedNucleotideGlyphs sut = new SplitReferenceEncodedNucleotideGlyphs(
                consensus,circularReadBases,
                -10,validRange);
        
        assertEquals(circularReadBases, NucleotideGlyph.convertToString(sut.decode()));
        assertEquals(1, sut.getGapIndexes().size());
        assertEquals(1, sut.getSnps().size());
        for(Integer index : sut.getGapIndexes()){
            assertEquals(NucleotideGlyph.Gap, sut.getSnps().get(index));
        }
        assertEquals(Integer.valueOf(13), sut.getGapIndexes().get(0));        
        assertEquals(circularReadBases.length(), sut.getLength());
        assertSame(validRange, sut.getValidRange());
        assertBasesDecodeCorrectly(circularReadBases, sut);
    }
    
    
    private void assertBasesDecodeCorrectly(String circularReadBases,
            SplitReferenceEncodedNucleotideGlyphs sut) {
        int numberOfGapsSoFar=0;
        for(int i=0; i< circularReadBases.length(); i++){
            final NucleotideGlyph base = NucleotideGlyph.getGlyphFor(circularReadBases.charAt(i));
            assertEquals(i +" did not decode correctly",
                    base, 
                    sut.get(i) );    
            if(base.isGap()){
                numberOfGapsSoFar++; 
                assertTrue(sut.isGap(i));
            }
            else{
                int ungappedIndex = i-numberOfGapsSoFar;
                assertEquals(ungappedIndex, sut.convertGappedValidRangeIndexToUngappedValidRangeIndex(i));
                assertEquals(i, sut.convertUngappedValidRangeIndexToGappedValidRangeIndex(ungappedIndex));
            }
        }
    }
}
