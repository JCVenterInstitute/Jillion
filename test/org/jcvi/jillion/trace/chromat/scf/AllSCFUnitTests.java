/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
