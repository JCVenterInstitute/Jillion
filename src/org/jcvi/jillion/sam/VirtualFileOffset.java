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
package org.jcvi.jillion.sam;

/**
 * {@code VirtualFileOffset} is an object
 * representation of a BAM virtual file offset
 * as defined in the SAM specification.
 * The VirutalFileOffset encodes a file offset
 * as two coordinates : the file offset into
 * the compressed BAM blocks and then an additional offset
 * into the <em>un</em>compressed block.
 * @author dkatzel
 *
 */
public class VirtualFileOffset implements Comparable<VirtualFileOffset>{
	//2^49 -1
	private static final long MAX_COMPRESSED_BLOCK_OFFSET = 562949953421311L;
	//2^17 -1
	private static final int MAX_UNCOMPRESSED_OFFSET = 131071;
	private final long encodedValue;
	/**
	 * Create a new {@link VirtualFileOffset}
	 * instance using the given compressedBlock offset and 
	 * uncompressed offset into the current block.
	 * @param compressedBlockOffset the number of compressed bytes to read
	 * from the beginning of the BAM file until the current BGZF block is reached;
	 * must be >=0 and less than 2<sup>49</sup>-1.
	 * @param uncompressedOffset the number of <em>un</em>compressed bytes 
	 * read in the current BGZF block; must be between 0 and 2<sup>17</sup>-1.
	 * @return a new {@link VirtualFileOffset} instance; will never be null.
	 * @throws IllegalArgumentException if the provided offset values are out of range.
	 */
	public static VirtualFileOffset create(long compressedBlockOffset, int uncompressedOffset){
		if(compressedBlockOffset <0){
			throw new IllegalArgumentException("compressed BlockOffset can not be negative : " + compressedBlockOffset);
		}
		if(compressedBlockOffset > MAX_COMPRESSED_BLOCK_OFFSET){
			throw new IllegalArgumentException("compressed BlockOffset can not be larger than " + MAX_COMPRESSED_BLOCK_OFFSET + " : " + compressedBlockOffset);
		}
		if(uncompressedOffset <0){
			throw new IllegalArgumentException("uncompressed offset can not be negative : " + uncompressedOffset);
		}
		if(uncompressedOffset > MAX_UNCOMPRESSED_OFFSET){
			throw new IllegalArgumentException("uncompressed offset can not be larger than" + MAX_UNCOMPRESSED_OFFSET + " : " + uncompressedOffset);
		}
		long encodedValue = compressedBlockOffset <<16;
		encodedValue |= uncompressedOffset;
		return new VirtualFileOffset(encodedValue);
	}
	
	public VirtualFileOffset(long encodedValue) {
		this.encodedValue = encodedValue;
	}
	
	
	
	public long getEncodedValue() {
		return encodedValue;
	}

	public long getCompressedBamBlockOffset(){
		return encodedValue>>16;
	}
	
	public int getUncompressedOffset(){
		return (int)(encodedValue & 0xFFFF);
	}

	@Override
	public int compareTo(VirtualFileOffset o) {
		if( encodedValue < o.encodedValue){
			return -1;
		}
		if(encodedValue == o.encodedValue){
			return 0;
		}
		return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (encodedValue ^ (encodedValue >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VirtualFileOffset)) {
			return false;
		}
		VirtualFileOffset other = (VirtualFileOffset) obj;
		if (encodedValue != other.encodedValue) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "VirtualFileOffset [getCompressedBamBlockOffset()="
				+ getCompressedBamBlockOffset() + ", getUncompressedOffset()="
				+ getUncompressedOffset() + "]";
	}
	
	
}
