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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestUnsignedIntArray {

	@Test(expected = NullPointerException.class)
	public void nullConstructorShouldThrowNPE(){
		new UnsignedIntArray(null);
	}
	
	@Test
	public void arrayOfPositiveSignedValues(){
		int[] expected = new int[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE, Integer.MAX_VALUE
				
		};
		
		UnsignedIntArray sut = new UnsignedIntArray(expected);
		assertEquals(expected.length, sut.getLength());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
		
	}
	
	@Test
	public void arrayOfMixOfSignedAndUnsignedValues(){
		int[] expected = new int[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE, Integer.MAX_VALUE,
				Integer.MIN_VALUE, -1
				
		};
		
		UnsignedIntArray sut = new UnsignedIntArray(expected);
		assertEquals(expected.length, sut.getLength());
		for(int i=0; i< expected.length; i++){
			long actual = sut.get(i);
			if(actual >Integer.MAX_VALUE){
				assertEquals(expected[i], IOUtil.toSignedInt(actual));
			}else{
				assertEquals(expected[i], actual);
			}
		}
		
	}
	
	@Test
	public void changeValuesInArrayAfterObjCreationShouldSeeUpdatedValues(){
		int[] input = new int[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Integer.MIN_VALUE, -1			
				
		};
		
		UnsignedIntArray sut = new UnsignedIntArray(input);
		
		sut.put(3, 100);
		
		int[] expected = Arrays.copyOf(input, input.length);
		expected[3] = 100;
		assertEquals(expected.length, sut.getLength());
		for(int i=0; i< expected.length; i++){
			long actual = sut.get(i);
			if(actual >Integer.MAX_VALUE){
				assertEquals(expected[i], IOUtil.toSignedInt(actual));
			}else{
				assertEquals(expected[i], actual);
			}
		}
		
	}
	
	@Test
	public void notEqualToNull(){
		int[] expected = new int[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedIntArray sut1 = new UnsignedIntArray(expected);
		assertFalse(sut1.equals(null));
	}
	
	@Test
	public void notEqualToOtherClass(){
		int[] expected = new int[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedIntArray sut1 = new UnsignedIntArray(expected);
		assertFalse(sut1.equals(null));
	}
	
	@Test
	public void sameValuesShouldBeEquals(){
		int[] expected = new int[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedIntArray sut1 = new UnsignedIntArray(expected);
		UnsignedIntArray sut2 = new UnsignedIntArray(expected);
		TestUtil.assertEqualAndHashcodeSame(sut1, sut2);
	} 
	@Test
	public void diffValuesShouldNotBeEquals(){
		int[] expected = new int[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedIntArray sut1 = new UnsignedIntArray(expected);
		int[] array2 = new int[expected.length];
		
		System.arraycopy(expected, 0, array2, 0	, array2.length);
		//change some values
		array2[2] = 17;
		UnsignedIntArray sut2 = new UnsignedIntArray(array2);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut1, sut2);
	} 
}
