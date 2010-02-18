/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Map;

import org.jcvi.Range;
import org.jcvi.TestUtil;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultPlacedRead {

    Read<ReferencedEncodedNucleotideGlyphs> read;
    SequenceDirection dir = SequenceDirection.FORWARD;
    long start = 100;
    
    DefaultPlacedRead sut ;
    @Before
    public void setup(){
        read = createMock(Read.class);
        sut = new DefaultPlacedRead(read, start,dir);
    }
    @Test
    public void constructor(){
        String id = "id";
        long length = 200L;
        Range validRange = Range.buildRange(start, length);
        ReferencedEncodedNucleotideGlyphs glyphs = createMock(ReferencedEncodedNucleotideGlyphs.class);
        Map<Integer, NucleotideGlyph> snps = createMock(Map.class);
        expect(read.getId()).andReturn(id);
        expect(read.getEncodedGlyphs()).andReturn(glyphs).times(3);
        expect(read.getLength()).andReturn(length).times(2);
        expect(glyphs.getValidRange()).andReturn(validRange);
        expect(glyphs.getSnps()).andReturn(snps);
        replay(read, glyphs);
        assertEquals(dir,sut.getSequenceDirection());
        assertEquals(start, sut.getStart());
        assertEquals(read, sut.getRead());
        assertEquals(id, sut.getId());
        assertEquals(glyphs, sut.getEncodedGlyphs());
        assertEquals(length, sut.getLength());
        assertEquals(start+ length-1 , sut.getEnd());
        assertEquals(validRange, sut.getValidRange());
        assertEquals(snps, sut.getSnps());
        verify(read, glyphs);        
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a DefaultPlacedRead"));
    }
    @Test
    public void sameRefIsEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesAreEqual(){
        PlacedRead sameValues =  new DefaultPlacedRead(read, start,dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentReadIsNotEqual(){
        Read differentRead = createMock(Read.class);
        PlacedRead hasDifferentRead =  new DefaultPlacedRead(differentRead, start,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentStartIsNotEqual(){
        PlacedRead hasDifferentStart =  new DefaultPlacedRead(read, start-1,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentStart);
    }
    @Test
    public void nullReadThrowsIllegalArgumentException(){
        try{
            new DefaultPlacedRead(null, start,dir);
            fail("should throw IllegalArgument exception when passed read is null");
        }catch(IllegalArgumentException e){
            assertEquals("read can not be null", e.getMessage());
        }
    }
    
    @Test
    public void testToString(){
        String expected = "offset = "+ start + " complimented? "+ dir+"  " + read;
        assertEquals(expected, sut.toString());
    }
}
