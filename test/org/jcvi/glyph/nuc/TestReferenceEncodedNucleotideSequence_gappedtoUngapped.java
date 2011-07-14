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
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import org.jcvi.Range;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestReferenceEncodedNucleotideSequence_gappedtoUngapped {

    String reference       = "ACGTACGTTACGTTACGT";
    String gappedBasecalls = "ACGTACGT-ACGTACG-T";
    String ungappedBasecalls = "ACGTACGTACGTACGT";
    DefaultNucleotideGlyphCodec codec = DefaultNucleotideGlyphCodec.getInstance();
    NucleotideSequence encodedReference = new DefaultNucleotideSequence(reference);
    ReferenceEncodedNucleotideSequence sut = new ReferenceEncodedNucleotideSequence(encodedReference, gappedBasecalls,0, Range.buildRange(0, gappedBasecalls.length()-1));
    
    
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
        assertEquals(2, sut.getNumberOfGaps());
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
        assertEquals(numberOfGaps, sut.getNumberOfGaps());
    }
    
    
}
