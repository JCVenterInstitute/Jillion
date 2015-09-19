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
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestNullSectionDecoder.class,
        TestCommentSectionEncoder.class,
        TestCommentSectionDecoder.class,
        TestPrivateDataDecoder.class,
        TestPrivateDataEncoder.class,
        TestVersion2SamplesSectionEncoder.class,
        TestVersion2SamplesSectionDecoder.class,
        TestDeltaDeltaEncoding.class,
        TestVersion3SamplesSectionEncoder.class,
        TestVersion3SamplesSectionDecoder.class,
        TestVersion3BasesSectionEncoder.class,
        TestVersion3BasesSectionDecoder.class,
        TestVersion2BasesSectionEncoder.class,
        TestVersion2BasesSectionDecoder.class,

        TestSectionCodecFactoryGetDecoderFor.class,
        TestSectionCodecFactoryGetEncoderFor.class
    }
    )
public class AllSectionUnitTests {

}
