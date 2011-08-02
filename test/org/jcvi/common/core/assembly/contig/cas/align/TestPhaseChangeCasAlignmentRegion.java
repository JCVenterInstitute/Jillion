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

package org.jcvi.common.core.assembly.contig.cas.align;

import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhaseChangeCasAlignmentRegion {

    PhaseChangeCasAlignmentRegion sut = new PhaseChangeCasAlignmentRegion((byte)1);
    @Test
    public void getType(){
        assertEquals( CasAlignmentRegionType.PHASE_CHANGE, sut.getType());
    }
    @Test
    public void getPhaseChange(){
        assertEquals((byte)1, sut.getPhaseChange());
    }
    
    @Test
    public void sameReferenceShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesShouldBeEqual(){
        PhaseChangeCasAlignmentRegion same = new PhaseChangeCasAlignmentRegion((byte)1);        
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualToDifferentClass(){
        assertFalse(sut.equals("not a phase change"));
    }
    
    @Test
    public void differentValueShouldNotBeEqual(){
        PhaseChangeCasAlignmentRegion different = new PhaseChangeCasAlignmentRegion((byte)2);        
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
}
