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
package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.util.iter.TestAbstractBlockingClosableIteratorExceptions;
import org.jcvi.jillion.core.util.iter.TestArrayIterator;
import org.jcvi.jillion.core.util.iter.TestByteArrayIterator;
import org.jcvi.jillion.core.util.iter.TestChainedIterator;
import org.jcvi.jillion.core.util.iter.TestChainedIteratorSupplier;
import org.jcvi.jillion.core.util.iter.TestCharArrayIterator;
import org.jcvi.jillion.core.util.iter.TestEmptyIterator;
import org.jcvi.jillion.core.util.iter.TestIntArrayIterator;
import org.jcvi.jillion.core.util.iter.TestLongArrayIterator;
import org.jcvi.jillion.core.util.iter.TestPeekableIterator;
import org.jcvi.jillion.core.util.iter.TestPeekableStreamingIterator;
import org.jcvi.jillion.core.util.iter.TestShortArrayIterator;
import org.jcvi.jillion.core.util.iter.TestSingleElementIterator;
import org.jcvi.jillion.core.util.iter.TestStreamingIterator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestSingleElementIterator.class,
    	
    	TestArrayUtil_IntArray.class,
    	
    	TestObjectsUtil.class,
        TestRunLength.class,
        TestLRUCache.class,
        TestWeakReferenceLRUCache.class,
        TestEmptyIterator.class,
        TestFileIterator.class,
        TestDepthFirstFileIterator.class,
        TestBreadthFirstFileIterator.class,
        TestJoinedStringBuilder.class,
        TestMultipleWrapper.class,
        TestChainedIterator.class,
        TestStreamingAdapter.class,
        TestStreamingIterator.class,
        TestMapValueComparator.class,

        TestPeekableIterator.class,
        TestPeekableStreamingIterator.class,
        
        TestAbstractBlockingClosableIteratorExceptions.class,
        TestDateUtilElapsedTime.class,
        TestGrowableByteArray.class,
        TestGrowableShortArray.class,
        TestGrowableCharArray.class,
        
        TestGrowableIntArray.class,
        TestGrowableLongArray.class,
        
        TestByteArrayIterator.class,
        TestShortArrayIterator.class,
        TestCharArrayIterator.class,
        
        TestIntArrayIterator.class,
        TestLongArrayIterator.class,
        
        TestUnsignedByteArray.class,
        TestUnsignedShortArray.class,
        TestUnsignedIntArray.class,
        
        TestArrayIterator.class,
        
        TestChainedIteratorSupplier.class,
        
        TestGenomeStatistics.class,
        
        TestN50Computations.class,
        TestN75Computations.class,
        TestN90Computations.class,
        
        TestNG50Computations.class,
        TestNG75Computations.class,
        TestNG90Computations.class,
        
        TestThrowingStream.class
        
    }
    )
public class AllUtilUnitTests {

}
