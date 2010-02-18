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
