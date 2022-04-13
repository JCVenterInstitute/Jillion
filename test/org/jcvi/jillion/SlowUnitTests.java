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
 * Created on Apr 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion;

import org.jcvi.jillion.align.AllAlignmentTests;
import org.jcvi.jillion.assembly.AllAssemblyUnitTests;
import org.jcvi.jillion.core.AllCoreUnitTests;
import org.jcvi.jillion.core.testUtil.SlowTests;
import org.jcvi.jillion.experimental.AllExperimentalTests;
import org.jcvi.jillion.fasta.AllFastaUnitTests;
import org.jcvi.jillion.maq.AllMaqTests;
import org.jcvi.jillion.profile.AllProfileUnitTests;
import org.jcvi.jillion.sam.AllSamTests;
import org.jcvi.jillion.testutils.AllTestUtilsTests;
import org.jcvi.jillion.trace.AllTraceUnitTests;
import org.jcvi.jillion.trim.AllTrimmerUnitTests;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
/**
 * Run only the "flow" automated tests.  These might take several
 * minutes to run.
 * @author dkatzel
 *
 */
@RunWith(Categories.class)
@IncludeCategory(SlowTests.class)
@SuiteClasses(
    {
        
        
    	 AllCoreUnitTests.class,         
        AllFastaUnitTests.class,
        AllTraceUnitTests.class,
        AllAssemblyUnitTests.class,
        AllSamTests.class,
        AllMaqTests.class,
        AllProfileUnitTests.class,
        AllTestUtilsTests.class,
        AllTrimmerUnitTests.class,
        AllAlignmentTests.class,
        AllExperimentalTests.class
   
    }
)
public class SlowUnitTests {
}
