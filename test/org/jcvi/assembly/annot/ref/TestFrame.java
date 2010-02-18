/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import org.jcvi.assembly.annot.Frame;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFrame {

    @Test
    public void validParse(){
        assertEquals(Frame.NO_FRAME, Frame.parseFrame(-1));
        assertEquals(Frame.ZERO, Frame.parseFrame(0));
        assertEquals(Frame.ONE, Frame.parseFrame(1));
        assertEquals(Frame.TWO, Frame.parseFrame(2));
    }
    
    @Test
    public void parseInvalidShouldThrowIllegalArgumentException(){
        assertExceptionIsThrownFor(-2);
        assertExceptionIsThrownFor(3);
    }

    private void assertExceptionIsThrownFor(int frame) {
        try{
            Frame.parseFrame(frame);
            fail("should throw IllegalArgumentException when frame = "+ frame);
        }
        catch(IllegalArgumentException e){
            assertEquals("unable to parse frame "+ frame, e.getMessage());
        }
    }
}
