/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.sam.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.index.Bin;
import org.jcvi.jillion.sam.index.Chunk;
import org.jcvi.jillion.sam.index.ReferenceIndex;

public final class ReferenceIndexBuilder{
	
	private final VirtualFileOffset[] intervals;

	private int largestIndexUsed=-1;
	//need to keep type as ArrayList since
	//we use trimToSize() method which is only on arraylist.
	@SuppressWarnings("PMD.LooseCoupling")
	private ArrayList<Bin> bins;
	private long numberOfUnalignedReads=0, numberOfAlignedReads=0;
	private VirtualFileOffset lowestStart = new VirtualFileOffset(Long.MAX_VALUE);
	private VirtualFileOffset higestEnd = new VirtualFileOffset(0L);
	
	private BinBuilder[] binBuilders;
	
	public ReferenceIndexBuilder(int length){
		int arraySize = IndexUtil.getIntervalOffsetFor(length-1);
		//size of array is largest possible bin of this length + 1 to get the number of those elements
		//we need to keep an array of all the bin builders because
		//bin numbers are not monotonically increasing.
		//we could have reads that span 2 of the samller bins
		//which gives it a smaller bin number so we will
		//frequently jump around bins
		binBuilders = new BinBuilder[ SamUtil.computeBinFor(length-1, length) +1];
		this.intervals = new VirtualFileOffset[arraySize+1];
		
	}
	
	public void addAlignment(int readStartOffset, int readEndOffsetExclusive, VirtualFileOffset start, VirtualFileOffset end){
		
		updateIntervals(readStartOffset, readEndOffsetExclusive-1, start, end);		
		updateBins(readStartOffset, readEndOffsetExclusive, start, end);
	}
	
	public void incrementUnmappedCount() {
		numberOfUnalignedReads++;
	}
	
	public ReferenceIndex build(){
		//only include the bins that actually had alignments
		//in the built index
		bins = new ArrayList<Bin>(binBuilders.length);
		for(int i=0; i< binBuilders.length; i++){
			if(binBuilders[i] !=null){
				bins.add(binBuilders[i].build());
			}
		}		
		bins.trimToSize();		
		return new ReferenceIndexImpl(this);
	}
	
	private void updateBins(int readStartOffset, int readEndOffsetExclusive,
			VirtualFileOffset start, VirtualFileOffset end) {
		int bin = SamUtil.computeBinFor(readStartOffset, readEndOffsetExclusive);
		
		BinBuilder binBuilder;
		if(binBuilders[bin] ==null){
			//make new one
			binBuilder = new BinBuilder(bin);
			binBuilders[bin] = binBuilder;
		}else{
			binBuilder = binBuilders[bin];
		}
		
		binBuilder.addChunk(new Chunk(start, end));
		numberOfAlignedReads++;
	}

	public void updateIntervals(int readStartOffset, int readEndOffset, VirtualFileOffset start, VirtualFileOffset end) {
		int startInterval = IndexUtil.getIntervalOffsetFor(readStartOffset);
		int endInterval = IndexUtil.getIntervalOffsetFor(readEndOffset);
		
		if(endInterval > largestIndexUsed){
			largestIndexUsed = endInterval;
		}
		for(int i = startInterval; i<=endInterval; i++){
			try{
			VirtualFileOffset currentValue = intervals[i];
			if(currentValue ==null || start.compareTo(currentValue) < 0){
				intervals[i] = start;
			}
			}catch(ArrayIndexOutOfBoundsException e){
				throw e;
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
