/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.JillionUtil;

final class ConsedConsensusQualityComputer {

	//The algorithm that consed uses was explained to dkatzel
	//by David Gordon, the author of Consed, via several phone calls and emails 
	//in March 2013
	//
	//Here is the basic algorithm:
	//
	//For each consensus position, look at all underlying reads that match
	//and sum the highest forward quality and the highest reverse quality
	//if there is extra coverage (even if only in one dir) then add an additional 5 qv.
	//max value allowed is 90.
	//
	//To filter which reads are considered, Consed uses a flanking window of 2bp on each side
	//the entire window must match the consensus in order for the read to be considered at 
	//the consensus position.
	//
	//below are some more details from emails from David Gordon:
	//
	//OK, I looked at the code for computing the consensus qualities. 
	//It only uses reads that agree with the consensus in a window about the base in question:
	//
	//    ...CCBCC...    consensus
	//    ...ccbcc...    read
	//
	// so in a column, if any of ccbcc disagrees with CCBCC,
	// this read is not used for the purpose of calculating the consensus quality of B.
	//
	// at the ends of contigs, then the window is one-sided.  For
    // example, at the left end the window looks like this:
	//
    //    BCC...
    //    bcc...
	//
    //  (even if the read extends further to the left).
	//
	// And all of the bases must not be pads.  So if there is a column of
	// pads, the window is larger:
	//
	//      ...C*CCBCC...
	//       ...c*ccbcc...
	//
	// If this window isn't completely contained within the read's aligned
	// region, then this read isn't used.
	//
	// The whole window business is to not allow mis-aligned reads to be used
	// in the calculation.
	//
	// Requiring the window decreases the chance that the read is misaligned
	// (at this location).  When you look at it this way, then these rules
	// make sense.
	//
	//
	// The +5 is used only once--not once for each strand.
	//
	// There is also an issue of library duplicates. 
	// If 2 reads have the same starting location, they are suspected
	// of being library duplicates and thus are not allowed to 
	// confirm each other so no +5 boost is given.  
	// For example, suppose you have 50 reads all top strand,
	// but they all start at the same location, 
	// then there is no +5 of the quality because it is likely 
	// they were all PCR'd from the same piece of DNA.
	//
	// Quality values are not allowed to be greater than 90--I just cut it off there. 
	// (Quality 98 and 99 have special meanings.)
	
	/**
	 * {@value}  = Max value that consed qualities are allowed to have,
	 * any qualities values that are greater have special meanings.
	 */
	private static final int MAX_CONSED_COMPUTED_QUALITY = 90;
	/**
	 * Bonus quality amount added to final consensus quality
	 * if there are multiple reads that agree with the 
	 * consensus in a single direction that don't
	 * start at the same position.
	 */
	private static final int BONUS_VALUE = 5;
	
	private static final int NUMBER_OF_NON_GAPS_IN_WINDOW =2;
	
	/**
     * Compute the consensus quality sequence as computed by the same algorithm consed uses.
     * @param contig the contig to compute the consensus qualities for; can not be null.
     * @return a {@link QualitySequence} can not be null.
     * @throws DataStoreException  if there is a problem fetching read quality data
     * @throws NullPointerException if contig is null.
     */
	public static QualitySequence computeConsensusQualities(Contig<? extends AssembledRead> contig, QualitySequenceDataStore readQualities) throws DataStoreException{
		if(contig ==null){
    		throw new NullPointerException("contig can not be null");
    	}
		if(readQualities ==null){
			throw new NullPointerException("read quality datastore can not be null");
		}
		return computeConsensusQualities(contig.getConsensusSequence(), contig.getReadIterator(), readQualities);
	}
	
	/**
     * Compute the consensus quality sequence as computed by the same algorithm consed uses.
     * @param consensusSequence the contig consensus sequence to compute the consensus qualities for; can not be null.
     * @return a {@link QualitySequence} can not be null.
     * @throws DataStoreException  if there is a problem fetching read quality data
     * @throws NullPointerException if contig consensus seuquence is null.
     */
	public static QualitySequence computeConsensusQualities(NucleotideSequence consensusSequence, Iterable<? extends AssembledRead> reads, QualitySequenceDataStore readQualities) throws DataStoreException{
		if(consensusSequence ==null){
    		throw new NullPointerException("consensus can not be null");
    	}
		if(readQualities ==null){
			throw new NullPointerException("read quality datastore can not be null");
		}
		return computeConsensusQualities(consensusSequence, IteratorUtil.createStreamingIterator(reads.iterator()), readQualities);
	}
	/**
     * Compute the consensus quality sequence as computed by the same algorithm consed uses.
     * @param contig the contig to compute the consensus qualities for; can not be null.
     * @return a {@link QualitySequence} can not be null.
     * @throws DataStoreException 
     * @throws NullPointerException if contig is null.
     */
    private static QualitySequence computeConsensusQualities(NucleotideSequence consensusSequence, 
    		StreamingIterator<? extends AssembledRead> iter,
    		QualitySequenceDataStore readQualities) throws DataStoreException{
    	
    	
    	try{
	    	int[] consensusGapsArray = toIntArray(consensusSequence.getGapOffsets());
	    	
	    	int consensusLength = (int)consensusSequence.getLength();
			List<List<QualityPosition>> forwardQualitiesTowardsConsensus = new ArrayList<List<QualityPosition>>((int)consensusSequence.getLength());
			List<List<QualityPosition>> reverseQualitiesTowardsConsensus = new ArrayList<List<QualityPosition>>((int)consensusSequence.getLength());
	    	
			for(int i=0; i< consensusLength; i++){
	    		forwardQualitiesTowardsConsensus.add(new ArrayList<QualityPosition>());
	    		reverseQualitiesTowardsConsensus.add(new ArrayList<QualityPosition>());
	    	}
		
		
	    	while(iter.hasNext()){
	    		AssembledRead read = iter.next();
	    		long start =read.getGappedStartOffset();
	    		
	    		int[] differenceArray = toIntArray( read.getNucleotideSequence().getDifferenceMap().keySet());	    		
	    		int[] readGaps = toIntArray(read.getNucleotideSequence().getGapOffsets());
	    		Range validRange = read.getReadInfo().getValidRange();
	    		Direction dir = read.getDirection();
	    		if(dir ==Direction.REVERSE){
	    			validRange = AssemblyUtil.reverseComplementValidRange(validRange, read.getReadInfo().getUngappedFullLength());
	    		}
	    		QualitySequence validQualities = AssemblyUtil.getUngappedComplementedValidRangeQualities(read, readQualities.get(read.getId()));

	    		Iterator<PhredQuality> qualIter = validQualities.iterator();
	    		int i=0;
	    		while(qualIter.hasNext()){
	    			if(notAGap(readGaps, i)){
		    			PhredQuality qual =qualIter.next();
		    			int consensusOffset = (int)(i+start);
		    			if(notAGap(consensusGapsArray, consensusOffset) 
		    					&& readMatchesWindow(consensusGapsArray, consensusLength, read, start, differenceArray, i)){
		    				addQualityToConsensusConsideratino(
									forwardQualitiesTowardsConsensus,
									reverseQualitiesTowardsConsensus, start,
									dir, qual, consensusOffset);			    			
		    			}
	    			}
	    			i++;
	    		}
	    	}
	    	//we've now looked through all the reads
	    	QualitySequenceBuilder consensusQualitiesBuilder = new QualitySequenceBuilder(consensusLength);
	    	for(int i=0; i< consensusLength; i++){
	    		consensusQualitiesBuilder.append(
	    				computeConsensusQuality( forwardQualitiesTowardsConsensus,reverseQualitiesTowardsConsensus, i));
	    	}
	    	removeConsensusGaps(consensusQualitiesBuilder,consensusGapsArray);
	    	return consensusQualitiesBuilder.build();
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
	private static void addQualityToConsensusConsideratino(
			List<List<QualityPosition>> forwardQualitiesTowardsConsensus,
			List<List<QualityPosition>> reverseQualitiesTowardsConsensus,
			long start, Direction dir, PhredQuality qual, int consensusOffset) {
		QualityPosition position = new QualityPosition(qual, start);
		if(dir==Direction.FORWARD){
			forwardQualitiesTowardsConsensus.get(consensusOffset).add(position);
		}else{
			reverseQualitiesTowardsConsensus.get(consensusOffset).add(position);
		}
	}
	private static void removeConsensusGaps(
			QualitySequenceBuilder consensusQualitiesBuilder,
			int[] consensusGapsArray) {
		//iterate backwards to preserve offset order
		for(int i=consensusGapsArray.length-1; i>=0; i--){
			consensusQualitiesBuilder.delete(Range.of(consensusGapsArray[i]));
		}
		
	}
	
	private static boolean notDifferentThan(int[] differenceArray, int offset){
		return notAGap(differenceArray, offset);
	}
	private static boolean notAGap(int[] consensusGapsArray,
			int consensusOffset) {
		return Arrays.binarySearch(consensusGapsArray, consensusOffset)<0;
	}
	private static int computeConsensusQuality(
			List<List<QualityPosition>> forwardQualitiesTowardsConsensus,
			List<List<QualityPosition>> reverseQualitiesTowardsConsensus, int i) {
		List<QualityPosition> forwards = forwardQualitiesTowardsConsensus.get(i);
		Collections.sort(forwards);	    		
		
		List<QualityPosition> reverses = reverseQualitiesTowardsConsensus.get(i);
		Collections.sort(reverses);
		
		byte highestForwardQuality = forwards.isEmpty()? 0 : forwards.get(forwards.size()-1).quality;
		byte highestReverseQuality = reverses.isEmpty()? 0 : reverses.get(reverses.size()-1).quality;
  	
		int sum = highestForwardQuality + highestReverseQuality;
		if(hasBonusCoverage(forwards) || hasBonusCoverage(reverses)){
			sum +=BONUS_VALUE;
		}
		return Math.min(sum, MAX_CONSED_COMPUTED_QUALITY);
	}
	public static boolean readMatchesWindow(int[] consensusGapsArray,
			int consensusLength, AssembledRead read, long start,
			int[] differenceArray, int i) {
		boolean windowMatches = notDifferentThan(differenceArray, i);
		int windowLeftSize = computeWindowLeft(consensusGapsArray, i+ start);
		
		for(int j= i-windowLeftSize; windowMatches && j>=0 && j<i; j++){
			windowMatches = notDifferentThan(differenceArray, j);
		}
		if(windowMatches){
			//short circuit so we don't do any extra computations
			int windowRightSize = computeWindowRight(consensusGapsArray, i+start, consensusLength);
			for(int j= i+1; windowMatches && j<=i+windowRightSize && j<read.getGappedLength(); j++){
				windowMatches = notDifferentThan(differenceArray, j);
			}
		}
		return windowMatches;
	}
    private static int[] toIntArray(Collection<Integer> ints){
    	int[] array = new int[ints.size()];
		Iterator<Integer> iter = ints.iterator();
		for(int i=0;  iter.hasNext(); i++){
			array[i]=iter.next().intValue();
		}
		return array;
    }
    private static int computeWindowLeft(int[] consensusGapsArray, long startPosition) {
		int numberOfBasesInWindow=0;
		int position = (int)startPosition-1;
		while(position >=0 && numberOfBasesInWindow < NUMBER_OF_NON_GAPS_IN_WINDOW){
			
			if(notAGap(consensusGapsArray, position)){
				//not a gap
				numberOfBasesInWindow++;
			}
			position--;
		}
		return numberOfBasesInWindow;
	}
    
    private static int computeWindowRight(int[] consensusGapsArray, long startPosition, int consensusLength) {
		int numberOfBasesInWindow=0;
		int position = (int)startPosition+1;
		int numberOfNonGapsInWindow=0;
		while(position <consensusLength && numberOfNonGapsInWindow < NUMBER_OF_NON_GAPS_IN_WINDOW){
			
			if(notAGap(consensusGapsArray, position)){
				//not a gap
				numberOfNonGapsInWindow++;				
			}
			numberOfBasesInWindow++;
			position++;
		}
		return numberOfBasesInWindow;
	}
    
    
	private static boolean hasBonusCoverage(List<QualityPosition> forwards) {
		if(forwards.isEmpty()){
			return false;
		}
		Iterator<QualityPosition> iter = forwards.iterator();
		long firstOffset = iter.next().startOffset;
		while(iter.hasNext()){
			long nextOffset = iter.next().startOffset;
			if(firstOffset !=nextOffset){
				return true;
			}
		}
		return false;
	}
	private static final class QualityPosition implements Comparable<QualityPosition>{
    	private final byte quality;
    	private final long startOffset;
    	
		public QualityPosition(PhredQuality quality, long startOffset) {
			this.quality = quality.getQualityScore();
			this.startOffset = startOffset;
		}

		@Override
		public int compareTo(QualityPosition other) {
			return JillionUtil.compare(quality, other.quality);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + quality;
			result = prime * result
					+ (int) (startOffset ^ (startOffset >>> 32));
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
			if (!(obj instanceof QualityPosition)) {
				return false;
			}
			QualityPosition other = (QualityPosition) obj;
			if (quality != other.quality) {
				return false;
			}
			if (startOffset != other.startOffset) {
				return false;
			}
			return true;
		}
    	
    }
}
