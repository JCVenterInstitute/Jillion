package org.jcvi.jillion.sam.index;

import java.util.List;

import org.jcvi.jillion.sam.VirtualFileOffset;

/**
 * {@code ReferenceIndex} is an object
 * representations of a BAM index file's
 * list of bins and linear intervals
 * used for indexing.
 * 
 * @author dkatzel
 *
 */
public interface ReferenceIndex {
	/**
	 * Get an unmodifiable List of {@link Bin}s
	 * in this reference.
	 * @return a List of Bins, will never
	 * be null but may be empty.
	 */
	List<Bin> getBins();
	/**
	 * Get (defensive copy of) the array of intervals for this
	 * index.  
	 * @return an array of {@link VirtualFileOffset}s,
	 * which will never be empty, although
	 * there might be null cells in the array.
	 */
	VirtualFileOffset[] getIntervals();
	
	boolean hasMetaData();
	
	Long getNumberOfUnAlignedReads();

	Long getNumberOfAlignedReads();
	
	VirtualFileOffset getLowestStartOffset();



	VirtualFileOffset getHighestEndOffset();
	int getNumberOfBins();

}