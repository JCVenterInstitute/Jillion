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
package org.jcvi.jillion.internal.sam.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.sam.index.Bin;
import org.jcvi.jillion.sam.index.Chunk;

class BaiBin implements Bin {

	private int binId;
	
	private List<Chunk> chunks;
	
	
	
	public BaiBin(int binId, Chunk[] chunks) {
		this.binId = binId;
		
		List<Chunk> chunkList = new ArrayList<Chunk>(chunks.length);
		for(Chunk c : chunks){
			chunkList.add(c);
		}
		this.chunks = Collections.unmodifiableList(chunkList);
	}

	@Override
	public int getBinNumber() {
		return binId;
	}

	@Override
	public List<Chunk> getChunks() {
		return chunks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + binId;
		result = prime * result + chunks.hashCode();
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
		if (!(obj instanceof Bin)) {
			return false;
		}
		Bin other = (Bin) obj;
		if (binId != other.getBinNumber()) {
			return false;
		}
		if (!chunks.equals(other.getChunks())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BaiBin [binId=" + binId + ", chunks=" + chunks + "]";
	}
	
	

}
