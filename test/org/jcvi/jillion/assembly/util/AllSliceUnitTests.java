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
 * Created on Apr 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.assembly.util.consensus.AllConsensusUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
    	AllQualityValueStrategyUnitTests.class,
    	
        TestDefaultSliceElement.class,
        TestCompactedSliceElement.class,
        TestDefaultSlice.class,
        TestCompactedSlice.class,
        TestSliceBuilder.class,
        
        TestDefaultSliceMap.class,
        TestCompactedSliceMap.class,
        TestSliceMapBuilderUsingQualityDataStore.class,
        TestSliceMapBuilderUsingDefaultQualities.class,
        
        TestParallelSliceCollector.class,
        TestSliceCollector.class,
        
        TestSliceMapBuilderReadFilter.class,
        
        AllConsensusUnitTests.class
        
    }
    )
public class AllSliceUnitTests {

}
