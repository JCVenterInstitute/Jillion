package org.jcvi.jillion.sam.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.sam.SamUtil;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.index.IndexUtil.BinSorter;

public final class ReferenceIndexBuilder{
	
	private final VirtualFileOffset[] intervals;

	private int largestIndexUsed=-1;
	//need to keep type as ArrayList since
	//we use trimToSize() method which is only on arraylist.
	@SuppressWarnings("PMD.LooseCoupling")
	private final ArrayList<Bin> bins = new ArrayList<Bin>();
	private int currentBinNumber =1;
	private BinBuilder currentBinBuilder = null;
	private long numberOfUnalignedReads=0, numberOfAlignedReads=0;
	private VirtualFileOffset lowestStart = new VirtualFileOffset(Long.MAX_VALUE);
	private VirtualFileOffset higestEnd = new VirtualFileOffset(0L);
	
	public ReferenceIndexBuilder(int length){
		this.intervals = new VirtualFileOffset[IndexUtil.getIntervalOffsetFor(length)];
		
	}
	
	public void addAlignment(int readStartOffset, int readEndOffsetExclusive, VirtualFileOffset start, VirtualFileOffset end){
		
		updateIntervals(readStartOffset, readEndOffsetExclusive-1, start, end);		
		updateBins(readStartOffset, readEndOffsetExclusive, start, end);
	}
	
	public void incrementUnmappedCount() {
		numberOfUnalignedReads++;
	}
	
	public ReferenceIndex build(){
		if(currentBinBuilder !=null){
			//add last bin to
			//our list of bins used.
			bins.add(currentBinBuilder.build());
		}
		bins.trimToSize();
		//sort bins in order
		Collections.sort(bins, BinSorter.INSTANCE);
		return new ReferenceIndexImpl(this);
	}
	
	private void updateBins(int readStartOffset, int readEndOffsetExclusive,
			VirtualFileOffset start, VirtualFileOffset end) {
		int bin = SamUtil.computeBinFor(readStartOffset, readEndOffsetExclusive);
		if(bin != currentBinNumber){
			if(currentBinBuilder !=null){
				//builder old bin and add it to
				//our list of bins used.
				bins.add(currentBinBuilder.build());
			}
			//make new bin builder for this new bin
			currentBinBuilder = new BinBuilder(bin);
			//update bin number
			currentBinNumber = bin;
		}
		//assume that the alignments are in sorted order
		//so we will only see bins that are >= current bin
		//so if bin isn't greater than the current bin number
		//than we must be in the same bin.
		currentBinBuilder.addChunk(new Chunk(start, end));
		numberOfAlignedReads++;
	}

	public void updateIntervals(int readStartOffset, int readEndOffset, VirtualFileOffset start, VirtualFileOffset end) {
		int startInterval = IndexUtil.getIntervalOffsetFor(readStartOffset) -1;
		int endInterval = IndexUtil.getIntervalOffsetFor(readEndOffset) -1;
		
		if(endInterval > largestIndexUsed){
			largestIndexUsed = endInterval;
		}
		for(int i = startInterval; i<=endInterval; i++){
			VirtualFileOffset currentValue = intervals[i];
			if(currentValue ==null || start.compareTo(currentValue) < 0){
				intervals[i] = start;
			}
		}
		
		if(start.compareTo(lowestStart)<0){
			lowestStart = start;
		}
		if(end.compareTo(higestEnd) >0){
			higestEnd = end;
		}
	}

	private static final class ReferenceIndexImpl implements ReferenceIndex {

		private final List<Bin> bins;
		private final VirtualFileOffset[] intervals;
		private final Long numberOfUnAlignedReads, numberOfAlignedReads;
		private final VirtualFileOffset lowestOffset, highestOffset;
		
		
		private ReferenceIndexImpl(ReferenceIndexBuilder builder){
			this.bins = builder.bins;
			this.intervals = Arrays.copyOf(builder.intervals, builder.largestIndexUsed+1);
			
			//match sam tools which only includes upto the max intervals seen
			//instead of the intervals possible for the reference seq length
			//AND fills in unused interval virtualFileOffsets with the
			//previous value.
			VirtualFileOffset prev = new VirtualFileOffset(0);
			for(int i=0; i<intervals.length; i++){
				if(intervals[i] ==null){
					intervals[i] = prev;
				}else{
					prev = intervals[i];
				}
			}
			numberOfUnAlignedReads = Long.valueOf(builder.numberOfUnalignedReads);
			numberOfAlignedReads = Long.valueOf(builder.numberOfAlignedReads);
			lowestOffset = builder.lowestStart;
			highestOffset = builder.higestEnd;
		}
		
		
		
		public Long getNumberOfUnAlignedReads() {
			return numberOfUnAlignedReads;
		}



		public Long getNumberOfAlignedReads() {
			return numberOfAlignedReads;
		}


		@Override
		public boolean hasMetaData() {
			return true;
		}



		@Override
		public int getNumberOfBins() {
			return bins.size();
		}



		@Override
		public VirtualFileOffset getLowestStartOffset() {
			return lowestOffset;
		}


		@Override
		public VirtualFileOffset getHighestEndOffset() {
			return highestOffset;
		}



		@Override
		public List<Bin> getBins() {
			return Collections.unmodifiableList(bins);
		}



		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((bins == null) ? 0 : bins.hashCode());
			result = prime * result + Arrays.hashCode(intervals);
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
			if (!(obj instanceof ReferenceIndex)) {
				return false;
			}
			ReferenceIndex other = (ReferenceIndex) obj;
			if (!bins.equals(other.getBins())) {
				return false;
			}
			if (!Arrays.equals(intervals, other.getIntervals())) {
				return false;
			}
			return true;
		}



		@Override
		public VirtualFileOffset[] getIntervals() {
			return Arrays.copyOf(intervals, intervals.length);
		}



		@Override
		public String toString() {
			return "ReferenceIndexImpl [bins=" + bins + ", intervals="
					+ Arrays.toString(intervals) + "]";
		}
		
		
	}
	
	

	

	
}
