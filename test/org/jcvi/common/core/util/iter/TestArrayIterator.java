package org.jcvi.common.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
public class TestArrayIterator {

	String[] array = new String[]{"moe","larry","curly"};
	
	@Test
	public void iterate(){
		ArrayIterator<String> sut = new ArrayIterator<String>(array);
		for(int i=0; i<array.length;i++){
			assertTrue(sut.hasNext());
			assertEquals(array[i],sut.next());
		}
		assertFalse(sut.hasNext());
		
	}
}
