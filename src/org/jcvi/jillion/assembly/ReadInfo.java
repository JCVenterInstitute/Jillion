/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class ReadInfo{

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
	public ReadInfo(Range validRange, int fullLength) {
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
	/**
     * Get the valid {@link Range} which is ungapped "good" part of the basecalls.  Depending
     * on what this {@link NucleotideSequence} represents can change the 
     * meaning of valid range some possible meanings include:
     * <ul>
     * <li>the high quality region<li>
     * <li>the region that aligns to a reference</li>
     * <li>the region used to compute assembly consensus</li>
     * </ul>
     * The maximum possible valid range length is the length
     * returned by {@link #getUngappedFullLength()}.
     * @return a Range with a minimum (zero-based) begin value of 0 
     * and a max (zero-based) end value of {@link #getUngappedFullLength()} -1.
     */
	public Range getValidRange() {
		return validRange;
	}
	/**
     * Get the ungapped full length of this read <strong>including bases outside of the valid range</strong>.
     * If this read has any portion of the read that was trimmed off because of bad quality, primer/vector sequence
     * or because it did not fully align to the reference then those portions still counted by this method.
     * @return the full length including bases outside of the valid range; always positive.
     */
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
	/**
	 *Two ReadInfos are equal if they have the same
	 *valid range and full length values.
	 */
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
		ReadInfo other = (ReadInfo) obj;
		if (fullLength != other.fullLength) {
			return false;
		}
		if (!validRange.equals(other.validRange)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "ReadInfo [validRange=" + validRange + ", fullLength="
				+ fullLength + "]";
	}
	
	

}
