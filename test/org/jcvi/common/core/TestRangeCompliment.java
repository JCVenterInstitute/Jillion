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

    Range range = Range.buildRange(0,10);
    
    @Test
    public void complimentEmptyShouldReturnEmpty(){
        assertTrue(range.complimentFrom(Collections.<Range>emptyList()).isEmpty());
    }
    @Test
    public void complimentWithItselfShouldReturnEmpty(){
        assertTrue(range.complimentFrom(Collections.singleton(range)).isEmpty());
    }
    
    @Test
    public void oneLargeRangeShouldGetSplit(){
        List<Range> expected = Arrays.asList(
                Range.buildRange(-10, -1),
                Range.buildRange(11, 20)
                );
        assertEquals(expected, range.complimentFrom(Collections.singleton(
                Range.buildRange(-10,20))));
    }
    
    @Test
    public void oneSideOverhangsShouldReturnThatSide(){
        Range largeRange = Range.buildRange(0,20);
        List<Range> expected = Arrays.asList(Range.buildRange(11,20));
        assertEquals(expected, range.complimentFrom(Collections.singleton(largeRange)));
    }
    
    @Test
    public void twoOverlappingRanges(){
        Collection<Range> ranges = Arrays.asList(
                    Range.buildRange(0,20),
                    Range.buildRange(-5,10))
                    ;
        List<Range> expected = Arrays.asList(
                Range.buildRange(-5,-1),
                Range.buildRange(11,20)
                );
        assertEquals(expected, range.complimentFrom(ranges));
  
    }
}
