/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import org.jcvi.TestUtil;
import org.jcvi.assembly.annot.DefaultExon;
import org.jcvi.assembly.annot.Frame;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultExon {

    Frame frame = Frame.ONE;
    Frame otherFrame = Frame.NO_FRAME;
    
    int start = 10;
    int end = 500;
    
    DefaultExon sut = new DefaultExon(frame, start, end);
    
    @Test
    public void constructor(){
        assertEquals(frame, sut.getFrame());
        assertEquals(start, sut.getStartPosition());
        assertEquals(end, sut.getEndPosition());
    }
    @Test
    public void nullFrameThrowsIllegalArgumentException(){
        try{
            new DefaultExon(null, start, end);
            fail("should throw IllegalArgumentException when frame = null");
        }
        catch(IllegalArgumentException e){
            assertEquals("frame can not be null", e.getMessage());
        }
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualsNotADefaultExon(){
        assertFalse(sut.equals("not a Default Exon"));
    }
    
    @Test
    public void equalsSameValues(){
        DefaultExon sameValues = new DefaultExon(frame, start, end);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void notEqualsDifferentFrame(){
        DefaultExon differentFrame = new DefaultExon(otherFrame, start, end);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentFrame);
    }
    @Test
    public void notEqualsDifferentStart(){
        DefaultExon differentStart = new DefaultExon(otherFrame, start+1, end);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentStart);
    }
    @Test
    public void notEqualsDifferentEnd(){
        DefaultExon differentEnd = new DefaultExon(otherFrame, start, end+1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentEnd);
    }
}
