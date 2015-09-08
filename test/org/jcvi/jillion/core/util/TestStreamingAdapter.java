/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestStreamingAdapter {

	List<String> stooges = Arrays.asList("larry","moe","curly");
	private StreamingIterator<String> sut;
	@Before
	public void setup(){
		sut = IteratorUtil.createStreamingIterator(stooges.iterator());
		
	}
	@Test
	public void adaptedIteratorShouldIterateCorrectly(){
		assertTrue(sut.hasNext());
		for(int i=0; i< stooges.size(); i++){
			assertEquals(stooges.get(i),sut.next());
		}
		assertFalse(sut.hasNext());
		try{
			sut.next();
			fail("should throw NoSuchElementException when no more elements");
		}catch(NoSuchElementException expected){
			
		}
	}
	
	@Test
	public void closingIteratorShouldMakeIteratorAppearFinished() throws IOException{
		sut.next(); //larry
		sut.next(); //moe
		sut.close(); //close before we get to curly
		assertFalse(sut.hasNext());
		try{
			sut.next();
			fail("should throw NoSuchElementException when no more elements");
		}catch(NoSuchElementException expected){
			assertEquals("iterator has been closed", expected.getMessage());
		}
	}
	@Test
	public void closingMultipleTimesShouldHaveNoEffect() throws IOException{
		sut.close();
		sut.close();
	}
}
