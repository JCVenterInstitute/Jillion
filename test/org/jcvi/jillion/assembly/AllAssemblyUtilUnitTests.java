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
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.assembly.util.AllCoverageUnitTests;
import org.jcvi.jillion.assembly.util.AllSliceUnitTests;
import org.jcvi.jillion.assembly.util.slice.AllVariableSliceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {   
    	TestAssemblyUtil_getUngappedComplementedValidRangeQualities.class,
    	 TestAssemblyUtil_gappedfullRange.class,
         TestAssemblyUtil_reverseComplimentRange.class,
         TestAssemblyUtil_convertToUngappedRange.class,
         TestAssemblyUtil_convertToUngappedFullRangeOffset.class,
         
        AllCoverageUnitTests.class,
        AllSliceUnitTests.class,

        AllVariableSliceTests.class
    }
    )
public class AllAssemblyUtilUnitTests {

}
