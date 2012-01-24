package org.jcvi.common.core.io;

import java.math.BigInteger;
import java.util.BitSet;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_bitSet {

	@Test
	public void noBytes(){
		BitSet bits= new BitSet();
		
		byte[] actualBytes = IOUtil.toByteArray(bits);
		assertArrayEquals(new byte[0], actualBytes);
	}
	
	@Test
	public void oneBitSet(){
		BitSet bits= new BitSet();
		
		bits.set(0);
		byte[] actualBytes = IOUtil.toByteArray(bits);
		byte[] expected = new byte[1];
		expected[0] = 1;
		assertArrayEquals(expected, actualBytes);
		
		BitSet convertedBack = IOUtil.toBitSet(actualBytes);
		assertEquals(bits,convertedBack);
	}
	
	@Test
	public void oneByte(){
		BitSet bits= new BitSet();
		bits.set(1,8); // 11111110 = 254
		byte[] actualBytes = IOUtil.toByteArray(bits);
		byte[] expected = new byte[1];
		expected[0] = (byte)254;
		assertEquals(new BigInteger(expected), new BigInteger(actualBytes));
		assertEquals(bits, IOUtil.toBitSet(actualBytes));
	}
	
	@Test
	public void multipleBytes(){
		BitSet bits= new BitSet();
		bits.set(1,8); // 11111110 = 254
		bits.set(15); //1000000 = -128
		
		byte[] expected = new byte[2];
		expected[1] = (byte)254;
		expected[0] = (byte)-128;
		
		byte[] actualBytes = IOUtil.toByteArray(bits);
		assertArrayEquals(expected, actualBytes);
		assertEquals(new BigInteger(expected), new BigInteger(actualBytes));
		assertEquals(bits, IOUtil.toBitSet(actualBytes));
	}
	
	@Test(expected = NullPointerException.class)
	public void toByteArrayShouldThrowNPEIfBitSetIsNull(){
		IOUtil.toByteArray(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void toBitSetShouldThrowNPEIfArrayIsNull(){
		IOUtil.toBitSet(null);
	}
	
}
