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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        AllSFFUtilUnitTests.class,
        TestSffFileIterator.class,
        TestDefaultReadData.class,
        TestDefaultSFFReadHeader.class,
        TestDefaultSFFCommonHeader.class,
        TestSffeadHeaderDecoder.class,
        TestDefaultSffReadDataDecoder.class,
        TestDefaultSffCommonHeaderDecoder.class,
        TestSffFlowgram.class,
        TestSFFCodecParseActualSFFFile.class,
        TestLargeSffFileDataStore.class,
        TestFlowIndexOverflow.class,
        TestDefaultSffFlowgramDataStore.class,
        TestSFFUtil_getTrimRange.class,
        TestSffWriterUtil.class,
        TestIndexed454SffFileDataStore.class,
        TestNoXMLManifestIndexedSffFileDataStore.class,
        TestIndexedSffFileDataStore.class,
        TestNoManifestIndexedSffFileDataStore.class,
        
        TestSffWriterNoManifest.class,
        TestSffWriterWithIndex.class,
        TestSffWriterVisitor.class
    }
    )
public class AllSFFUnitTests {

}
