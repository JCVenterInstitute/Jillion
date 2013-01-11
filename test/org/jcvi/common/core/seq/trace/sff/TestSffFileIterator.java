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

package org.jcvi.common.core.seq.trace.sff;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jcvi.common.core.seq.trace.sff.SffFileIterator;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSffFileIterator {

	ResourceHelper RESOURCES  =new ResourceHelper(TestSffFileIterator.class);
	
	@Test
	public void iterateOverAllRecords() throws IOException{
		SffFileIterator iter = SffFileIterator.createNewIteratorFor(RESOURCES.getFile("files/5readExample.sff"));

		for(int i=0; i<5; i++){
			assertTrue(iter.hasNext());
			iter.next();
		}
		assertFalse(iter.hasNext());
		assertGettingNextThrowsException(iter);
	}

	private void assertGettingNextThrowsException(SffFileIterator iter) {
		try{
			iter.next();
			fail("should throw no such element exception");
		}catch(NoSuchElementException expected){
			
		}
	}
	
	@Test
	public void closeIteratorEarlyShouldStopIterating() throws IOException{
		SffFileIterator iter = SffFileIterator.createNewIteratorFor(RESOURCES.getFile("files/5readExample.sff"));
		assertTrue(iter.hasNext());
		iter.next();
		iter.close();
		assertFalse(iter.hasNext());
		assertGettingNextThrowsException(iter);
	}
	
	
	
}
