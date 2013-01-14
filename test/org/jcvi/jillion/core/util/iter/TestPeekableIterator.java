package org.jcvi.jillion.core.util.iter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableIterator;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPeekableIterator {

	private final List<String> dwarfs = Arrays.asList("Happy","Sleepy","Dopey","Doc","Bashful","Sneezy","Grumpy");
	
	@Test
	public void noPeeking(){
		
		Iterator<String> expected = dwarfs.iterator();
		PeekableIterator<String> actual = IteratorUtil.createPeekableIterator(dwarfs.iterator());
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void withPeeking(){
		Iterator<String> expected = dwarfs.iterator();
		PeekableIterator<String> actual = IteratorUtil.createPeekableIterator(dwarfs.iterator());
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
		PeekableIterator<String> actual = IteratorUtil.createPeekableIterator(dwarfs.iterator());
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
}
