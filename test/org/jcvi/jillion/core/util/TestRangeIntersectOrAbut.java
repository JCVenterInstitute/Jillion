package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.Range;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestRangeIntersectOrAbut {

	@Test
	public void doesNot() {
		Range r = Range.of(0, 10);
		
		assertFalse(r.intersectsOrAbuts(Range.of(20, 30)));
		assertFalse(r.intersectsOrAbuts(Range.of(-10, -5)));
		
		assertFalse(r.intersectsOrAbuts(Range.of(-10, -2)));
		assertFalse(r.intersectsOrAbuts(Range.of(12, 30)));
	}
	
	@Test
	public void does() {
		Range r = Range.of(0, 10);
		
		assertTrue(r.intersectsOrAbuts(Range.of(5, 12)));
		assertTrue(r.intersectsOrAbuts(Range.of(10, 20)));
		assertTrue(r.intersectsOrAbuts(r));
		assertTrue(r.intersectsOrAbuts(Range.of(11, 20)));
		
		
		assertTrue(r.intersectsOrAbuts(Range.of(-10, -1)));
		assertTrue(r.intersectsOrAbuts(Range.of(-10, 4)));
	}
	
	@Test
	public void emptyDoesNot() {
		Range r = Range.of(0, -1);
		assertFalse(r.intersects(r));
		assertFalse(r.intersectsOrAbuts(r));
	}
}
