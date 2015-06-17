package org.jcvi.jillion.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.junit.Test;

public class TestArrayIterator {

	@Test
	public void iterate(){
		List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9);
		
		Integer[] array = list.toArray(new Integer[list.size()]);
		
		Iterator<Integer> actual = new ArrayIterator<>(array);
		Iterator<Integer> expected = list.iterator();
		
		assertTrue(actual.hasNext());
		assertTrue(expected.hasNext());
		
		while(expected.hasNext()){
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test(expected= NullPointerException.class)
	public void nullArrayShouldThrowNPE(){
		new ArrayIterator<String>(null);
	}
	
	
	@Test(expected = UnsupportedOperationException.class)
	public void removeShouldThrowUnSupportedOpException(){
		Iterator<String> iter = new ArrayIterator<String>(new String[0]);
		iter.remove();
	}
	
	@Test
	public void emptyArray(){
		Iterator<String> iter = new ArrayIterator<String>(new String[0]);
		
		assertFalse(iter.hasNext());
	}
	
	@Test(expected = NoSuchElementException.class)
	public void shouldThrowNoSuchElementExceptionIfNoNext(){
		Iterator<String> iter = new ArrayIterator<String>(new String[0]);
		//has next already tested false in other test
		iter.next();
	}
	
	
	@Test
	public void makeDefensiveCopy(){
		testMakesDefensiveCopy(array -> new ArrayIterator<>(array, true));
	}
	
	private void testMakesDefensiveCopy(Function<Integer[], Iterator<Integer>> constructorFunction){
		Integer[] array = new Integer[]{0, 1,2,3,4,5,6,7,8,9};
		
		Iterator<Integer> iter = constructorFunction.apply(array);
		
		array[5] = -99;
		
		for(int i=0; i<array.length; i++){
			assertEquals(Integer.valueOf(i), iter.next());
		}
	}
	
	@Test
	public void noDefensiveCopy(){
		Integer[] array = new Integer[]{0, 1,2,3,4,5,6,7,8,9};
		
		Iterator<Integer> iter = new ArrayIterator<Integer>(array, false);
		
		array[5] = -99;
		
		for(int i=0; i<array.length; i++){
			assertEquals(array[i], iter.next());
		}
	}
	
	@Test
	public void defaultConstructorMakesDefensiveCopy(){
		testMakesDefensiveCopy(array -> new ArrayIterator<>(array));
	}
}
