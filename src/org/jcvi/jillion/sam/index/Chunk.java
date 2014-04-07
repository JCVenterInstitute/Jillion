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
package org.jcvi.jillion.sam.index;

import org.jcvi.jillion.sam.VirtualFileOffset;

public class Chunk {

	private final VirtualFileOffset begin,end;

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

	public VirtualFileOffset getBegin() {
		return begin;
	}

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
