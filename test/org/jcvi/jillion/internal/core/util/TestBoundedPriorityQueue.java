package org.jcvi.jillion.internal.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

public class TestBoundedPriorityQueue {

	@Test
	public void emptyQueue(){
		BoundedPriorityQueue<Integer> sut = BoundedPriorityQueue.create(10);
		assertEquals(0, sut.size());
	}
	
	@Test
	public void addOneElement(){
		BoundedPriorityQueue<Integer> sut = BoundedPriorityQueue.create(10);
		sut.add(5);
		assertEquals(1, sut.size());
		assertEquals(5, sut.peek().intValue());
	}
	
	@Test
	public void addOneElementBeyondMaxShouldRemoveSmallest(){
		BoundedPriorityQueue<Integer> sut = BoundedPriorityQueue.create(2);
		sut.add(5);
		sut.add(3);
		sut.add(7);
		assertEquals(2, sut.size());
		assertTrue(sut.contains(Integer.valueOf(5)));
		assertTrue(sut.contains(Integer.valueOf(7)));
	
	}
	
	@Test
	public void tryToAddElementBeyondMaxShouldButElementIsLessThanWorstInQueueShouldDoNothing(){
		BoundedPriorityQueue<Integer> sut = BoundedPriorityQueue.create(2);
		sut.add(5);
		sut.add(7);
		sut.add(3);
		assertEquals(2, sut.size());
		assertTrue(sut.contains(Integer.valueOf(5)));
		assertTrue(sut.contains(Integer.valueOf(7)));
	
	}
	
	@Test
	public void addlotsOfRecordsInSortOrder(){
		BoundedPriorityQueue<Integer> sut = BoundedPriorityQueue.create(10);
		for(int i=0; i< 100; i++){
			sut.add(i);
		}
		
		assertEquals(10, sut.size());
		for(int i=90; i>=99; i--){
			assertEquals(i, sut.poll().intValue());
		}
	}
	
	@Test
	public void addlotsOfRecordsInReverseSortOrder(){
		BoundedPriorityQueue<Integer> sut = BoundedPriorityQueue.create(10);
		for(int i=99; i>=0; i--){
			sut.add(i);
		}
		
		assertEquals(10, sut.size());
		for(int i=90; i>=99; i--){
			assertEquals(i, sut.poll().intValue());
		}
	}
	
	@Test
	public void customComparator(){
		BoundedPriorityQueue<String> sut = BoundedPriorityQueue.create(5, Comparator.comparingInt(s-> s.length()));
		
		sut.add("ABCDEFGHIJKLMNOP");
		sut.add("ZUZU");
		sut.add("fi fie fo fum");
		sut.add("twinkle twinkle little star");
		sut.add("a");
		sut.add("b");
		sut.add("bla");
		
		assertEquals(5, sut.size());
		assertEquals("bla", sut.poll());
		assertEquals("ZUZU", sut.poll());
		assertEquals("fi fie fo fum", sut.poll());
		assertEquals("ABCDEFGHIJKLMNOP", sut.poll());
		assertEquals("twinkle twinkle little star", sut.poll());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeMaxSizeShouldThrowException(){
		BoundedPriorityQueue.<Integer>create(-5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void zeroMaxSizeShouldThrowException(){
		BoundedPriorityQueue.<Integer>create(0);
	}
	
}
