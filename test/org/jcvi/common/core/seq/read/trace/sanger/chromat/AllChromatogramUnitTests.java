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
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.AllAbiUnitTests;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.AllSCFUnitTests;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.AllZTRUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestChannel.class,
        TestChannelGroup.class,
        TestSignalStrength.class,
        TestBasicChromatogram.class,
        TestEncodedByteData.class,
        TestEncodedShortData.class,        
        AllSCFUnitTests.class,        
        AllZTRUnitTests.class,        
        TestConvertZtr2Scf.class,
        TestMixAndMatchChromatogramParsers.class,
        AllAbiUnitTests.class,
        TestChromatogramFileParser.class,
        TestChromatogram2Fasta.class        
    }
    )
public class AllChromatogramUnitTests {

}
