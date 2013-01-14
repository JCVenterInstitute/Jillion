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

package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Before;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSizeRangeComparator {

    Range small = new Range.Builder(10).build();
    Range medium = new Range.Builder(-10, 30).build();
    Range large = new Range.Builder(-50, 100).build();
    
    List<Range> ranges;
    @Before
    public void setup(){
        ranges = new ArrayList<Range>(3);
        ranges.add(small);
        ranges.add(medium);
        ranges.add(large);
    }
}
