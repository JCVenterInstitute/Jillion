package org.jcvi.jillion.sam.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.sam.VirtualFileOffset;

class BaiRefIndex implements ReferenceIndex {
	private final List<Bin> bins;
	private VirtualFileOffset[] intervals;
	private Long unalignedCount, alignedCount;
	private VirtualFileOffset lowestStart, highestEnd;
	
	public BaiRefIndex(Bin[] bins, VirtualFileOffset[] intervals) {
		this.intervals = intervals;
		
		List<Bin> binList = new ArrayList<Bin>(bins.length);
		for(Bin b : bins){
			if(b!=null){
				binList.add(b);
			}
		}
		//sam tools doesn't sort the bins!
		//so sort it just in case
		Collections.sort(binList, IndexUtil.BinSorter.INSTANCE);
		
		this.bins = Collections.unmodifiableList(binList);
		
	}

	@Override
	public List<Bin> getBins() {
		return bins;
	}

	@Override
	public int getNumberOfBins() {
		return bins.size();
	}

	@Override
	public boolean hasMetaData() {
		return lowestStart !=null && highestEnd !=null
				&& alignedCount !=null && unalignedCount !=null;
	}

	@Override
	public VirtualFileOffset getLowestStartOffset() {
		return lowestStart;
	}

	public void setLowestStartOffset(VirtualFileOffset lowestStart) {
		this.lowestStart = lowestStart;
	}
	@Override
	public VirtualFileOffset getHighestEndOffset() {
		return highestEnd;
	}

	public void setHighestEndOffset(VirtualFileOffset highestEnd) {
		this.highestEnd = highestEnd;
	}

	public void setUnalignedCount(Long unalignedCount) {
		this.unalignedCount = unalignedCount;
	}

	public void setAlignedCount(Long alignedCount) {
		this.alignedCount = alignedCount;
	}

	@Override
	public Long getNumberOfUnAlignedReads() {
		return unalignedCount;
	}

	@Override
	public Long getNumberOfAlignedReads() {
		return alignedCount;
	}

	@Override
	public VirtualFileOffset[] getIntervals() {
		return Arrays.copyOf(intervals, intervals.length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bins.hashCode();
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
	public String toString() {
		return "BaiRefIndex [bins=" + bins + ", intervals="
				+ Arrays.toString(intervals) + "]";
	}

	
}
