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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.junit.Test;

public class TestGrowableIntArray {
	@Test
	public void append(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		int[] expected = new int[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void replace(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		sut.replace(1, (int)15);
		int[] expected = new int[]{10,15,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendBeyondCapacityShouldGrowArray(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		sut.append((int)50);
		sut.append((int)60);
		
		int[] expected = new int[]{10,20,30,40,50,60};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void remove(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.remove(2);
		
		int[] expected = new int[]{10,20,40};
		assertToArrayCorrect(sut, expected);
	}
	private void assertToArrayCorrect(GrowableIntArray sut, int[] expected) {
		assertEquals(expected.length, sut.getCurrentLength());
		assertArrayEquals(expected, sut.toArray());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
	}
	@Test
	public void removeRange(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.remove(Range.of(1,2));
		
		int[] expected = new int[]{10,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void removeEmptyRangeShouldDoNothing(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.remove(new Range.Builder().shift(2).build());
		
		int[] expected = new int[]{10,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insert(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.insert(1, (int)15);
		
		int[] expected = new int[]{10,15,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void insertAtOffsetLengthShouldActLikeAppend(){
		GrowableIntArray sut = new GrowableIntArray(4);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.insert(4, (int)50);
		
		int[] expected = new int[]{10,20,30,40,50};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertArray(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.insert(1, new int[]{15,16,17,18,19});
		
		int[] expected = new int[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertOtherGrowableIntArray(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.insert(1, new GrowableIntArray(new int[]{15,16,17,18,19}));
		
		int[] expected = new int[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void prepend(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.prepend((int)10);
		
		int[] expected = new int[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void prependArray(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.prepend(new int[]{10,11,12,13});
		
		int[] expected = new int[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void prependOtherGrowableIntArray(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.prepend(new GrowableIntArray(new int[]{10,11,12,13}));
		
		int[] expected = new int[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendArray(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.append(new int[]{50,60,70,80,90});
		int[] expected = new int[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendOtherGrowableIntArray(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		sut.append(new GrowableIntArray(new int[]{50,60,70,80,90}));
		int[] expected = new int[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void constructUsingInitialArray(){
		int[] array = new int[]{10,20,30,40,50};
		GrowableIntArray sut = new GrowableIntArray(array);
		assertArrayEquals(array, sut.toArray());
	}
	@Test
	public void mixOfAllOperations(){
		int[] initialArray = new int[]{Byte.MAX_VALUE,20,Integer.MAX_VALUE,40,50};
		GrowableIntArray sut = new GrowableIntArray(initialArray);
		sut.append((int)60);  //Byte.MAX_VALUE,20,Integer.MAX_VALUE,40,50,60
		sut.remove(3);  //Byte.MAX_VALUE,20,Integer.MAX_VALUE,50,60
		sut.prepend((int)5);  //5,Byte.MAX_VALUE,20,Integer.MAX_VALUE,50,60
		sut.replace(2, (int)15); //5,Byte.MAX_VALUE,15,Integer.MAX_VALUE,50,60
		sut.insert(3, new int[]{25,26,27,28,29}); //5,Byte.MAX_VALUE,15,25,26,27,28,29,Integer.MAX_VALUE,50,60
		
		sut.reverse();  //60,50,Integer.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(1);  //60,Integer.MAX_VALUE,29,28,27,26,25,15,Byte.MAX_VALUE,5
		sut.remove(Range.of(3,5)); //60,Integer.MAX_VALUE,29,25,15,Byte.MAX_VALUE,5
		sut.append((int)99);
		
		int[] expected = new int[]{60,Integer.MAX_VALUE,29,25,15,Byte.MAX_VALUE,5,99};
		assertArrayEquals(expected, sut.toArray());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNegativeSizeShouldThrowException(){
		new GrowableIntArray(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithSizeZeroShouldThrowException(){
		new GrowableIntArray(0);
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullArrayShouldThrowException(){
		new GrowableIntArray(null);
	}
	@Test
	public void copy(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		GrowableIntArray copy = sut.copy();
		
		int[] expected = new int[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
		assertArrayEquals(expected, copy.toArray());
	}
	
	@Test
	public void modificationsToCopyShouldNotAffectOriginal(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		GrowableIntArray copy = sut.copy();
		copy.remove(2);

		assertArrayEquals(new int[]{10,20,30,40}, sut.toArray());
		assertArrayEquals(new int[]{10,20,40}, copy.toArray());
	}
	@Test
	public void modificationsToOriginalShouldNotAffectCopy(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		
		GrowableIntArray copy = sut.copy();
		sut.remove(2);

		assertArrayEquals(new int[]{10,20,30,40}, copy.toArray());
		assertArrayEquals(new int[]{10,20,40}, sut.toArray());
	}
	
	@Test
	public void reverseEvenNumberOfValues(){
		GrowableIntArray sut = new GrowableIntArray(5);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		sut.reverse();
		
		assertArrayEquals(new int[]{40,30,20,10}, sut.toArray());
	}
	@Test
	public void reverseOddNumberOfValues(){
		GrowableIntArray sut = new GrowableIntArray(10);
		sut.append((int)10);
		sut.append((int)20);
		sut.append((int)30);
		sut.append((int)40);
		sut.append((int)50);
		sut.reverse();
		
		assertArrayEquals(new int[]{50,40,30,20,10}, sut.toArray());
	}
	

}
