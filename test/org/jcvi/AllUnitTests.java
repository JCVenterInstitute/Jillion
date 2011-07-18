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
 * Created on Apr 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import junit.framework.Test;

import org.jcvi.align.AllAlignTests;
import org.jcvi.assembly.AllAssemblyUnitTests;
import org.jcvi.assembly.ace.AllAceUnitTests;
import org.jcvi.common.core.seq.AllGlyphUnitTests;
import org.jcvi.common.core.seq.fastx.AllFastXTests;
import org.jcvi.datastore.AllDataStoreUnitTests;
import org.jcvi.glyph.qualClass.AllQualityClassUnitTests;
import org.jcvi.io.AllIOUnitTests;
import org.jcvi.ncbi.AllNcbiUnitTests;
import org.jcvi.sequence.AllSequencingTests;
import org.jcvi.trace.AllTraceUnitTests;
import org.jcvi.util.AllUtilUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        
        TestCommonUtil.class,
        MathUtilSuite.class,
        TestTestUtilSuite.class,
        TestDistance.class,
        TestRange.class,
        TestRangeArrivalComparator.class,
        TestRangeDepartureComparator.class,
        TestEmptyRange.class,
        TestRangeIterator.class,
        TestRangeComparatorShortestToLongest.class,
        TestRangeComparatorLongestToShortest.class,
        AllGlyphUnitTests.class,
        AllFastXTests.class,
        AllAceUnitTests.class,
        AllQualityClassUnitTests.class,
        AllSequencingTests.class,
        AllIOUnitTests.class,
        AllTraceUnitTests.class,
        AllAssemblyUnitTests.class,
        AllUtilUnitTests.class,
        AllDataStoreUnitTests.class,
        AllAlignTests.class,
        AllNcbiUnitTests.class
    }
)
public class AllUnitTests {
  //required for ant?
    public static Test suite() {
        return new junit.framework.JUnit4TestAdapter(AllUnitTests.class);
     }
    public static void main(String[] args)  {
        junit.textui.TestRunner.run(AllUnitTests.suite());
    }
}
