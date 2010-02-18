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
