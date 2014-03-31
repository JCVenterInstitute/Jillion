package org.jcvi.jillion.sam.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BaiBin implements Bin {

	private int binId;
	
	private List<Chunk> chunks;
	
	
	
	public BaiBin(int binId, Chunk[] chunks) {
		this.binId = binId;
		
		List<Chunk> chunkList = new ArrayList<Chunk>(chunks.length);
		for(Chunk c : chunks){
			chunkList.add(c);
		}
		//TODO add code to remove bin meta data
		//which Picard stores as last bin?
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
		result = prime * result + ((chunks == null) ? 0 : chunks.hashCode());
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
		if (!(obj instanceof BaiBin)) {
			return false;
		}
		BaiBin other = (BaiBin) obj;
		if (binId != other.binId) {
			return false;
		}
		if (chunks == null) {
			if (other.chunks != null) {
				return false;
			}
		} else if (!chunks.equals(other.chunks)) {
			return false;
		}
		return true;
	}
	
	

}
