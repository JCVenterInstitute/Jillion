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

import org.jcvi.common.core.align.AllAlignUnitTests;
import org.jcvi.common.core.io.AllCoreIOUnitTests;
import org.jcvi.common.core.seq.AllSeqUnitTests;
import org.jcvi.jillion.assembly.AllAssemblyUnitTests;
import org.jcvi.jillion.core.datastore.AllDataStoreUnitTests;
import org.jcvi.jillion.core.testUtil.TestTestUtilSuite;
import org.jcvi.jillion.core.util.AllUtilUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestDirection.class,
        AllRangeTests.class,
        
        
        TestTestUtilSuite.class,
        AllUtilUnitTests.class,
        AllCoreIOUnitTests.class,
        AllSequenceUnitTests.class,
        AllSeqUnitTests.class,
        AllDataStoreUnitTests.class,
        AllAssemblyUnitTests.class,
        AllAlignUnitTests.class
        
    }
    )
public class AllCoreUnitTests {

}
