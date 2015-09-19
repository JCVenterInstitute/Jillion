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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	
    	TestFastqRecordBuilder.class,
    	
    	TestFastqQualityCodecOffsets.class,
    	
        TestSangerFastQQualityCodec.class,
        TestIlluminaFastQQualityCodec.class,
        TestSangerFastQQualityCodecActual.class,
        TestParseSangerEncodedFastQFile.class,        
        TestFastqWriter.class,
        TestFastqWriterWithComment.class,
        
        TestFastqParser.class,
        TestFastqParserWithFunctionLambda.class,
        TestInvalidFastq.class,
        
        TestDefaultFastQFileDataStore.class,
        TestDefaultMultiLineFastqRecordsInDataStore.class,
        TestDefaultFastqFileDataStoreGuessCodec.class, 
        
        TestFastqFileWithEmptyRead.class,
        
        TestIndexedFastQFileDataStore.class,
        TestDefaultFastqFileDataStoreMultilineGuessCodec.class,
        TestIndexedFastqFileDataStoreGuessCodec.class,
        TestIndexedMultilineFastqDataStore.class,
        
        TestLargeFastQFileDataStore.class,
        TestLargeMultilineFastqDataStore.class,
        TestLargeFastqFileDataStoreGuessCodec.class,
        TestLargeMultilineFastqFileDataStoreGuessCodec.class,
        
        TestInMemorySortedFastqWriter.class,
        TestTmpDirStillOnlyUsedCacheNotEnoughToDumpToFileSortedFastqWriter.class,
        TestTmpDirSingleFileSortedFastqWriter.class,
        TestTmpDirOneFilePerReadSortedFastqWriter.class,
        
        TestFastqFileBuilderWithLambdaFilter.class,
        
        TestSplitFastqRoundRobin.class,
        TestSplitFastqRollover.class,
        TestSplitFastqDeconvolver.class,
        
        AllIlluminaUnitTests.class,
        AllSolexaUnitTests.class,
        AllFastqUtilUnitTests.class
    }
    )
public class AllFastqUnitTests {

}
