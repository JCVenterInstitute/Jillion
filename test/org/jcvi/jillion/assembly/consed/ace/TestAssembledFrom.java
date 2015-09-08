/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Feb 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.consed.ace.AlignedReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAssembledFrom {

    Direction dir = Direction.FORWARD;
    int offset = 12345;
    
    AlignedReadInfo sut = new AlignedReadInfo(offset, dir);
    
    @Test
    public void constructor(){
        assertEquals(offset, sut.getStartOffset());
        assertEquals(dir, sut.getDirection());
    }
    @Test(expected = NullPointerException.class)
    public void nullDirectionShouldThrowNPE(){
    	new AlignedReadInfo(offset,null);
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
    public void differentClassNotEquals(){
        assertFalse(sut.equals("not an AssembledFrom"));
    }
    
    @Test
    public void equalsSameValues(){
        AlignedReadInfo sameValues = new AlignedReadInfo(offset, dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
  

    @Test
    public void differentOffsetShouldNotBeEqual(){
        AlignedReadInfo differentOffset = new AlignedReadInfo(offset+1, dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentOffset);
    }

    @Test
    public void differentComlimentShouldNotBeEqual(){
        AlignedReadInfo differentCompliment = new AlignedReadInfo(offset, Direction.REVERSE);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentCompliment);
    }
    
    @Test
    public void testToString(){
        String expected = "AlignedReadInfo [dir=" + dir + ", startOffset=" + offset
				+ "]";
        assertEquals(expected, sut.toString());
    }
}
