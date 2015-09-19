/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestAceFileUtil.class,
        TestConsedConsensusQualityComputer.class,
        
       TestDefaultPhdInfo.class,
       TestAssembledFrom.class,
       TestAceParserPhdInfo.class,
       
       TestParseInvalidAceNotAnAceFile.class,
       
       TestSkippingReadsDoesntSendBasecallsToConsensusInstead.class,
       
       TestDefaultAceContig.class,
       TestAceContigBuilderInvalidRead.class,
       TestAceContigBuilderRecallConsensus.class,
       TestAceContigBuilderVisitorReadVisitorUsesAlignCoords.class,
       TestDefaultAceAssembledReadBuilder.class,
       
       
       TestBaseSegmentUtil.class,
       TestDefaultAceBestSegment.class,
       TestAcePlacedReadAdapter.class,
       
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
       
       TestDefaultAceFileDataStore2.class,
       TestIndexedAceFileDataStore2.class,
       TestLargeAceFileDataStore2.class,
       
       TestAceTestUtil.class,
       TestAceContigVisitorAdapter.class,
       TestAceContigVisitorAdapterRemoveReadsFromContig.class,
       
       TestAceFileParserWithNegativeReadAlignValues.class,
       
       TestDenovoAceContigBuilder.class,
       TestAceContigBuilderInsertGapsIntoRead.class
       
    }
)
public class AllAceUnitTests {

}
