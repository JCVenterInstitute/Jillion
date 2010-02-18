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
