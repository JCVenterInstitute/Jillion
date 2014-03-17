package org.jcvi.jillion.sam;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestVirtualFileOffset {

	//2^49 -1
	private static final long MAX_COMPRESSED_BLOCK_OFFSET = 562949953421311L;
	//2^17 -1
	private static final int MAX_UNCOMPRESSED_OFFSET = 131071;
	
	private long compressedBlockOffset = 98765;
	
	private int uncompressedOffset = 12345;
	
	VirtualFileOffset sut = VirtualFileOffset.create(compressedBlockOffset, uncompressedOffset);
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeCompressedOffsetShouldThrowException(){
		VirtualFileOffset.create(-1,0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeUnCompressedOffsetShouldThrowException(){
		VirtualFileOffset.create(0,-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void compressedOffsetGreaterThanMaxShouldThrowException(){
		VirtualFileOffset.create(MAX_COMPRESSED_BLOCK_OFFSET +1L,  5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void unCompressedOffsetGreaterThanMaxShouldThrowException(){
		VirtualFileOffset.create(0,MAX_UNCOMPRESSED_OFFSET +1);
	}
	
	@Test
	public void getters(){
		assertEquals(compressedBlockOffset, sut.getCompressedBamBlockOffset());
		assertEquals(uncompressedOffset, sut.getUncompressedOffset());
	}
	
	@Test
	public void encodedValue(){
		long actual = sut.getEncodedValue();
		long expected = compressedBlockOffset<<16;
		expected |= uncompressedOffset;
		
		assertEquals(expected, actual);
	}
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void notEqualToDifferentObj(){
		assertFalse(sut.equals("not vfo"));
	}
	@Test
	public void equalsSameRef(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	
	@Test
	public void equalsSameValues(){
		VirtualFileOffset other = VirtualFileOffset.create(compressedBlockOffset, uncompressedOffset);
		TestUtil.assertEqualAndHashcodeSame(sut, other);
	}
	
	@Test
	public void notEqualDifferenCompressedOffset(){
		VirtualFileOffset other = VirtualFileOffset.create(
				compressedBlockOffset+1L,
				uncompressedOffset);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);

	}
	
	@Test
	public void notEqualDifferentUncompressedOffset(){
		VirtualFileOffset other = VirtualFileOffset.create(
				compressedBlockOffset,
				uncompressedOffset +1);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);

	}
	
	@Test
	public void equalShouldCompareToZero(){
		assertEquals(0, sut.compareTo(sut));
	}
	
	@Test
	public void compareToSameCompressedBlockDifferentUncompressedOffset(){
		VirtualFileOffset other = VirtualFileOffset.create(
				compressedBlockOffset,
				uncompressedOffset +1);
		assertTrue( sut.compareTo(other) < 0);
		assertTrue( other.compareTo(sut) > 0);		
	}
	
	@Test
	public void compareToSameUncompressedBlockDifferentCompressedOffset(){
		VirtualFileOffset other = VirtualFileOffset.create(
				compressedBlockOffset+1,
				uncompressedOffset);
		assertTrue( sut.compareTo(other) < 0);
		assertTrue( other.compareTo(sut) > 0);		
	}
}
