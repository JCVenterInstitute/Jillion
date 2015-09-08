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
