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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        AllSFFUtilUnitTests.class,
        TestDefaultReadData.class,
        TestDefaultSFFReadHeader.class,
        TestDefaultSFFCommonHeader.class,
        TestSFFReadHeaderCodec_decode.class,
        TestSFFReadHeaderCodec_encoder.class,
        TestDefaultSFFReadDataCodec_decode.class,
        TestDefaultSFFReadDataCodec_encode.class,
        TestDefaultSFFCommonHeaderCodec_decode.class,
        TestDefaultSFFCommonHeaderCodec_encode.class,
        TestSFFFlowgram.class,
        TestSFFCodecParseActualSFFFile.class,
        TestLargeSffFileDataStore.class,
        TestFlowIndexOverflow.class,
        TestNewblerSuffixNameConverter.class,
        TestDefaultSffFlowgramDataStore.class,
        TestSFFUtil_getTrimRange.class,
        TestSffVisitorWriter.class,
        TestH2NucleotideSffDataStore.class,
        TestH2QualitySffDataStore.class,
        TestFilteredH2QualityDataStore.class,
        TestFilteredH2NucleotideDataStore.class,
        TestSffInfoDataStore.class
        
    }
    )
public class AllSFFUnitTests {

}
