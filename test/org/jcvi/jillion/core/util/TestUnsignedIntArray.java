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
