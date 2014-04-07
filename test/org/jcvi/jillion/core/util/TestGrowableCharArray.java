/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
import org.jcvi.jillion.internal.core.util.GrowableCharArray;
import org.junit.Test;

public class TestGrowableCharArray {
	@Test
	public void append(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		char[] expected = new char[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void replace(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.replace(1, (char)15);
		char[] expected = new char[]{10,15,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendBeyondCapacityShouldGrowArray(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.append((char)50);
		sut.append((char)60);
		
		char[] expected = new char[]{10,20,30,40,50,60};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void remove(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.remove(2);
		
		char[] expected = new char[]{10,20,40};
		assertToArrayCorrect(sut, expected);
	}
	private void assertToArrayCorrect(GrowableCharArray sut, char[] expected) {
		assertEquals(expected.length, sut.getCurrentLength());
		assertArrayEquals(expected, sut.toArray());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
	}
	@Test
	public void removeRange(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.remove(Range.of(1,2));
		
		char[] expected = new char[]{10,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void removeEmptyRangeShouldDoNothing(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.remove(new Range.Builder().shift(2).build());
		
		char[] expected = new char[]{10,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insert(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.insert(1, (char)15);
		
		char[] expected = new char[]{10,15,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void insertAtOffsetLengthShouldActLikeAppend(){
		GrowableCharArray sut = new GrowableCharArray(4);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.insert(4, (char)50);
		
		char[] expected = new char[]{10,20,30,40,50};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertArray(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.insert(1, new char[]{15,16,17,18,19});
		
		char[] expected = new char[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertOtherGrowableCharArray(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.insert(1, new GrowableCharArray(new char[]{15,16,17,18,19}));
		
		char[] expected = new char[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void prepend(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.prepend((char)10);
		
		char[] expected = new char[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void prependArray(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.prepend(new char[]{10,11,12,13});
		
		char[] expected = new char[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void prependOtherGrowableCharArray(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.prepend(new GrowableCharArray(new char[]{10,11,12,13}));
		
		char[] expected = new char[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendArray(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.append(new char[]{50,60,70,80,90});
		char[] expected = new char[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendOtherGrowableCharArray(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		sut.append(new GrowableCharArray(new char[]{50,60,70,80,90}));
		char[] expected = new char[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void constructUsingInitialArray(){
		char[] array = new char[]{10,20,30,40,50};
		GrowableCharArray sut = new GrowableCharArray(array);
		assertArrayEquals(array, sut.toArray());
	}
	@Test
	public void mixOfAllOperations(){
		char[] initialArray = new char[]{Character.MAX_VALUE,20,Character.MAX_VALUE,40,50};
		GrowableCharArray sut = new GrowableCharArray(initialArray);
		sut.append((char)60);  //Byte.MAX_VALUE,20,char.MAX_VALUE,40,50,60
		sut.remove(3);  //Byte.MAX_VALUE,20,char.MAX_VALUE,50,60
		sut.prepend((char)5);  //5,Byte.MAX_VALUE,20,char.MAX_VALUE,50,60
		sut.replace(2, (char)15); //5,Byte.MAX_VALUE,15,char.MAX_VALUE,50,60
		sut.insert(3, new char[]{25,26,27,28,29}); //5,Byte.MAX_VALUE,15,25,26,27,28,29,char.MAX_VALUE,50,60
		
		sut.reverse();  //60,50,char.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(1);  //60,char.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(Range.of(3,5)); //60,char.MAX_VALUE,29,25,15,Byte.MAX_VALUE,5
		sut.append((char)99);
		
		char[] expected = new char[]{60,Character.MAX_VALUE,29,25,15,Character.MAX_VALUE,5,99};
		assertArrayEquals(expected, sut.toArray());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNegativeSizeShouldThrowException(){
		new GrowableCharArray(-1);
	}
	@Test
	public void constructorWithSizeZeroShouldBeAllowed(){
		GrowableCharArray sut =new GrowableCharArray(0);
		assertEquals(0, sut.getCurrentLength());
		sut.append((char)10);
		assertEquals(1, sut.getCurrentLength());
		assertEquals(10, sut.get(0));
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullArrayShouldThrowException(){
		new GrowableCharArray((char[])null);
	}
	
	@Test(expected = NullPointerException.class)
	public void constructorWithNullCollectionShouldThrowException(){
		new GrowableCharArray((Collection<Character>)null);
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullElementInCollectionShouldThrowException(){
		List<Character> list = new ArrayList<Character>();
		list.add(Character.valueOf((char)1));
		list.add(null);
		
		new GrowableCharArray(list);
	}
	@Test
	public void constructorWithCollection(){
		List<Character> list = Arrays.asList((char)10,(char)20,(char)30,(char)40,(char)50);
		GrowableCharArray sut =new GrowableCharArray(list);
		
		char[] expected = new char[]{10,20,30,40, 50};
		
		assertArrayEquals(expected, sut.toArray());
	}
	
	
	
	@Test
	public void copy(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		GrowableCharArray copy = sut.copy();
		
		char[] expected = new char[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
		assertArrayEquals(expected, copy.toArray());
	}
	
	@Test
	public void modificationsToCopyShouldNotAffectOriginal(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		GrowableCharArray copy = sut.copy();
		copy.remove(2);

		assertArrayEquals(new char[]{10,20,30,40}, sut.toArray());
		assertArrayEquals(new char[]{10,20,40}, copy.toArray());
	}
	@Test
	public void modificationsToOriginalShouldNotAffectCopy(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		
		GrowableCharArray copy = sut.copy();
		sut.remove(2);

		assertArrayEquals(new char[]{10,20,30,40}, copy.toArray());
		assertArrayEquals(new char[]{10,20,40}, sut.toArray());
	}
	
	@Test
	public void reverseEvenNumberOfValues(){
		GrowableCharArray sut = new GrowableCharArray(5);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.reverse();
		
		assertArrayEquals(new char[]{40,30,20,10}, sut.toArray());
	}
	@Test
	public void reverseOddNumberOfValues(){
		GrowableCharArray sut = new GrowableCharArray(10);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.append((char)50);
		sut.reverse();
		
		assertArrayEquals(new char[]{50,40,30,20,10}, sut.toArray());
	}
	
	@Test
	public void sortUnSortedValues(){
		GrowableCharArray sut = new GrowableCharArray(10);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.append((char)50);
		sut.reverse();
		sut.sort();
		assertArrayEquals(new char[]{10,20,30,40,50}, sut.toArray());
	}
	
	@Test
	public void binarySearch(){
		GrowableCharArray sut = new GrowableCharArray(10);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.append((char)50);
		for(int i=0; i<sut.getCurrentLength(); i++){
			assertEquals(i, sut.binarySearch(sut.get(i)));
		}
		assertEquals("after all",-6, sut.binarySearch((char)60));
		assertEquals("before all", -1, sut.binarySearch((char)6));
		assertEquals("in between", -4, sut.binarySearch((char)35));
	}
	
	
	@Test
	public void sortedRemove(){
		GrowableCharArray sut = new GrowableCharArray(10);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.append((char)50);
		
		
		assertTrue(sut.sortedRemove((char)30));
		assertFalse(sut.sortedRemove((char)45));
		assertArrayEquals(new char[]{10, 20,40,50}, sut.toArray());

	}
	
	@Test
	public void sortedInsert(){
		GrowableCharArray sut = new GrowableCharArray(10);
		sut.append((char)10);
		sut.append((char)20);
		sut.append((char)30);
		sut.append((char)40);
		sut.append((char)50);
		
		
		assertEquals(3,sut.sortedInsert((char)35));
		assertEquals(2,sut.sortedInsert((char)30));
		assertArrayEquals(new char[]{10, 20,30,30,35, 40,50}, sut.toArray());

	}
	
	
	@Test
	public void iterator(){
		GrowableCharArray sut = new GrowableCharArray(new char[]{10, 20,30,30,35, 40,50});
		
		Iterator<Character> expected = Arrays.asList((char)10,(char) 20,(char)30,(char)30,(char)35,(char) 40, (char)50).iterator();
		
		Iterator<Character> actual = sut.iterator();
		
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}
	
	@Test
	public void sortedInsertSmallerArray(){
		GrowableCharArray sut = new GrowableCharArray(new char[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new char[]{15,17,31,37,60});
		assertArrayEquals(new char[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60}, sut.toArray());
	}
	@Test
	public void sortedInsertLargerArray(){
		GrowableCharArray sut = new GrowableCharArray(new char[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new char[]{15,17,31,37,60,61,62,63,64,65});
		assertArrayEquals(new char[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60,61,62,63,64,65}, sut.toArray());
	}
	@Test
	public void sortedInsertSameSizedArray(){
		GrowableCharArray sut = new GrowableCharArray(new char[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new char[]{15,17,31,37,60,61,62});
		assertArrayEquals(new char[]{10,15,17, 20,30,30,31, 35, 37, 40,50,60,61,62}, sut.toArray());
	}
	
	@Test
	public void sortedInsertEmptyArraShouldMakeNoChanges(){
		GrowableCharArray sut = new GrowableCharArray(new char[]{10, 20,30,30,35, 40,50});
		
		sut.sortedInsert(new char[0]);
		assertArrayEquals(new char[]{10, 20,30,30,35, 40,50}, sut.toArray());
	}
	
	@Test
	public void sortedInsertOnEmptyGrowableArrayShouldAppend(){
		GrowableCharArray sut = new GrowableCharArray(10);
		
		sut.sortedInsert(new char[]{15,17,31,37,60,61,62});
		assertArrayEquals(new char[]{15,17,31,37,60,61,62}, sut.toArray());
	}
	
	@Test
	public void getCount(){
		GrowableCharArray sut = new GrowableCharArray(new char[]{10, 20,30,30,35, 40,50});
		assertEquals(2, sut.getCount((char)30));
	}
	@Test
	public void getCountNoMatchesShouldReturn0(){
		GrowableCharArray sut = new GrowableCharArray(new char[]{10, 20,30,30,35, 40,50});
		assertEquals(0, sut.getCount((char)200));
	}
}
