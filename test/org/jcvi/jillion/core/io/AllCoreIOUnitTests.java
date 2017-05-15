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
package org.jcvi.jillion.core.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestIOUtil_closeAndIgnore.class,
        TestIOUtil_blockingSkip.class,
        TestIOUtil_readByteArray.class,
        TestIOUtil_readShortArray.class,
        TestIOUtil_convertSignedToUnsigned.class,
        TestIOUtil_UnsignedByteArray.class,
        TestIOUtil_deleteDir.class,
        TestIOUtil_convertToUnsignedByteArray.class,
        TestIOUtil_whenMakingDirectories.class,
        TestIOUtil_unsignedByteToSignedByte.class,
        TestIOUtil_unsignedShortToSignedShort.class,
        TestIOUtil_unsignedIntToSignedInt.class,
        TestIOUtil_getUnsignedBitAndByteCount.class,
        TestIOUtil_bitSet.class,
        TestIOUtil_copy.class,
        TestIOUtil_toByteArray.class,
        TestIOUtil_toString.class,
        TestIOUtil_numberOfBitsIn.class,
        TestIOUtil_createTempDir.class,
        
        TestFileUtil.class,
        TestMagicNumberInputStream.class,
        TestOpenAwareInputStream.class,
        TestRandomAccessFileInputStream.class,
        
        TestInputStreamSupplierForFile.class,
        TestInputStreamSupplierWithEmptyFile.class,
        TestSubLengthInputStream.class,
        
        TestTextLineParser.class,
        TestBufferSize.class,
        
        TestPushBackBufferedReader.class
    }
    )
public class AllCoreIOUnitTests {

}
