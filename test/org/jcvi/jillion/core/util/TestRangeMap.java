package org.jcvi.jillion.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Before;
public class TestRangeMap {

	RangeMap<Integer> sut;
	@Before
	public void setup() {
		sut = new RangeMap<>();
	}
	@Test
	public void emptyMap() {
		
		
		assertTrue(sut.isEmpty());
		assertEquals(0, sut.size());
		
		assertTrue(sut.computeMergedRanges().isEmpty());
	}
	
	@Test
	public void oneElement() {
		Range range = Range.of(1,10);
		sut.put(range, 10);
		
		assertFalse(sut.isEmpty());
		assertEquals(1, sut.size());
		assertEquals(Integer.valueOf(10), sut.get(range));
		
		assertEquals(List.of(range), sut.computeMergedRanges());
		List<Integer> found = new ArrayList<Integer>();
		
		sut.getAllThatIntersect(range, (r, v, callback)-> found.add(v));
		
		assertEquals(List.of(10), found);
	}
	
	@Test
	public void twoElements() {
		Range range = Range.of(1,10);
		Range range2 = Range.of(20,30);
		sut.put(range, 10);
		sut.put(range2, 20);
		
		assertFalse(sut.isEmpty());
		assertEquals(2, sut.size());
		assertEquals(Integer.valueOf(10), sut.get(range));
		assertEquals(Integer.valueOf(20), sut.get(range2));
		
		assertEquals(List.of(range, range2), sut.computeMergedRanges());
		
		
		List<Integer> found = new ArrayList<Integer>();
		
		sut.getAllThatIntersect(Range.of(0, 30), (r, v, callback)-> found.add(v));
		
		assertEquals(List.of(10, 20), found);
		
		List<Integer> found2 = new ArrayList<Integer>();
		
		sut.getAllThatIntersect(Range.of(50,60), (r, v, callback)-> found2.add(v));
		
		assertEquals(Collections.emptyList(), found2);
	}
}
