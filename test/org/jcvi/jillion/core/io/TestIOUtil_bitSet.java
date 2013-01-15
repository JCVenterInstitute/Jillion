package org.jcvi.jillion.core.io;

import java.math.BigInteger;
import java.util.BitSet;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_bitSet {

	@Test
	public void noBytes(){
		BitSet bits= new BitSet();
		
		byte[] actualBytes = IOUtil.toByteArray(bits,0);
		assertArrayEquals(new byte[0], actualBytes);
	}
	
	@Test
	public void oneBitSet(){
		BitSet bits= new BitSet();
		
		bits.set(0);
		byte[] actualBytes = IOUtil.toByteArray(bits,1);
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
		byte[] actualBytes = IOUtil.toByteArray(bits,8);
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
		
		byte[] actualBytes = IOUtil.toByteArray(bits,16);
		assertArrayEquals(expected, actualBytes);
		assertEquals(new BigInteger(expected), new BigInteger(actualBytes));
		assertEquals(bits, IOUtil.toBitSet(actualBytes));
	}
	
	@Test(expected = NullPointerException.class)
	public void toByteArrayShouldThrowNPEIfBitSetIsNull(){
		IOUtil.toByteArray((BitSet)null,0);
	}
	@Test(expected = IllegalArgumentException.class)
	public void toByteArrayNegativeBitLengthShouldThrowException(){
		IOUtil.toByteArray(new BitSet(),-1);
	}
	@Test(expected = NullPointerException.class)
	public void toBitSetShouldThrowNPEIfArrayIsNull(){
		IOUtil.toBitSet((byte[])null);
	}
	
	@Test
	public void toBitSetByte(){
		BitSet expected = new BitSet();
		expected.set(5);		
		assertEquals(expected, IOUtil.toBitSet((byte)32));
	}
	@Test
	public void toBitSetShort(){
		BitSet expected = new BitSet();
		expected.set(10);		
		assertEquals(expected, IOUtil.toBitSet((short)1024));
	}
	@Test
	public void toBitSetByteOnlyNeeds2bits(){
		BitSet expected = new BitSet();
		expected.set(0);
		expected.set(1);		
		assertEquals(expected, IOUtil.toBitSet((byte)3));
	}
}
