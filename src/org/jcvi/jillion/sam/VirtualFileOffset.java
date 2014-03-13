package org.jcvi.jillion.sam;


public class VirtualFileOffset implements Comparable<VirtualFileOffset>{
	//2^49 -1
	private static final long MAX_COMPRESSED_BLOCK_OFFSET = 562949953421311L;
	//2^17 -1
	private static final int MAX_UNCOMPRESSED_OFFSET = 131071;
	private final long encodedValue;

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
