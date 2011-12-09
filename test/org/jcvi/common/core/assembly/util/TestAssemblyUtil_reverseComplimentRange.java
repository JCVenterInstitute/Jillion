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
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAssemblyUtil_reverseComplimentRange {

    private final Range range = Range.buildRange(0, 9);
    @Test
    public void reverseFullRange(){
        assertEquals(range, AssemblyUtil.reverseComplimentValidRange(range, range.size()));
    }
    
    @Test
    public void reverse(){
        Range expectedRange = Range.buildRange(5,14);
        assertEquals(expectedRange, AssemblyUtil.reverseComplimentValidRange(range, 15));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void fullLengthSmallerThanValidRangeShouldThrowIllegalArgumentException(){
        AssemblyUtil.reverseComplimentValidRange(range, range.size()-1);
    }
    
    @Test
    public void keepCoordinateSystem(){
        Range spacedBasedRange = range.convertRange(CoordinateSystem.SPACE_BASED);
        assertEquals(spacedBasedRange, AssemblyUtil.reverseComplimentValidRange(spacedBasedRange, range.size()));
    }
    
    @Test
    public void validRangeInMiddleOfFullRange(){
        Range validRange = Range.buildRange(5,9);
       assertEquals(Range.buildRange(10,14), AssemblyUtil.reverseComplimentValidRange(validRange, 20));
    }
}
