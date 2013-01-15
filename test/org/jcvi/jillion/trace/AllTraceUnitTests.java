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

package org.jcvi.jillion.trace;

import org.jcvi.common.core.seq.read.trace.pyro.All454UnitTests;
import org.jcvi.jillion.trace.archive.AllTraceArchiveUnitTests;
import org.jcvi.jillion.trace.archive2.AllTraceArchive2UnitTests;
import org.jcvi.jillion.trace.fastq.AllFastqUnitTests;
import org.jcvi.jillion.trace.frg.AllFrgUnitTests;
import org.jcvi.jillion.trace.sanger.AllSangerTraceUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestTraceQualityDataStoreAdapter.class,
        TestTraceNucleotideDataStoreAdapter.class,
        
        AllFastqUnitTests.class,
        All454UnitTests.class,
        AllSangerTraceUnitTests.class,
        AllFrgUnitTests.class  ,
        AllTraceArchiveUnitTests.class,
        AllTraceArchive2UnitTests.class
        
   
    }
    )
public class AllTraceUnitTests {

}
