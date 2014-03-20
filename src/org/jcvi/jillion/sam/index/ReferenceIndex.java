package org.jcvi.jillion.sam.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.sam.SamUtil;
import org.jcvi.jillion.sam.VirtualFileOffset;

public final class ReferenceIndex {

	private final List<Bin> bins;
	private final VirtualFileOffset[] intervals;
	
	
	private ReferenceIndex(Builder builder){
		this.bins = builder.bins;
		this.intervals = builder.intervals;
	}
	
	
	
	public List<Bin> getBins() {
		return bins;
	}



	public VirtualFileOffset[] getIntervals() {
		return Arrays.copyOf(intervals, intervals.length);
	}



	public static final class Builder{
		private final VirtualFileOffset[] intervals;
		private int currentIntervalArrayOffset=-1;
		//need to keep type as ArrayList since
		//we use trimToSize() method which is only on arraylist.
		@SuppressWarnings("PMD.LooseCoupling")
		private final ArrayList<Bin> bins = new ArrayList<Bin>();
		private int currentBinNumber =1;
		private Bin.Builder currentBinBuilder = null;
		
		public Builder(int length){
			this.intervals = new VirtualFileOffset[IndexUtil.getIntervalOffsetFor(length)];
			
		}
		
		public void addAlignment(int readStartOffset, int readEndOffsetExclusive, VirtualFileOffset start, VirtualFileOffset end){
			
			updateIntervals(readStartOffset, start);		
			updateBins(readStartOffset, readEndOffsetExclusive, start, end);
		}
		
		public ReferenceIndex build(){
			if(currentBinBuilder !=null){
				//add last bin to
				//our list of bins used.
				bins.add(currentBinBuilder.build());
			}
			bins.trimToSize();
			//sort bins in order
			Collections.sort(bins);
			return new ReferenceIndex(this);
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
				currentBinBuilder = new Bin.Builder(bin);
				//update bin number
				currentBinNumber = bin;
			}
			//assume that the alignments are in sorted order
			//so we will only see bins that are >= current bin
			//so if bin isn't greater than the current bin number
			//than we must be in the same bin.
			currentBinBuilder.addChunk(new Chunk(start, end));
			
		}

		public void updateIntervals(int readStartOffset, VirtualFileOffset start) {
			int interval = IndexUtil.getIntervalOffsetFor(readStartOffset) -1;
			if(interval > currentIntervalArrayOffset){
				intervals[interval] = start;
				currentIntervalArrayOffset = interval;
			}
		}
	}
}
