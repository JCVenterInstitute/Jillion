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
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.DefaultCasAlignmentRegion;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultCasAlignmentRegion {
    
    long length = 12345L;
    
    DefaultCasAlignmentRegion sut = new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, length);
    @Test
    public void constructor(){
        assertEquals(length, sut.getLength());
        assertEquals(CasAlignmentRegionType.INSERT, sut.getType());
    }
    
    @Test(expected = NullPointerException.class)
    public void nullTypeShouldThrowNPE(){
        new DefaultCasAlignmentRegion(null, length);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void negativeLengthShouldThrowIllegalArgumentException(){
        new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, -1);
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        DefaultCasAlignmentRegion same = new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, length);
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
    
    @Test
    public void differentLengthShouldNotBeEqual(){
        DefaultCasAlignmentRegion differentLength = new DefaultCasAlignmentRegion(CasAlignmentRegionType.INSERT, length+10);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentLength);
    }
    
    @Test
    public void differentTypeShouldNotBeEqual(){
        DefaultCasAlignmentRegion differentType = new DefaultCasAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, length);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentType);
    }
    @Test
    public void differentClassShouldNotBeEqual(){
        assertFalse(sut.equals("not a alignment region"));
    }
    
    @Test
    public void shouldNotBeEqualToNull(){
        assertFalse(sut.equals(null));
    }
}
