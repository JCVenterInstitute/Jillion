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

package org.jcvi.common.core.util.iter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestStreamingIterator {

	private StreamingIterator<String> sut;
	
	List<String> stooges = Arrays.asList("larry","moe","curly");
	List<String> stooges2 = Arrays.asList("shemp","curly-joe","joe besser");
	@Before
	public void setup(){
		List<StreamingIterator<String>> list = new ArrayList<StreamingIterator<String>>();
		list.add(IteratorUtil.createStreamingIterator(stooges.iterator()));
		list.add(IteratorUtil.createStreamingIterator(stooges2.iterator()));
		sut = IteratorUtil.createChainedStreamingIterator(list);

	}
	
	@Test
	public void whenFirstIteratorFinishedShouldStartIteratingSecond(){
		List<String> expected = new ArrayList<String>();
		expected.addAll(stooges);
		expected.addAll(stooges2);
		assertTrue(sut.hasNext());
		for(int i=0; i< expected.size(); i++){
			assertEquals(expected.get(i), sut.next());
		}
		assertFalse(sut.hasNext());
	}
	@Test
	public void closingIteratorShouldMakeIteratorAppearFinished() throws IOException{
		sut.next(); //larry
		sut.next(); //moe
		sut.close(); //close before we get to curly AND 2nd iterator
		assertFalse(sut.hasNext());
		try{
			sut.next();
			fail("should throw NoSuchElementException when no more elements");
		}catch(NoSuchElementException expected){
			
		}
	}
	@Test
	public void closingMultipleTimesShouldHaveNoEffect() throws IOException{
		sut.close();
		sut.close();
	}
}
