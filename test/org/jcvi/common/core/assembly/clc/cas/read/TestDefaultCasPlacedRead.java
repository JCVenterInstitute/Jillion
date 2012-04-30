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

package org.jcvi.common.core.assembly.clc.cas.read;

import org.easymock.EasyMockSupport;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.clc.cas.read.DefaultCasPlacedRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultCasPlacedRead extends EasyMockSupport{

    private Read read;
    private final long startOffset = 1234;
    private final Range validRange = Range.create(5,10); 
    private final Direction dir = Direction.FORWARD;
    private final int ungappedFullLength = 10;
    
    DefaultCasPlacedRead sut;
    @Before
    public void setup(){
        read = createMock(Read.class);
        sut = new DefaultCasPlacedRead(read, startOffset, validRange, dir, ungappedFullLength);
        
    }
    
    @Test
    public void validRange(){
        assertEquals(validRange, sut.getValidRange());
    }
    @Test
    public void getUngappedFullLength(){
        assertEquals(ungappedFullLength, sut.getUngappedFullLength());
    }
    
    @Test
    public void direction(){
        assertEquals(dir, sut.getDirection());
    }
    
    @Test
    public void getStart(){
        assertEquals(startOffset, sut.getGappedContigStart());
    }
    
    @Test
    public void getId(){
        String id = "id";
        expect(read.getId()).andReturn(id);
        replayAll();
        assertEquals(id, sut.getId());
        verifyAll();
    }
    
    @Test(expected = NullPointerException.class)
    public void nullReadShouldThrowNPE(){
        new DefaultCasPlacedRead(null, startOffset, validRange, dir, ungappedFullLength);
    }
    @Test(expected = NullPointerException.class)
    public void nullValidRangeShouldThrowNPE(){
        new DefaultCasPlacedRead(read, startOffset, null, dir, ungappedFullLength);
    }
    @Test(expected = NullPointerException.class)
    public void nullDirectionShouldThrowNPE(){
        new DefaultCasPlacedRead(read, startOffset, validRange, null, ungappedFullLength);
    }
    @Test
    public void sameObjectReferenceShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        DefaultCasPlacedRead same = new DefaultCasPlacedRead(read, startOffset, validRange, dir, ungappedFullLength);
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
    @Test
    public void differentReadShouldNotBeEqual(){
        DefaultCasPlacedRead different = new DefaultCasPlacedRead(createMock(Read.class), startOffset, validRange, dir, ungappedFullLength);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualToDifferentType(){
        assertFalse(sut.equals("not a cas read"));
    }
}
