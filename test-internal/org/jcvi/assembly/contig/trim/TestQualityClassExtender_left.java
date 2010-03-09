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

import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.ReadTrimUtil;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestQualityClassExtender_left {

    private static final List<NucleotideGlyph> DECODED_FULL_RANGE_BASES = NucleotideGlyph.getGlyphsFor("ACGTTCGA");
    QualityClassExtender<PlacedRead> sut = new QualityClassExtender<PlacedRead>();
    NucleotideEncodedGlyphs mockReference,mockValidRangeBasecalls;
    PlacedRead mockRead;
    EncodedGlyphs<NucleotideGlyph> mockFullRangeBasecalls ;
    Range clv, currentRange;
    long fullLength =8;
    @Before
    public void setup(){
        mockReference = createMock(NucleotideEncodedGlyphs.class);
        mockRead = createMock(PlacedRead.class);
        mockFullRangeBasecalls = createMock(EncodedGlyphs.class);
        mockValidRangeBasecalls = createMock(NucleotideEncodedGlyphs.class);
    }
    @Ignore @Test
    public void nextLeftIsMismatchShouldNotExtend(){        
        nextLeftIsMismatchedShouldNotExtend(SequenceDirection.FORWARD, DECODED_FULL_RANGE_BASES);
    }
    @Test
    public void reverseComplimentNextLeftIsMismatchShouldNotExtend(){        
        nextLeftIsMismatchedShouldNotExtend(SequenceDirection.REVERSE, 
                NucleotideGlyph.reverseCompliment(DECODED_FULL_RANGE_BASES));
    }
    @Test
    public void nextLeftIsMatchShouldExtend(){
        nextLeftIsMatchShouldExtend(SequenceDirection.FORWARD, DECODED_FULL_RANGE_BASES);
    }
    @Ignore @Test
    public void reverseComplimentNextLeftIsMatchShouldExtend(){
        nextLeftIsMatchShouldExtend(SequenceDirection.REVERSE, 
                NucleotideGlyph.reverseCompliment(DECODED_FULL_RANGE_BASES));
    }
    private void nextLeftIsMatchShouldExtend(SequenceDirection dir,
            List<NucleotideGlyph> fullRangeDecodedFasta) {
        clv= Range.buildRange(2,6);
        currentRange = Range.buildRange(3,5);
        Range rangeToUse = currentRange;
        long startOffset= 1;
        if(dir==SequenceDirection.REVERSE){
            clv = AssemblyUtil.reverseComplimentValidRange(clv, fullLength);
            rangeToUse = AssemblyUtil.reverseComplimentValidRange(currentRange, fullRangeDecodedFasta.size());
        }else{
            expect(mockReference.get((int)(startOffset-1))).andReturn(NucleotideGlyph.getGlyphFor('G'));
            
        }
        
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(dir);
        expect(mockRead.getStart()).andStubReturn(startOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(fullRangeDecodedFasta);
        expect(mockValidRangeBasecalls.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)rangeToUse.getStart())).andReturn(0);
       
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewLeft =sut.extendLeft(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
        assertEquals(clv.getStart(), actualNewLeft);
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    @Test
    public void nextMultipleLeftsAreMatchShouldExtend(){
        nextMultipleLeftsAreMatchShouldExtend(SequenceDirection.FORWARD, DECODED_FULL_RANGE_BASES);
    }
    
    @Ignore @Test
    public void reverseComplimentNextMultipleLeftsAreMatchShouldExtend(){
        nextMultipleLeftsAreMatchShouldExtend(SequenceDirection.REVERSE, 
                NucleotideGlyph.reverseCompliment(DECODED_FULL_RANGE_BASES));
    }
    private void nextMultipleLeftsAreMatchShouldExtend(SequenceDirection dir,
            List<NucleotideGlyph> fullRangeDecodedFasta) {
        clv= Range.buildRange(1,6);
        currentRange = Range.buildRange(3,5);
        if(dir==SequenceDirection.REVERSE){
            clv = AssemblyUtil.reverseComplimentValidRange(clv, (int)fullLength);
            currentRange = AssemblyUtil.reverseComplimentValidRange(currentRange, fullRangeDecodedFasta.size());
        }
        long startOffset= 2;
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(dir);
        expect(mockRead.getStart()).andStubReturn(startOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(fullRangeDecodedFasta);
        expect(mockValidRangeBasecalls.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)currentRange.getStart())).andReturn(0);
        expect(mockReference.get((int)(startOffset-1))).andReturn(NucleotideGlyph.getGlyphFor('G'));
        expect(mockReference.get((int)(startOffset-2))).andReturn(NucleotideGlyph.getGlyphFor('C'));
        
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewLeft =sut.extendLeft(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
        assertEquals(clv.getStart(), actualNewLeft);
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    @Test
    public void nextLeftIsOutsideClvShouldNotExtend(){
        SequenceDirection dir =SequenceDirection.FORWARD;
        List<NucleotideGlyph> fullRangeDecodedFasta = DECODED_FULL_RANGE_BASES;
        nextLeftIsOutisdeCLVShouldNotExtend(dir, fullRangeDecodedFasta);
    }
    @Ignore @Test
    public void reverseComplimentNextLeftIsOutsideClvShouldNotExtend(){
        SequenceDirection dir =SequenceDirection.REVERSE;
        List<NucleotideGlyph> fullRangeDecodedFasta = NucleotideGlyph.reverseCompliment(DECODED_FULL_RANGE_BASES);
        nextLeftIsOutisdeCLVShouldNotExtend(dir, fullRangeDecodedFasta);
    }
    private void nextLeftIsOutisdeCLVShouldNotExtend(SequenceDirection dir,
            List<NucleotideGlyph> fullRangeDecodedFasta) {
        currentRange = Range.buildRange(3,5);
        clv= currentRange;
        if(dir==SequenceDirection.REVERSE){
            clv = AssemblyUtil.reverseComplimentValidRange(clv, (int)fullLength);
            currentRange = AssemblyUtil.reverseComplimentValidRange(currentRange, fullRangeDecodedFasta.size());
        }
        long startOffset= 1;
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(dir);
        expect(mockRead.getStart()).andStubReturn(startOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(fullRangeDecodedFasta);
        expect(mockValidRangeBasecalls.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)currentRange.getStart())).andReturn(0);
        
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewLeft =sut.extendLeft(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
        assertEquals(currentRange.getStart(), actualNewLeft);
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
    
   
    private void nextLeftIsMismatchedShouldNotExtend(SequenceDirection dir,
            List<NucleotideGlyph> fullRangeDecodedFasta) {
        clv= Range.buildRange(2,6);
        currentRange = Range.buildRange(3,5);
        Range rangeToUse = currentRange;
        if(dir==SequenceDirection.REVERSE){
            clv = AssemblyUtil.reverseComplimentValidRange(clv, (int)fullLength);
            rangeToUse = AssemblyUtil.reverseComplimentValidRange(currentRange, fullRangeDecodedFasta.size());
        }
        long startOffset= 1;
        expect(mockFullRangeBasecalls.getLength()).andReturn(fullLength);
        expect(mockRead.getValidRange()).andStubReturn(currentRange);
        expect(mockRead.getSequenceDirection()).andStubReturn(dir);
        expect(mockRead.getStart()).andStubReturn(startOffset);
        expect(mockRead.getEncodedGlyphs()).andReturn(mockValidRangeBasecalls);
        expect(mockFullRangeBasecalls.decode())
                .andReturn(fullRangeDecodedFasta);
        expect(mockValidRangeBasecalls.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)rangeToUse.getStart())).andReturn(0);
       // expect(mockReference.get((int)(startOffset-1))).andReturn(NucleotideGlyph.getGlyphFor('A'));
        
        replay(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
        int actualNewLeft =sut.extendLeft(mockReference, mockRead, mockFullRangeBasecalls, clv, currentRange);
        assertEquals(currentRange.getStart(), actualNewLeft);
        verify(mockReference, mockRead, mockFullRangeBasecalls,mockValidRangeBasecalls);
    }
}
