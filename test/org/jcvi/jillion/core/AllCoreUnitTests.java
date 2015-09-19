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
package org.jcvi.jillion.core;

import org.jcvi.jillion.core.datastore.AllDataStoreUnitTests;
import org.jcvi.jillion.core.io.AllCoreIOUnitTests;
import org.jcvi.jillion.core.testUtil.TestTestUtilSuite;
import org.jcvi.jillion.core.util.AllUtilUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestDirection.class,
        AllRangeTests.class,
        
        
        TestTestUtilSuite.class,
        AllUtilUnitTests.class,
        AllCoreIOUnitTests.class,
        AllSequenceUnitTests.class,
        
       
        AllDataStoreUnitTests.class
        
        
        
        
    }
    )
public class AllCoreUnitTests {

}
