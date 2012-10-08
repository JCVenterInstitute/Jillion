package org.jcvi.common.core.symbol.qual;

import java.util.Iterator;

import org.jcvi.common.core.Range;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestQualitySequence {

	
	private final byte[] qualities = new byte[]{20,20,20,20,30,30,40,50,60};
	
	private QualitySequence sut;
	
	protected abstract QualitySequence create(byte[] qualities);
	
	@Before
	public void createSut(){
		sut = create(qualities);
	}
	
	@Test
	public void getLength(){	
		assertEquals(qualities.length, sut.getLength());
	}
	
	@Test
	public void get(){
		for(int i=0; i<qualities.length; i++){
			assertEquals(qualities[i], sut.get(i).getQualityScore());
		}
	}
	
	@Test
	public void iterator(){
		Iterator<PhredQuality> iter = sut.iterator();
		assertTrue(iter.hasNext());
		int i=0;
		while(iter.hasNext()){
			assertEquals(qualities[i], iter.next().getQualityScore());
			i++;
		}
		assertEquals(qualities.length,i);
	}
	@Test
	public void rangedIterator(){
		Iterator<PhredQuality> iter = sut.iterator(Range.of(3,7));
		assertTrue(iter.hasNext());
		for(int i=3; i<8; i++){
			assertEquals(qualities[i], iter.next().getQualityScore());
		}
		assertFalse(iter.hasNext());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void removingFromRangeIterThrowsException(){
		Iterator<PhredQuality> iter = sut.iterator(Range.of(3,7));
		assertTrue(iter.hasNext());
		iter.next();
		iter.remove();
	}
	@Test(expected=UnsupportedOperationException.class)
	public void removingFromIterThrowsException(){
		Iterator<PhredQuality> iter = sut.iterator();
		assertTrue(iter.hasNext());
		iter.next();
		iter.remove();
	}
	@Test
	public void testToString(){
		StringBuilder expected = new StringBuilder(4*qualities.length);
		for(int i=0; i< qualities.length-1; i++){
			expected.append(qualities[i])
					.append(", ");
		}
		expected.append(qualities[qualities.length-1]);
	}
	
}
