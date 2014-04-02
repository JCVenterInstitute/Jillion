package org.jcvi.jillion.sam.index;

import java.util.ArrayList;
import java.util.List;

public final class BinBuilder {

	private final int binNumber;

	private final List<Chunk> chunks = new ArrayList<Chunk>();
	
	public BinBuilder(int binNumber) {
		this.binNumber = binNumber;
	}
	
	public BinBuilder addChunk(Chunk chunk){
		if(chunk == null){
			throw new NullPointerException("chunk can not be null");
		}
		if(chunks.isEmpty()){
			//add this chunk as our first chunk
			chunks.add(chunk);
		}else{
			//maybe we can merge chunks?
			
			//we probably are adding chunks in sorted
			//order so we should only ever care
			//about the last chunk in our list
			Chunk lastChunk = chunks.get(chunks.size() -1);
			if(canBeMerged(lastChunk, chunk)){
				//replace last chunk with merged chunk
				Chunk mergedChunk = new Chunk(lastChunk.getBegin(),	chunk.getEnd());
				chunks.set(chunks.size() -1, mergedChunk);
			}else{
				//append
				chunks.add(chunk);
			}
		}
		
		return this;
	}
	/**
	 * Two chunks can be merged
	 * if the end block of the first chunk
	 *  and the start block
	 * of the other chunk are the same
	 * compressed block
	 * or if they are in adjacent compressed blocks.
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	private boolean canBeMerged(Chunk c1, Chunk c2) {
		long endBlockOffsetOf1 = c1.getEnd().getCompressedBamBlockOffset();
		long startBlockOffsetOf2 = c2.getBegin().getCompressedBamBlockOffset();
		//we can merge these 2 chunks if
		//they are in the same compressed block
		//or if they are in adjacent compressed blocks
		return endBlockOffsetOf1 == startBlockOffsetOf2 
				|| endBlockOffsetOf1 +1 == startBlockOffsetOf2 ;
	}
	
	public Bin build(){
		return new BinImpl(this);
	}
		
	private static final class BinImpl implements Bin{
			
		private final int binNumber;
		private final List<Chunk> chunks;
		
		private BinImpl(BinBuilder builder){
			this.binNumber = builder.binNumber;
			this.chunks = new ArrayList<Chunk>(builder.chunks);
		}
		
		
		@Override
		public int getBinNumber() {
			return binNumber;
		}


		@Override
		public List<Chunk> getChunks() {
			return chunks;
		}



		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + binNumber;
			result = prime * result + chunks.hashCode();
			return result;
		}


		@Override
		public String toString() {
			return "Bin [binNumber=" + binNumber + ", chunks=" + chunks + "]";
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
			if (binNumber != other.getBinNumber()) {
				return false;
			}
			if (!chunks.equals(other.getChunks())) {
				return false;
			}
			return true;
		}
	}
}
