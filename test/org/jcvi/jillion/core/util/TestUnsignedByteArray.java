/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestUnsignedByteArray {

	@Test(expected = NullPointerException.class)
	public void nullConstructorShouldThrowNPE(){
		new UnsignedByteArray(null);
	}
	
	@Test
	public void arrayOfPositiveSignedValues(){
		byte[] expected = new byte[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2				
				
		};
		
		UnsignedByteArray sut = new UnsignedByteArray(expected);
		assertEquals(expected.length, sut.getLength());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
		
	}
	
	@Test
	public void arrayOfMixOfSignedAndUnsignedValues(){
		byte[] expected = new byte[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MIN_VALUE, -1				
				
		};
		
		UnsignedByteArray sut = new UnsignedByteArray(expected);
		assertEquals(expected.length, sut.getLength());
		for(int i=0; i< expected.length; i++){
			int actual = sut.get(i);
			if(actual >Byte.MAX_VALUE){
				assertEquals(expected[i], IOUtil.toSignedByte(actual));
			}else{
				assertEquals(expected[i], actual);
			}
		}
		
	}
	
	@Test
	public void changeValuesInArrayAfterObjCreationShouldNotUpdateValues(){
		byte[] input = new byte[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MIN_VALUE, -1				
				
		};
		
		UnsignedByteArray sut = new UnsignedByteArray(input);
		
		sut.put(3, 100);
		byte[] expected = Arrays.copyOf(input, input.length);
		expected[3] = 100;
		
		assertEquals(input.length, sut.getLength());
		for(int i=0; i< input.length; i++){
			int actual = sut.get(i);
			if(actual >Byte.MAX_VALUE){
				assertEquals(expected[i], IOUtil.toSignedByte(actual));
			}else{
				assertEquals(expected[i], actual);
			}
		}
		
	}
	
	@Test
	public void notEqualToNull(){
		byte[] expected = new byte[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MIN_VALUE, -1
		};
		
		UnsignedByteArray sut1 = new UnsignedByteArray(expected);
		assertFalse(sut1.equals(null));
	}
	
	@Test
	public void notEqualToOtherClass(){
		byte[] expected = new byte[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MIN_VALUE, -1
		};
		
		UnsignedByteArray sut1 = new UnsignedByteArray(expected);
		assertFalse(sut1.equals(null));
	}
	
	@Test
	public void sameValuesShouldBeEquals(){
		byte[] expected = new byte[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MIN_VALUE, -1
		};
		
		UnsignedByteArray sut1 = new UnsignedByteArray(expected);
		UnsignedByteArray sut2 = new UnsignedByteArray(expected);
		TestUtil.assertEqualAndHashcodeSame(sut1, sut2);
	} 
	@Test
	public void diffValuesShouldNotBeEquals(){
		byte[] expected = new byte[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MIN_VALUE, -1
		};
		
		UnsignedByteArray sut1 = new UnsignedByteArray(expected);
		byte[] array2 = new byte[expected.length];
		
		System.arraycopy(expected, 0, array2, 0	, array2.length);
		//change some values
		array2[2] = 17;
		UnsignedByteArray sut2 = new UnsignedByteArray(array2);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut1, sut2);
	} 
}
