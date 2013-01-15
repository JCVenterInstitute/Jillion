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

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAssemblyUtil_reverseComplimentRange {

    private final Range range = Range.of(0, 9);
    @Test
    public void reverseFullRange(){
        assertEquals(range, AssemblyUtil.reverseComplementValidRange(range, range.getLength()));
    }
    
    @Test
    public void reverse(){
        Range expectedRange = Range.of(5,14);
        assertEquals(expectedRange, AssemblyUtil.reverseComplementValidRange(range, 15));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void fullLengthSmallerThanValidRangeShouldThrowIllegalArgumentException(){
        AssemblyUtil.reverseComplementValidRange(range, range.getLength()-1);
    }
    
    @Test
    public void validRangeInMiddleOfFullRange(){
        Range validRange = Range.of(5,9);
       assertEquals(Range.of(10,14), AssemblyUtil.reverseComplementValidRange(validRange, 20));
    }
}
