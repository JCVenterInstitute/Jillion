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
 * Created on Mar 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger;

import org.jcvi.jillion.core.pos.TestDefaultPositionCodec;
import org.jcvi.jillion.core.pos.TestPosition;
import org.jcvi.jillion.core.pos.TestPositionSequenceBuilder;
import org.jcvi.jillion.trace.sanger.chromat.AllChromatogramUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestPosition.class,
    	TestDefaultPositionCodec.class,
    	TestPositionSequenceBuilder.class,
    	TestDefaultPositionFastaFileDataStore.class,
    	TestDefaultPositionSequenceFastaRecordWriter.class,
    	
        
        AllChromatogramUnitTests.class
        
    }
    )
public class AllSangerTraceUnitTests {

}
