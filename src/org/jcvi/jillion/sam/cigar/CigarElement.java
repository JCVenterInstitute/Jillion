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
package org.jcvi.jillion.sam.cigar;
/**
 * Value object containing a single 
 * element of a Cigar.
 * @author dkatzel
 *
 */
public final class CigarElement {
	//since the BAM file
	//stores the op_length in 28bits
	//we can safely use a signed int
	//which can store 31 bits
	private final int length;
	
	private final CigarOperation op;
	/**
	 * Create a new {@link CigarElement} object.
	 * 
	 * @param op The {@link CigarOperation}; can not be null.
	 * 
	 * @param length the length of this operation; can not be < 1.
	 * 
	 * @throws NullPointerException if op is null.
	 * @throws IllegalArgumentException if length < 1.
	 */
	public CigarElement(CigarOperation op, int length) {
		if(op==null){
			throw new NullPointerException("cigar op can not be null");
		}
		//length shouldn't be 0
		if(length<1){
			throw new IllegalArgumentException("length can < 1");
		}
		this.op = op;
		this.length = length;
	}
	/**
	 * Get the length of this operation.
	 * @return the length as a positive int.
	 */
	public int getLength() {
		return length;
	}
	/**
	 * Get the {@link CigarOperation} of this element.
	 * @return a {@link CigarOperation}; will never be null.
	 */
	public CigarOperation getOp() {
		return op;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + op.hashCode();
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
		if (!(obj instanceof CigarElement)) {
			return false;
		}
		CigarElement other = (CigarElement) obj;
		if (length != other.length) {
			return false;
		}
		if (op != other.op) {
			return false;
		}
		return true;
	}
	
	
	
}
