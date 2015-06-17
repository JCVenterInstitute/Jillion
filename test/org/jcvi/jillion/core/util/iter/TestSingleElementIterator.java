package org.jcvi.jillion.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestSingleElementIterator {

	private String foo = "foo";
	private Iterator<String> iter;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setuo(){
		 iter = new SingleElementIterator<>(foo);
	}
	@Test
	public void iterate(){
		
		assertTrue(iter.hasNext());
		assertEquals(foo, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void shouldThrowNoSuchElementExceptionIfAlreadyIterated(){
		iter.next();
		
		assertFalse(iter.hasNext());
		expectedException.expect(NoSuchElementException.class);
		
		iter.next();
	}
}
