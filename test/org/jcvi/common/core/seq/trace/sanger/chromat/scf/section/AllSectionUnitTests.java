/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf.section;

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
