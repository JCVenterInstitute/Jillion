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

package org.jcvi.assembly.ace.consed;

import org.jcvi.Range;
import org.jcvi.assembly.ace.consed.NavigationElement.Type;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsensusNavigationElement {

    String id= "contig id";
    Range range = Range.buildRange(5,10);
    String comment = "this is a comment";
    ConsensusNavigationElement sut = new ConsensusNavigationElement(id, range,comment);
    
    @Test(expected = NullPointerException.class)
    public void nullIdShouldThrowNPE(){
        new ConsensusNavigationElement(null, range);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        new ConsensusNavigationElement(id, null);
    }
    
    @Test
    public void noComment(){
        ConsensusNavigationElement element = new ConsensusNavigationElement(id, range);
        assertEquals(id, element.getTargetId());
        assertEquals(range, element.getUngappedPositionRange());
        assertEquals(Type.CONSENSUS, element.getType());
        assertNull(element.getComment());
    }
    
    @Test
    public void withComment(){
        assertEquals(id, sut.getTargetId());
        assertEquals(range, sut.getUngappedPositionRange());
        assertEquals(Type.CONSENSUS, sut.getType());
        assertEquals(comment,sut.getComment());
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        ConsensusNavigationElement sameValues = new ConsensusNavigationElement(id, range,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentIdShouldNotBeEqual(){
        ConsensusNavigationElement differentId = new ConsensusNavigationElement("different"+id, range,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    @Test
    public void differentRangeShouldNotBeEqual(){
        ConsensusNavigationElement differentRange = new ConsensusNavigationElement(id, range.shrink(0, 2),comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRange);
    }
    @Test
    public void differentCommentShouldNotBeEqual(){
        ConsensusNavigationElement differentComment = new ConsensusNavigationElement(id, range,"different"+comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentComment);
    }
    @Test
    public void differentTypeShouldNotBeEqual(){
        ReadNavigationElement differentType = new ReadNavigationElement(id, range,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentType);
    }
    @Test
    public void noCommentShouldNotBeEqual(){
        ConsensusNavigationElement noComment = new ConsensusNavigationElement(id, range);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, noComment);
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentObj(){
        assertFalse(sut.equals("not a navigation element"));
    }
}
