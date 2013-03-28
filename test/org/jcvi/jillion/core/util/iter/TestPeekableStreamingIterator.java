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
package org.jcvi.jillion.core.util.iter;


import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableStreamingIterator;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPeekableStreamingIterator {

private final List<String> dwarfs = Arrays.asList("Happy","Sleepy","Dopey","Doc","Bashful","Sneezy","Grumpy");
	
	@Test
	public void noPeeking(){
		
		Iterator<String> expected = dwarfs.iterator();
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void withPeeking(){
		Iterator<String> expected = dwarfs.iterator();
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		while(expected.hasNext()){
			assertTrue(actual.hasNext());			
			String next = expected.next();
			assertEquals(next,actual.peek());
			//can peek multiple times
			assertEquals(next, actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void multiplePeeks(){
		Iterator<String> expected = dwarfs.iterator();
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		while(expected.hasNext()){
			assertTrue(actual.hasNext());			
			String next = expected.next();
			for(int i=0; i<5;i++){
				assertEquals(next,actual.peek());
			}
			assertEquals(next, actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void closing() throws IOException{
		
		PeekableStreamingIterator<String> actual = IteratorUtil.createPeekableStreamingIterator(dwarfs.iterator());
		for(int i=0; i<5;i++){
			actual.next();
		}
		actual.close();
		
		assertFalse(actual.hasNext());
		try{
			actual.peek();
			fail("should throw NoSuchElementException if already closed");
		}catch(NoSuchElementException expected){
			
		}
		try{
			actual.next();
			fail("should throw NoSuchElementException if already closed");
		}catch(NoSuchElementException expected){
			
		}
	}
}
