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

import org.jcvi.jillion.assembly.ca.AllCeleraAssemblerTests;
import org.jcvi.jillion.assembly.clc.cas.AllCasUnitTests;
import org.jcvi.jillion.assembly.consed.AllConsedUnitTests;
import org.jcvi.jillion.assembly.tigr.ctg.AllTigrContigUnitTests;
import org.jcvi.jillion.assembly.tigr.tasm.AllTasmUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
    TestDefaultPlacedRead.class,
   
    TestContigDataStoreTransformationService.class,
    
    AllTigrContigUnitTests.class,
    AllConsedUnitTests.class,
    
    AllCasUnitTests.class,
   
    AllCeleraAssemblerTests.class,
    AllTasmUnitTests.class
    }
    )
public class AllContigUnitTests {

}
