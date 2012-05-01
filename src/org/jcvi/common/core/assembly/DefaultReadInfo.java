package org.jcvi.common.core.assembly;

import org.jcvi.common.core.Range;

public class DefaultReadInfo implements ReadInfo {

	private final Range validRange;
	private final int fullLength;
	
	/**
	 * Create a new {@link ReadInfo}.
	 * @param validRange the (ungapped) valid range for this
	 * read in fulllength coordinates; can not be null.
	 * @param fullLength the ungapped full length of this read
	 * including any portions that have been trimmed off;
	 * must be >=0.
	 * @throws NullPointerException if validRange is null
	 * @throws IllegalArgumentException if {@code fullLength < 0 } or
	 * if  {@code fullLength < validRange.getEnd()} 
	 */
	public DefaultReadInfo(Range validRange, int fullLength) {
		if(validRange ==null){
			throw new NullPointerException("valid range can not be null");
		}
		if(fullLength < 0){
			throw new IllegalArgumentException("full length must be >=0");
		}
		if(fullLength <validRange.getEnd()){
			throw new IllegalArgumentException("full length must be >=validRange.getEnd()");
		}
		this.validRange = validRange;
		this.fullLength = fullLength;
	}

	@Override
	public Range getValidRange() {
		return validRange;
	}

	@Override
	public int getUngappedFullLength() {
		return fullLength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fullLength;
		result = prime * result
				+ validRange.hashCode();
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		DefaultReadInfo other = (DefaultReadInfo) obj;
		if (fullLength != other.fullLength) {
			return false;
		}
		if (!validRange.equals(other.validRange)) {
			return false;
		}
		return true;
	}
	
	

}
