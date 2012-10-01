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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.DefaultAceBaseSegment;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultAceBestSegment {

    String name = "name";
    Range range = Range.of(1,10);
    DefaultAceBaseSegment sut = new DefaultAceBaseSegment(name, range);
    
    @Test(expected = NullPointerException.class)
    public void nullNameShouldThrowNPE(){
        new DefaultAceBaseSegment(null, range);
    }
    @Test(expected = NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        new DefaultAceBaseSegment(name,null);
    }
    
    @Test
    public void constructor(){
        assertEquals(name, sut.getReadName());
        assertEquals(range, sut.getGappedConsensusRange());
    }
    @Test
    public void differentClassNotEqual(){
        assertFalse(sut.equals("not a best segment"));
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void sameRefIsEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesIsEqual(){
        DefaultAceBaseSegment sameValues = new DefaultAceBaseSegment(name, range);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentNameIsNotEqual(){
        DefaultAceBaseSegment differentName = new DefaultAceBaseSegment("different"+name, range);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentName);
    }
    
    @Test
    public void differentRangeIsNotEqual(){
        DefaultAceBaseSegment differentRange = new DefaultAceBaseSegment(name, new Range.Builder(range).shiftLeft(1).build());
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRange);
    }
}
