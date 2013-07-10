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
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.GrowableShortArray;
import org.junit.Test;

public class TestGrowableShortArray {
	@Test
	public void append(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		short[] expected = new short[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void replace(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.replace(1, (short)15);
		short[] expected = new short[]{10,15,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendBeyondCapacityShouldGrowArray(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.append((short)50);
		sut.append((short)60);
		
		short[] expected = new short[]{10,20,30,40,50,60};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void remove(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.remove(2);
		
		short[] expected = new short[]{10,20,40};
		assertToArrayCorrect(sut, expected);
	}
	private void assertToArrayCorrect(GrowableShortArray sut, short[] expected) {
		assertEquals(expected.length, sut.getCurrentLength());
		assertArrayEquals(expected, sut.toArray());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
	}
	@Test
	public void removeRange(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.remove(Range.of(1,2));
		
		short[] expected = new short[]{10,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void removeEmptyRangeShouldDoNothing(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.remove(new Range.Builder().shift(2).build());
		
		short[] expected = new short[]{10,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insert(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.insert(1, (short)15);
		
		short[] expected = new short[]{10,15,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void insertAtOffsetLengthShouldActLikeAppend(){
		GrowableShortArray sut = new GrowableShortArray(4);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.insert(4, (short)50);
		
		short[] expected = new short[]{10,20,30,40,50};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertArray(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.insert(1, new short[]{15,16,17,18,19});
		
		short[] expected = new short[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertOtherGrowableShortArray(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.insert(1, new GrowableShortArray(new short[]{15,16,17,18,19}));
		
		short[] expected = new short[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void prepend(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.prepend((short)10);
		
		short[] expected = new short[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void prependArray(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.prepend(new short[]{10,11,12,13});
		
		short[] expected = new short[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void prependOtherGrowableShortArray(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.prepend(new GrowableShortArray(new short[]{10,11,12,13}));
		
		short[] expected = new short[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendArray(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.append(new short[]{50,60,70,80,90});
		short[] expected = new short[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendOtherGrowableShortArray(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		sut.append(new GrowableShortArray(new short[]{50,60,70,80,90}));
		short[] expected = new short[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void constructUsingInitialArray(){
		short[] array = new short[]{10,20,30,40,50};
		GrowableShortArray sut = new GrowableShortArray(array);
		assertArrayEquals(array, sut.toArray());
	}
	@Test
	public void mixOfAllOperations(){
		short[] initialArray = new short[]{Byte.MAX_VALUE,20,Short.MAX_VALUE,40,50};
		GrowableShortArray sut = new GrowableShortArray(initialArray);
		sut.append((short)60);  //Byte.MAX_VALUE,20,Short.MAX_VALUE,40,50,60
		sut.remove(3);  //Byte.MAX_VALUE,20,Short.MAX_VALUE,50,60
		sut.prepend((short)5);  //5,Byte.MAX_VALUE,20,Short.MAX_VALUE,50,60
		sut.replace(2, (short)15); //5,Byte.MAX_VALUE,15,Short.MAX_VALUE,50,60
		sut.insert(3, new short[]{25,26,27,28,29}); //5,Byte.MAX_VALUE,15,25,26,27,28,29,Short.MAX_VALUE,50,60
		
		sut.reverse();  //60,50,Short.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(1);  //60,Short.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(Range.of(3,5)); //60,Short.MAX_VALUE,29,25,15,Byte.MAX_VALUE,5
		sut.append((short)99);
		
		short[] expected = new short[]{60,Short.MAX_VALUE,29,25,15,Byte.MAX_VALUE,5,99};
		assertArrayEquals(expected, sut.toArray());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNegativeSizeShouldThrowException(){
		new GrowableShortArray(-1);
	}
	@Test
	public void constructorWithSizeZeroShouldBeAllowed(){
		GrowableShortArray sut =new GrowableShortArray(0);
		assertEquals(0, sut.getCurrentLength());
		sut.append((short)10);
		assertEquals(1, sut.getCurrentLength());
		assertEquals(10, sut.get(0));
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullArrayShouldThrowException(){
		new GrowableShortArray((short[])null);
	}
	
	@Test(expected = NullPointerException.class)
	public void constructorWithNullCollectionShouldThrowException(){
		new GrowableShortArray((Collection<Short>)null);
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullElementInCollectionShouldThrowException(){
		List<Short> list = new ArrayList<Short>();
		list.add(Short.valueOf((short)1));
		list.add(null);
		
		new GrowableShortArray(list);
	}
	@Test
	public void constructorWithCollection(){
		List<Short> list = Arrays.asList((short)10,(short)20,(short)30,(short)40,(short)50);
		GrowableShortArray sut =new GrowableShortArray(list);
		
		short[] expected = new short[]{10,20,30,40, 50};
		
		assertArrayEquals(expected, sut.toArray());
	}
	
	
	
	@Test
	public void copy(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		GrowableShortArray copy = sut.copy();
		
		short[] expected = new short[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
		assertArrayEquals(expected, copy.toArray());
	}
	
	@Test
	public void modificationsToCopyShouldNotAffectOriginal(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		GrowableShortArray copy = sut.copy();
		copy.remove(2);

		assertArrayEquals(new short[]{10,20,30,40}, sut.toArray());
		assertArrayEquals(new short[]{10,20,40}, copy.toArray());
	}
	@Test
	public void modificationsToOriginalShouldNotAffectCopy(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		
		GrowableShortArray copy = sut.copy();
		sut.remove(2);

		assertArrayEquals(new short[]{10,20,30,40}, copy.toArray());
		assertArrayEquals(new short[]{10,20,40}, sut.toArray());
	}
	
	@Test
	public void reverseEvenNumberOfValues(){
		GrowableShortArray sut = new GrowableShortArray(5);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.reverse();
		
		assertArrayEquals(new short[]{40,30,20,10}, sut.toArray());
	}
	@Test
	public void reverseOddNumberOfValues(){
		GrowableShortArray sut = new GrowableShortArray(10);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.append((short)50);
		sut.reverse();
		
		assertArrayEquals(new short[]{50,40,30,20,10}, sut.toArray());
	}
	
	@Test
	public void sortUnSortedValues(){
		GrowableShortArray sut = new GrowableShortArray(10);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.append((short)50);
		sut.reverse();
		sut.sort();
		assertArrayEquals(new short[]{10,20,30,40,50}, sut.toArray());
	}
	
	@Test
	public void binarySearch(){
		GrowableShortArray sut = new GrowableShortArray(10);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.append((short)50);
		for(int i=0; i<sut.getCurrentLength(); i++){
			assertEquals(i, sut.binarySearch(sut.get(i)));
		}
		assertEquals("after all",-6, sut.binarySearch((short)60));
		assertEquals("before all", -1, sut.binarySearch((short)6));
		assertEquals("in between", -4, sut.binarySearch((short)35));
	}
	
	
	@Test
	public void sortedRemove(){
		GrowableShortArray sut = new GrowableShortArray(10);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.append((short)50);
		
		
		assertTrue(sut.sortedRemove((short)30));
		assertFalse(sut.sortedRemove((short)45));
		assertArrayEquals(new short[]{10, 20,40,50}, sut.toArray());

	}
	
	@Test
	public void sortedInsert(){
		GrowableShortArray sut = new GrowableShortArray(10);
		sut.append((short)10);
		sut.append((short)20);
		sut.append((short)30);
		sut.append((short)40);
		sut.append((short)50);
		
		
		assertEquals(3,sut.sortedInsert((short)35));
		assertEquals(2,sut.sortedInsert((short)30));
		assertArrayEquals(new short[]{10, 20,30,30,35, 40,50}, sut.toArray());

	}
	
	
	@Test
	public void iterator(){
		GrowableShortArray sut = new GrowableShortArray(new short[]{10, 20,30,30,35, 40,50});
		
		Iterator<Short> expected = Arrays.asList((short)10,(short) 20,(short)30,(short)30,(short)35,(short) 40, (short)50).iterator();
		
		Iterator<Short> actual = sut.iterator();
		
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
}
