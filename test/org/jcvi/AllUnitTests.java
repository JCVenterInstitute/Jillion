/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Apr 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import org.jcvi.jillion.assembly.AllAssemblyUnitTests;
import org.jcvi.jillion.core.AllCoreUnitTests;
import org.jcvi.jillion.fasta.AllFastaUnitTests;
import org.jcvi.jillion.maq.AllMaqTests;
import org.jcvi.jillion.sam.AllSamTests;
import org.jcvi.jillion.trace.AllTraceUnitTests;
import org.jcvi.jillion_experimental.AllExperimentalTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        
        
    	 AllCoreUnitTests.class,         
        AllFastaUnitTests.class,
        AllTraceUnitTests.class,
        AllAssemblyUnitTests.class,
        AllSamTests.class,
        AllMaqTests.class,
        AllExperimentalTests.class
    }
)
public class AllUnitTests {
}
