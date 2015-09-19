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
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import org.jcvi.jillion.trace.chromat.scf.header.AllHeaderUnitTests;
import org.jcvi.jillion.trace.chromat.scf.pos.AllPositionStrategyUnitTests;
import org.jcvi.jillion.trace.chromat.scf.section.AllSectionUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestPrivateData.class,
        TestSCFChromatogram.class,
       AllHeaderUnitTests.class,
       AllPositionStrategyUnitTests.class,
       AllSectionUnitTests.class,

       TestActualSCFCodec.class,
       TestSCFChromatogramWithGaps.class,
       TestSCFChromatogramFile.class,
       TestVersion2Parser.class
    }
    )
public class AllSCFUnitTests {

}
