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
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.assembly.util.AllAssemblyUtilUnitTests;
import org.jcvi.jillion.assembly.util.coverage.AllCoverageUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {        
                
    	 TestAssemblyUtil_gappedfullRange.class,
         TestAssemblyUtil_reverseComplimentRange.class,
         TestAssemblyUtil_convertToUngappedRange.class,
         TestAssemblyUtil_convertToUngappedFullRangeOffset.class,
         
        AllCoverageUnitTests.class,
        AllScaffoldUnitTests.class,
        
        
        
        AllAssemblyUtilUnitTests.class,
        
        AllContigUnitTests.class
    }
    )
public class AllAssemblyUnitTests {

}
