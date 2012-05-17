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
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;


import org.jcvi.common.core.assembly.ace.consed.AllConsedUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestAceFileUtil.class,
       TestDefaultPhdInfo.class,
       TestAssembledFrom.class,
       TestAceParserPhdInfo.class,
       TestDefaultAceContig.class,
       TestAceContigBuilderInvalidRead.class,
       TestDefaultAceBestSegment.class,
       TestDefaultAceBestSegmentMap.class,
       TestOntheFlyAceBestSegmentMap.class,
       TestAcePlacedReadAdapter.class,
       TestDefaultAceTagsFromAceFile.class,
       TestDefaultAceFileDataStore.class,
       TestIndexAceFileOffsets.class,
       TestIndexedAceFileDataStore.class,
       TestLargeAceFileDataStore.class,
       
       TestAceFileWriter.class,
       TestHiLowAceContigPhdDatastore.class,
       TestDefaultAcePlacedReadReAbacus.class,
       TestDefaultAceContigBuilderReAbacus.class,
       TestAceVisitorSkipContigs.class,
       TestAceFileParserWithInvalidGapChar.class,
       AllConsedUnitTests.class
    }
)
public class AllAceUnitTests {

}
