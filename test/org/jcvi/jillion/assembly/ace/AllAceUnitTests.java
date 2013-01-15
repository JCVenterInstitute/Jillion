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
package org.jcvi.jillion.assembly.ace;


import org.jcvi.jillion.assembly.ace.consed.AllConsedUnitTests;
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
       
       TestParseInvalidAceNotAnAceFile.class,
       
       TestDefaultAceContig.class,
       TestAceContigBuilderInvalidRead.class,
       TestAceContigBuilderRecallConsensus.class,
       TestDefaultAceAssembledReadBuilder.class,
       
       TestBaseSegmentUtil.class,
       TestDefaultAceBestSegment.class,
       TestAcePlacedReadAdapter.class,
       
       TestDefaultAceFileDataStore.class,
       TestIndexAceFileOffsets.class,
       TestMemoryMappedIndexedAceFileDataStore.class,
       TestLargeIndexedAceFileDataStore.class,
       TestLargeAceFileDataStore.class,
       
       TestDefaultAceFileDataStoreStreamingIterators.class,
       TestIndexedAceDataStoreStreamingIterators.class,
       TestLargeAceFileDataStoreStreamingIterators.class,
       
       TestAceFileParserStopParsingMiddleContig.class,
       TestAceFileUtil_writingAceContigs.class,
       TestDefaultAceFileWriter.class,
       TestHighLowAceContigPhdDatastore.class,
       TestDefaultAcePlacedReadReAbacus.class,
       TestDefaultAceContigBuilderReAbacus.class,
       
       TestFilteredDefaultAceFileDataStore.class,
       TestFilteredIndexedAceFileDataStore.class,
       TestFilteredLargeIndexedAceFileDataStore.class,
       
       TestAceFileParserWithInvalidGapChar.class,
       
       AceTagsFromDefaultAceFileDataStore.class,
       AceTagsFromIndexedAceFileDataStore.class,
       AceTagsFromLargeAceFileDataStore.class,
       
       AllConsedUnitTests.class
    }
)
public class AllAceUnitTests {

}
