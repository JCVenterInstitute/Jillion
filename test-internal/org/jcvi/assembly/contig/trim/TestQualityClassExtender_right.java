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
 * Created on Oct 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestQualityClassExtender_right {
    private static final List<NucleotideGlyph> DECODED_FULL_RANGE_BASES = NucleotideGlyph.getGlyphsFor("ACGTTCGA");
    QualityClassExtender<PlacedRead> sut = new QualityClassExtender<PlacedRead>();
    NucleotideEncodedGlyphs mockReference,mockValidRangeBasecalls;
    PlacedRead mockRead;
    EncodedGlyphs<NucleotideGlyph> mockFullRangeBasecalls ;
    Range clv, currentRange;
    long fullLength =DECODED_FULL_RANGE_BASES.size();
    @Before
    public void setup(){
        mockReference = createMock(NucleotideEncodedGlyphs.class);
        mockRead = createMock(PlacedRead.class);
        mockFullRangeBasecalls = createMock(EncodedGlyphs.class);
        mockValidRangeBasecalls = createMock(NucleotideEncodedGlyphs.class);
    }
    @Test
    public void nextRightIsMismatchShouldNotExtend(){        
        nextRightIsMismatchedShouldNotExtend(SequenceDirection.FORWARD, DECODED_FULL_RANGE_BASES);
    }
    @Test
    public void reverseComplimentNextRightIsMismatchShouldNotExtend(){        
        nextRightIsMismatchedShouldNotExtend(SequenceDirection.REVERSE, 
                NucleotideGlyph.reverseCompliment(DECODED_FULL_RANGE_BASES));
    }
    private void nextRightIsMismatchedShouldNotExtend(SequenceDirection dir,
            List<NucleotideGlyph> fullRangeDecodedFasta) {
        clv= Range.buildRange(2,6);
        currentRange = Range.buildRange(3,5);
        Range rangeTobeUsed = currentRange;
        if(dir==SequenceDirection.REVERSE){
            clv = AssemblyUtil.reverseComplimentValidRange(clv, fullLength);
            rangeTobeUsed = AssemblyUtil.reverseComplimentValidRange(currentRange, fullLength);
        }
        long endOffset= 3;
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(dir);
        expect(mockRead.getEnd()).andStubReturn(endOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(fullRangeDecodedFasta);
        expect(mockValidRangeBasecalls
                .convertGappedValidRangeIndexToUngappedValidRangeIndex((int)rangeTobeUsed.getEnd()))
                .andReturn(2);
        expect(mockReference.get((int)(endOffset+1))).andReturn(NucleotideGlyph.getGlyphFor('T'));
        
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewRight =sut.extendRight(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
        assertEquals(currentRange.getEnd(), actualNewRight);
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    
    @Test
    public void nextRightIsMatchShouldExtend(){
        nextRightIsMatchShouldExtend(SequenceDirection.FORWARD, DECODED_FULL_RANGE_BASES);
    }
    @Test
    public void reverseComplimentNextRightIsMatchShouldExtend(){
        nextRightIsMatchShouldExtend(SequenceDirection.REVERSE, 
                NucleotideGlyph.reverseCompliment(DECODED_FULL_RANGE_BASES));
    }
    
    private void nextRightIsMatchShouldExtend(SequenceDirection dir,
            List<NucleotideGlyph> fullRangeDecodedFasta) {
        clv= Range.buildRange(2,6);
        currentRange = Range.buildRange(3,5);
        long endOffset= 3;
        Range rangeTobeUsed = currentRange;
        if(dir==SequenceDirection.REVERSE){
            clv = AssemblyUtil.reverseComplimentValidRange(clv, (int)fullLength);
            rangeTobeUsed = AssemblyUtil.reverseComplimentValidRange(currentRange, fullRangeDecodedFasta.size());
            expect(mockReference.get((int)(endOffset+2))).andReturn(NucleotideGlyph.getGlyphFor('T'));
        }
        
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(dir);
        expect(mockRead.getEnd()).andStubReturn(endOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(fullRangeDecodedFasta);
        expect(mockValidRangeBasecalls
                .convertGappedValidRangeIndexToUngappedValidRangeIndex((int)rangeTobeUsed.getEnd()))
                .andReturn(2);
        expect(mockReference.get((int)(endOffset+1))).andReturn(NucleotideGlyph.getGlyphFor('G'));
        
        
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewRight =sut.extendRight(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
        assertEquals(currentRange.getEnd()+1, actualNewRight);
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    @Test
    public void nextMultipleRightsAreMatchShouldExtend(){
        clv= Range.buildRange(2,6);
        currentRange = Range.buildRange(3,4);
       
        long endOffset= 2;
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(SequenceDirection.FORWARD);
        expect(mockRead.getEnd()).andStubReturn(endOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(DECODED_FULL_RANGE_BASES);
        expect(mockValidRangeBasecalls
                .convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       (int)currentRange.getEnd()))
                .andReturn((int)currentRange.size()-1);
        expect(mockReference.get((int)(endOffset+1))).andReturn(NucleotideGlyph.getGlyphFor('C'));
        expect(mockReference.get((int)(endOffset+2))).andReturn(NucleotideGlyph.getGlyphFor('G'));        
       
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewRight =sut.extendRight(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
            assertEquals(currentRange.getEnd()+2, actualNewRight);
        
        
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    
    private void nextMultipleRightsAreMatchShouldExtend(SequenceDirection dir,
            List<NucleotideGlyph> fullRangeDecodedFasta){
        clv= Range.buildRange(2,6);
        currentRange = Range.buildRange(3,4);
       
        long endOffset= 2;
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(dir);
        expect(mockRead.getEnd()).andStubReturn(endOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(fullRangeDecodedFasta);
        expect(mockValidRangeBasecalls
                .convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       (int)currentRange.getEnd()))
                .andReturn((int)currentRange.size()-1);
        expect(mockReference.get((int)(endOffset+1))).andReturn(NucleotideGlyph.getGlyphFor('C'));
        expect(mockReference.get((int)(endOffset+2))).andReturn(NucleotideGlyph.getGlyphFor('G'));        
       
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewRight =sut.extendRight(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
            assertEquals(currentRange.getEnd()+2, actualNewRight);
        
        
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    //ignore for now because extender doesn't work yet...
    @Ignore @Test
    public void reverseComplimentNextMultipleRightsAreMatchShouldExtend(){
        clv= AssemblyUtil.reverseComplimentValidRange(Range.buildRange(2,6), (int)fullLength);
        currentRange = Range.buildRange(3,4);
       
        long endOffset= 2;
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(SequenceDirection.REVERSE);
        expect(mockRead.getEnd()).andStubReturn(endOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(NucleotideGlyph.reverseCompliment(DECODED_FULL_RANGE_BASES));
        expect(mockValidRangeBasecalls
                .convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       5))
                .andReturn(5);
        expect(mockReference.get((int)(endOffset+1))).andReturn(NucleotideGlyph.getGlyphFor('C'));
        expect(mockReference.get((int)(endOffset+2))).andReturn(NucleotideGlyph.getGlyphFor('G'));        
       
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewRight =sut.extendRight(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
            assertEquals(currentRange.getEnd()+2, actualNewRight);
        
        
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    
}
