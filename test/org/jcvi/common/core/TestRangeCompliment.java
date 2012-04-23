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

package org.jcvi.common.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestRangeCompliment {

    Range range = Range.create(0,10);
    
    @Test
    public void complementEmptyShouldReturnEmpty(){
        assertTrue(range.complementFrom(Collections.<Range>emptyList()).isEmpty());
    }
    @Test
    public void complementWithItselfShouldReturnEmpty(){
        assertTrue(range.complementFrom(Collections.singleton(range)).isEmpty());
    }
    
    @Test
    public void oneLargeRangeShouldGetSplit(){
        List<Range> expected = Arrays.asList(
                Range.create(-10, -1),
                Range.create(11, 20)
                );
        assertEquals(expected, range.complementFrom(Collections.singleton(
                Range.create(-10,20))));
    }
    
    @Test
    public void oneSideOverhangsShouldReturnThatSide(){
        Range largeRange = Range.create(0,20);
        List<Range> expected = Arrays.asList(Range.create(11,20));
        assertEquals(expected, range.complementFrom(Collections.singleton(largeRange)));
    }
    
    @Test
    public void twoOverlappingRanges(){
        Collection<Range> ranges = Arrays.asList(
                    Range.create(0,20),
                    Range.create(-5,10))
                    ;
        List<Range> expected = Arrays.asList(
                Range.create(-5,-1),
                Range.create(11,20)
                );
        assertEquals(expected, range.complementFrom(ranges));
  
    }
}
