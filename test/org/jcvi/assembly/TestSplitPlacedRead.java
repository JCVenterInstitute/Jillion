/*
 * Created on Apr 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestSplitPlacedRead {

    private Read<ReferencedEncodedNucleotideGlyphs> read;
    private PlacedRead leftOfOrigin, rightOfOrigin;
    private long start = -300L;
    private SequenceDirection dir= SequenceDirection.FORWARD;
    
    SplitPlacedRead sut;
    
    @Before
    public void setup(){
        read = createMock(Read.class);
        leftOfOrigin = createMock(PlacedRead.class);
        rightOfOrigin = createMock(PlacedRead.class);
        sut = new SplitPlacedRead(read, start, dir, leftOfOrigin, rightOfOrigin);        
    }
    
    @Test
    public void constructor(){
        assertEquals(read, sut.getRead());
        assertEquals(start, sut.getStart());
        assertEquals(dir, sut.getSequenceDirection());
        assertEquals(leftOfOrigin, sut.getLeftOfOrigin());
        assertEquals(rightOfOrigin, sut.getRightOfOrigin());
    }
}
