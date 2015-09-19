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
package org.jcvi.jillion.assembly.consed;

import org.jcvi.jillion.assembly.consed.ace.AllAceUnitTests;
import org.jcvi.jillion.assembly.consed.nav.AllConsedNavigationUnitTests;
import org.jcvi.jillion.assembly.consed.phd.AllPhdUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author dkatzel
 *
 *
 */
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestConsedUtil.class,
        TestConsedUtil_Split0xNoGaps.class,
        TestConsedUtil_Split0xWithGaps.class,
        TestConsedUtilGetNextAceVersion.class,
        
        TestConsedUtilGetLatestAce.class,
        
        TestConsedUtilGetAcePrefix.class,
        TestConsedUtilGetConsedDir.class,

        
        AllPhdUnitTests.class,
        
        AllAceUnitTests.class,
        
        AllConsedNavigationUnitTests.class
    }
    )
public class AllConsedUnitTests {

}
