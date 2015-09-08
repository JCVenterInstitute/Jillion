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
package org.jcvi.jillion.sam.index;

import org.jcvi.jillion.sam.VirtualFileOffset;

/**
 * A {@code Chunk} represents a single span of alignments
 * where all the aligned reads in the sorted BAM belong
 * to the same {@link Bin}.
 * @author dkatzel
 *
 */
public class Chunk {

	private final VirtualFileOffset begin,end;
	/**
	 * Create a new Chunk with the given begin and end {@link VirtualFileOffset}s.
	 * Usually begin < end however, some BamIndex implementations
	 * violate this rule to store metadata in a backwards
	 * compatible way.
	 * @param begin the begin offset; can not be null.
	 * @param end the begin offset; can not be null.
	 * @throws NullPointerException if either parameter
	 * is null.
	 */
	public Chunk(VirtualFileOffset begin, VirtualFileOffset end) {
		if(begin ==null){
			throw new NullPointerException("begin can not be null");
		}
		if(end ==null){
			throw new NullPointerException("end can not be null");
		}
		this.begin = begin;
		this.end = end;
	}
	/**
	 * Get the {@link VirtualFileOffset}
	 * of the beginning of this Chunk.
	 * @return a VirtualFileOffset, will never be null.
	 */
	public VirtualFileOffset getBegin() {
		return begin;
	}
	/**
	 * Get the {@link VirtualFileOffset}
	 * of the end of this Chunk.
	 * @return a VirtualFileOffset, will never be null.
	 */
	public VirtualFileOffset getEnd() {
		return end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin.hashCode();
		result = prime * result +  end.hashCode();
		return result;
	}
	/**
	 * Two {@link Chunk}s are equal if they have
	 * the same begin and end values.
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Chunk)) {
			return false;
		}
		Chunk other = (Chunk) obj;
		if (!begin.equals(other.begin)) {
			return false;
		}
		if (!end.equals(other.end)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Chunk [begin=" + begin + ", end=" + end + "]";
	}
	
	
	
}
