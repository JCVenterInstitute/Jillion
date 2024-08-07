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
package org.jcvi.jillion.experimental;

import org.jcvi.jillion.experimental.align.AllAlignUnitTests;
import org.jcvi.jillion.experimental.assembly.agp.AllAgpUnitTests;
import org.jcvi.jillion.experimental.plate.AllPlateUnitTests;
import org.jcvi.jillion.experimental.primer.AllPrimerUnitTests;
import org.jcvi.jillion.experimental.trace.archive2.AllTraceArchive2UnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        
             
         AllPrimerUnitTests.class,
        AllPlateUnitTests.class,
        AllAlignUnitTests.class,
        
        
        AllAgpUnitTests.class,

        AllTraceArchive2UnitTests.class
    }
)
public class AllExperimentalTests {

}
