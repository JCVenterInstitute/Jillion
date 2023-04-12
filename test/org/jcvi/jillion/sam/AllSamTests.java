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
package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.attribute.AllAttributeTests;
import org.jcvi.jillion.sam.cigar.AllCigarTests;
import org.jcvi.jillion.sam.header.AllSamHeaderTests;
import org.jcvi.jillion.sam.index.AllBamIndexTests;
import org.jcvi.jillion.sam.transform.AllSamTransformationServiceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	AllCigarTests.class,
    	AllSamHeaderTests.class,
    	AllAttributeTests.class,
    	
    	TestVirtualFileOffset.class,
    	TestSamRecordFlags.class,
    	TestBinComputation.class,
    	TestSortOrder.class,
    	
    	TestSamParserFactory.class,
    	
    	TestPresortedSamFileWriter.class,
    	TestPresortedBamFileWriter.class,
    	
    	TestResortedBamFileWriter.class,
    	TestResortedSamFileWriter.class,
    	
    	AllBamIndexTests.class,
    	
    	AllSamTransformationServiceTests.class,
    	
    	TestBamParserImplementations.class,
    	
    	TestBamDataStore.class,
    	TestQuerySortedSamDataStore.class,
    	SamRecordFlagsTest.class
    }
    )
public class AllSamTests {

}
