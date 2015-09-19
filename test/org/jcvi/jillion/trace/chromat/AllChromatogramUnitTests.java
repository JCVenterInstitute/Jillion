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
package org.jcvi.jillion.trace.chromat;

import org.jcvi.jillion.trace.chromat.abi.AllAbiUnitTests;
import org.jcvi.jillion.trace.chromat.scf.AllSCFUnitTests;
import org.jcvi.jillion.trace.chromat.ztr.AllZTRUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestChannel.class,
        TestChannelGroup.class,
        TestBasicChromatogram.class,
        TestEncodedByteData.class,
        TestEncodedShortData.class,        
        AllSCFUnitTests.class,        
        AllZTRUnitTests.class,        
        
        TestZtr2ScfVersion3.class,
        TestZtr2ScfVersion2.class,
        
        AllAbiUnitTests.class,
        TestChromatogramFactory.class
             
    }
    )
public class AllChromatogramUnitTests {

}
