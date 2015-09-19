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
package org.jcvi.jillion.assembly.tigr.tasm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all the TIGR Assembler internal unit tests.
 * @author dkatzel
 *
 *
 */
@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestTasmUtil.class,
    	
       TestDefaultTigrAssemblerAssembledReadBuilder.class,
       TestTigrAssemblerWriter.class,
       TestTigrAssemblerPlacedReadAdapter.class,
       TestTigrAssemblerContigAdapterBuilderWithNoOptionalAttributes.class,
       TestAnnotationTasmParsing.class,
       
       TestDefaultTasmDataStore.class,
       TestIndexedTasmDataStore.class,
       TestLargeTasmDataStore.class,
       
       TestTasmWriterBuilderOutputStream.class,
       TestTasmWriterBuilderFile.class
    }
    )
public class AllTasmUnitTests {

}
