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
