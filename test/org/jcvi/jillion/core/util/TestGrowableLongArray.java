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
import org.jcvi.jillion.internal.core.util.GrowableLongArray;
import org.junit.Test;

public class TestGrowableLongArray {
	@Test
	public void append(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		long[] expected = new long[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void replace(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.replace(1, (long)15);
		long[] expected = new long[]{10,15,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendBeyondCapacityShouldGrowArray(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.append((long)50);
		sut.append((long)60);
		
		long[] expected = new long[]{10,20,30,40,50,60};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void remove(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.remove(2);
		
		long[] expected = new long[]{10,20,40};
		assertToArrayCorrect(sut, expected);
	}
	private void assertToArrayCorrect(GrowableLongArray sut, long[] expected) {
		assertEquals(expected.length, sut.getCurrentLength());
		assertArrayEquals(expected, sut.toArray());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
	}
	@Test
	public void removeRange(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.remove(Range.of(1,2));
		
		long[] expected = new long[]{10,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void removeEmptyRangeShouldDoNothing(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.remove(new Range.Builder().shift(2).build());
		
		long[] expected = new long[]{10,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insert(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.insert(1, (long)15);
		
		long[] expected = new long[]{10,15,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void insertAtOffsetLengthShouldActLikeAppend(){
		GrowableLongArray sut = new GrowableLongArray(4);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.insert(4, (long)50);
		
		long[] expected = new long[]{10,20,30,40,50};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertArray(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.insert(1, new long[]{15,16,17,18,19});
		
		long[] expected = new long[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertOtherGrowableLongArray(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.insert(1, new GrowableLongArray(new long[]{15,16,17,18,19}));
		
		long[] expected = new long[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void prepend(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.prepend((long)10);
		
		long[] expected = new long[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void prependArray(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.prepend(new long[]{10,11,12,13});
		
		long[] expected = new long[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void prependOtherGrowableLongArray(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.prepend(new GrowableLongArray(new long[]{10,11,12,13}));
		
		long[] expected = new long[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendArray(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.append(new long[]{50,60,70,80,90});
		long[] expected = new long[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendOtherGrowableLongArray(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		sut.append(new GrowableLongArray(new long[]{50,60,70,80,90}));
		long[] expected = new long[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void constructUsingInitialArray(){
		long[] array = new long[]{10,20,30,40,50};
		GrowableLongArray sut = new GrowableLongArray(array);
		assertArrayEquals(array, sut.toArray());
	}
	@Test
	public void mixOfAllOperations(){
		long[] initialArray = new long[]{Byte.MAX_VALUE,20,Long.MAX_VALUE,40,50};
		GrowableLongArray sut = new GrowableLongArray(initialArray);
		sut.append((long)60);  //Byte.MAX_VALUE,20,Long.MAX_VALUE,40,50,60
		sut.remove(3);  //Byte.MAX_VALUE,20,Long.MAX_VALUE,50,60
		sut.prepend((long)5);  //5,Byte.MAX_VALUE,20,Long.MAX_VALUE,50,60
		sut.replace(2, (long)15); //5,Byte.MAX_VALUE,15,Long.MAX_VALUE,50,60
		sut.insert(3, new long[]{25,26,27,28,29}); //5,Byte.MAX_VALUE,15,25,26,27,28,29,Long.MAX_VALUE,50,60
		
		sut.reverse();  //60,50,Long.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(1);  //60,Long.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(Range.of(3,5)); //60,Long.MAX_VALUE,29,25,15,Byte.MAX_VALUE,5
		sut.append((long)99);
		
		long[] expected = new long[]{60,Long.MAX_VALUE,29,25,15,Byte.MAX_VALUE,5,99};
		assertArrayEquals(expected, sut.toArray());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNegativeSizeShouldThrowException(){
		new GrowableLongArray(-1);
	}
	@Test
	public void constructorWithSizeZeroShouldBeAllowed(){
		GrowableLongArray sut =new GrowableLongArray(0);
		assertEquals(0, sut.getCurrentLength());
		sut.append((long)10);
		assertEquals(1, sut.getCurrentLength());
		assertEquals(10, sut.get(0));
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullArrayShouldThrowException(){
		new GrowableLongArray((long[])null);
	}
	
	@Test(expected = NullPointerException.class)
	public void constructorWithNullCollectionShouldThrowException(){
		new GrowableLongArray((Collection<Long>)null);
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullElementInCollectionShouldThrowException(){
		List<Long> list = new ArrayList<Long>();
		list.add(Long.valueOf(1L));
		list.add(null);
		
		new GrowableLongArray(list);
	}
	@Test
	public void constructorWithCollection(){
		List<Long> list = Arrays.asList((long)10,(long)20,(long)30,(long)40,(long)50);
		GrowableLongArray sut =new GrowableLongArray(list);
		
		long[] expected = new long[]{10,20,30,40, 50};
		
		assertArrayEquals(expected, sut.toArray());
	}
	
	
	@Test
	public void copy(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		GrowableLongArray copy = sut.copy();
		
		long[] expected = new long[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
		assertArrayEquals(expected, copy.toArray());
	}
	
	@Test
	public void modificationsToCopyShouldNotAffectOriginal(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		GrowableLongArray copy = sut.copy();
		copy.remove(2);

		assertArrayEquals(new long[]{10,20,30,40}, sut.toArray());
		assertArrayEquals(new long[]{10,20,40}, copy.toArray());
	}
	@Test
	public void modificationsToOriginalShouldNotAffectCopy(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		
		GrowableLongArray copy = sut.copy();
		sut.remove(2);

		assertArrayEquals(new long[]{10,20,30,40}, copy.toArray());
		assertArrayEquals(new long[]{10,20,40}, sut.toArray());
	}
	
	@Test
	public void reverseEvenNumberOfValues(){
		GrowableLongArray sut = new GrowableLongArray(5);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.reverse();
		
		assertArrayEquals(new long[]{40,30,20,10}, sut.toArray());
	}
	@Test
	public void reverseOddNumberOfValues(){
		GrowableLongArray sut = new GrowableLongArray(10);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.append((long)50);
		sut.reverse();
		
		assertArrayEquals(new long[]{50,40,30,20,10}, sut.toArray());
	}
	
	@Test
	public void sortUnSortedValues(){
		GrowableLongArray sut = new GrowableLongArray(10);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.append((long)50);
		sut.reverse();
		sut.sort();
		assertArrayEquals(new long[]{10,20,30,40,50}, sut.toArray());
	}
	
	@Test
	public void binarySearch(){
		GrowableLongArray sut = new GrowableLongArray(10);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.append((long)50);
		for(int i=0; i<sut.getCurrentLength(); i++){
			assertEquals(i, sut.binarySearch(sut.get(i)));
		}
		assertEquals("after all",-6, sut.binarySearch((long)60));
		assertEquals("before all", -1, sut.binarySearch((long)6));
		assertEquals("in between", -4, sut.binarySearch((long)35));
	}
	
	@Test
	public void sortedRemove(){
		GrowableLongArray sut = new GrowableLongArray(10);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.append((long)50);
		
		
		assertTrue(sut.sortedRemove((long)30));
		assertFalse(sut.sortedRemove((long)45));
		assertArrayEquals(new long[]{10, 20,40,50}, sut.toArray());

	}
	
	@Test
	public void sortedInsert(){
		GrowableLongArray sut = new GrowableLongArray(10);
		sut.append((long)10);
		sut.append((long)20);
		sut.append((long)30);
		sut.append((long)40);
		sut.append((long)50);
		
		
		assertEquals(3,sut.sortedInsert((long)35));
		assertEquals(2,sut.sortedInsert((long)30));
		assertArrayEquals(new long[]{10, 20,30,30,35, 40,50}, sut.toArray());

	}
	
	@Test
	public void iterator(){
		GrowableLongArray sut = new GrowableLongArray(new long[]{10, 20,30,30,35, 40,50});
		
		Iterator<Long> expected = Arrays.asList((long)10,(long) 20,(long)30,(long)30,(long)35,(long) 40, (long)50).iterator();
		
		Iterator<Long> actual = sut.iterator();
		
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void sortedInsertSmallerArray(){
		GrowableLongArray sut = new GrowableLongArray(new long[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new long[]{15,17,31,37,60});
		assertArrayEquals(new long[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60}, sut.toArray());
	}
	@Test
	public void sortedInsertLargerArray(){
		GrowableLongArray sut = new GrowableLongArray(new long[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new long[]{15,17,31,37,60,61,62,63,64,65});
		assertArrayEquals(new long[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60,61,62,63,64,65}, sut.toArray());
	}
	@Test
	public void sortedInsertSameSizedArray(){
		GrowableLongArray sut = new GrowableLongArray(new long[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new long[]{15,17,31,37,60,61,62});
		assertArrayEquals(new long[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60,61,62}, sut.toArray());
	}
	
	@Test
	public void sortedInsertEmptyArraShouldMakeNoChanges(){
		GrowableLongArray sut = new GrowableLongArray(new long[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new long[0]);
		assertArrayEquals(new long[]{10, 20,30,30,35, 40,50}, sut.toArray());
	}
	
	@Test
	public void sortedInsertOnEmptyGrowableArrayShouldAppend(){
		GrowableLongArray sut = new GrowableLongArray(10);
		
		sut.sortedInsert(new long[]{15,17,31,37,60,61,62});
		assertArrayEquals(new long[]{15,17,31,37,60,61,62}, sut.toArray());
	}
}
