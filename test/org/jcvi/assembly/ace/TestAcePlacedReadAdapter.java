/*
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Date;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

public class TestAcePlacedReadAdapter {

    PlacedRead mockPlacedRead;
    String id = "readId";
    Date date = new Date(123456789L);
    PhdInfo phdInfo = new DefaultPhdInfo(id, id+".phd.1", date);
    AcePlacedReadAdapter sut;
    long referenceIndex = 1234;
    long validRangeIndex = 7;
    Range validRange = Range.buildRange(1,10);
    @Before
    public void setup(){
        mockPlacedRead = createMock(PlacedRead.class);
        expect(mockPlacedRead.getId()).andReturn(id);
        replay(mockPlacedRead);
        sut = new AcePlacedReadAdapter(mockPlacedRead, date,null);
        reset(mockPlacedRead);
    }
    
    @Test
    public void constructor(){
        assertEquals(phdInfo,sut.getPhdInfo());
    }
    @Test
    public void convertReferenceIndexToValidRangeIndex() {
        
        expect(mockPlacedRead.convertReferenceIndexToValidRangeIndex(referenceIndex)).andReturn(validRangeIndex);
        replay(mockPlacedRead);
        assertEquals(validRangeIndex, sut.convertReferenceIndexToValidRangeIndex(referenceIndex));
        verify(mockPlacedRead);
    }
    @Test
    public void convertValidRangeIndexToReferenceIndex() {
        expect(mockPlacedRead.convertValidRangeIndexToReferenceIndex(validRangeIndex)).andReturn(referenceIndex);
        replay(mockPlacedRead);
        assertEquals(referenceIndex, sut.convertValidRangeIndexToReferenceIndex(validRangeIndex));
        verify(mockPlacedRead);
    }
    @Test
    public void getSequenceDirection() {
        SequenceDirection direction = SequenceDirection.REVERSE;
        expect(mockPlacedRead.getSequenceDirection()).andReturn(direction);
        replay(mockPlacedRead);
        assertEquals(direction, sut.getSequenceDirection());
        verify(mockPlacedRead);
    }
    @Test
    public void getSnps() {
        Map<Integer,NucleotideGlyph> snpMap = createMock(Map.class);
        expect(mockPlacedRead.getSnps()).andReturn(snpMap);
        replay(mockPlacedRead);
        assertEquals(snpMap, sut.getSnps());
        verify(mockPlacedRead);
    }
    @Test
    public void getValidRange() {
        Range validRange = Range.buildRange(1,10);
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        replay(mockPlacedRead);
        assertEquals(validRange, sut.getValidRange());
        verify(mockPlacedRead);
    }
    @Test
    public void getEncodedGlyphs() {
        NucleotideEncodedGlyphs encodedGlyphs = createMock(NucleotideEncodedGlyphs.class);
        expect(mockPlacedRead.getEncodedGlyphs()).andReturn(encodedGlyphs);
        replay(mockPlacedRead);
        assertEquals(encodedGlyphs, sut.getEncodedGlyphs());
        verify(mockPlacedRead);
        
    }
    @Test
    public void getId() {
        expect(mockPlacedRead.getId()).andReturn(id);
        replay(mockPlacedRead);
        assertEquals(id, sut.getId());
        verify(mockPlacedRead);
    }
    @Test
    public void getLength() {

        expect(mockPlacedRead.getLength()).andReturn(validRange.getLength());
        replay(mockPlacedRead);
        assertEquals(validRange.getLength(), sut.getLength());
        verify(mockPlacedRead);
    }
    @Test
    public void getEnd() {
        expect(mockPlacedRead.getEnd()).andReturn(validRange.getEnd());
        replay(mockPlacedRead);
        assertEquals(validRange.getEnd(), sut.getEnd());
        verify(mockPlacedRead);
    }
    @Test
    public void getStart() {
        expect(mockPlacedRead.getStart()).andReturn(validRange.getStart());
        replay(mockPlacedRead);
        assertEquals(validRange.getStart(), sut.getStart());
        verify(mockPlacedRead);
    } 
    
}
