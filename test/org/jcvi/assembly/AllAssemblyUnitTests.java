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
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.assembly.agp.AllAgpUnitTests;
import org.jcvi.assembly.analysis.AllAnalysisUnitTests;
import org.jcvi.assembly.annot.ref.AllRefUnitTests;
import org.jcvi.assembly.cas.AllCasUnitTests;
import org.jcvi.assembly.contig.AllContigUnitTests;
import org.jcvi.assembly.coverage.AllCoverageUnitTests;
import org.jcvi.assembly.slice.AllSliceUnitTests;
import org.jcvi.assembly.trim.AllTrimUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {        
        TestDefaultPlacedContigClone.class,
        TestDefaultPlacedRead.class,
        TestDefaultLocation.class,
        TestSplitReferenceEncodedNucleotideGlyphs.class,
        TestSplitPlacedRead.class,
        TestSectionOfPlacedRead.class,
        
        AllTrimUnitTests.class,
        AllSliceUnitTests.class,
        AllAnalysisUnitTests.class,
        AllRefUnitTests.class,
        AllContigUnitTests.class,
        AllCoverageUnitTests.class,
        AllAgpUnitTests.class,
        AllCasUnitTests.class,
        
        TestAssemblyUtil_gappedfullRange.class,
        TestAssemblyUtil_reverseComplimentRange.class
    }
    )
public class AllAssemblyUnitTests {

}
