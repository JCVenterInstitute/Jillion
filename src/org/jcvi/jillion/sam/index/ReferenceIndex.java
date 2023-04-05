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
package org.jcvi.jillion.sam.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.sam.SamUtil;
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
	/**
	 * Does this ReferenceIndex contain
	 * optional metadata?
	 * Some implementations of BAM indexes include 
	 * metadata that is not in the BAM index specification.
	 * 
	 * @return {@code true} if {@link #getNumberOfAlignedReads()},
	 * {@link #getNumberOfUnAlignedReads()}, {@link #getLowestStartOffset()}
	 * and {@link #getHighestEndOffset()} all return non-null values;
	 * {@code false} otherwise.
	 */
	boolean hasMetaData();
	/**
	 * Get the optional metadata field which contains the number
	 * of unaligned reads in this reference.
	 * @return {@code null} if this reference does not
	 * have metadata; otherwise a long.
	 * @see #hasMetaData()
	 */
	Long getNumberOfUnAlignedReads();
	/**
	 * Get the optional metadata field which contains the number
	 * of aligned reads in this reference.
	 * @return {@code null} if this reference does not
	 * have metadata; otherwise a long.
	 * @see #hasMetaData()
	 */
	Long getNumberOfAlignedReads();
	/**
	 * Get the optional metadata field which contains the lowest begin
	 * {@link VirtualFileOffset} that references this reference.
	 * @return {@code null} if this reference does not
	 * have metadata; otherwise a {@link VirtualFileOffset}.
	 * @see #hasMetaData()
	 */
	VirtualFileOffset getLowestStartOffset();
	/**
	 * Get the optional metadata field which contains the highest end
	 * {@link VirtualFileOffset} that references this reference.
	 * @return {@code null} if this reference does not
	 * have metadata; otherwise a {@link VirtualFileOffset}.
	 * @see #hasMetaData()
	 */
	VirtualFileOffset getHighestEndOffset();
	/**
	 * Get the number of {@link Bin}s in this reference.
	 * This is the same as {@code getBins().size()}
	 * @return an int will always be >=0.
	 */
	int getNumberOfBins();
	/**
	 * Two ReferenceIndexes are equal
	 * if they have the same {@link Bin}s
	 * and the same intervals.
	 * <br/>
	 * <strong>Note</strong> Since the metadata
	 * fields are optional, they do not count 
	 * towards equality checks.
	 * {@inheritDoc}
	 */
	@Override
	boolean equals(Object obj);
	/**
	 * Find all {@link Bin}s that overlap with the given alignment Range and pass them
	 * to the given consumer.
	 * @param range the range to look for; can not be null.
	 * @param consumer the consumer of Bins found; can not be null.
	 * 
	 * @throws NullPointerException if any parameter is null.
	 * @since 6.0
	 * @see #findBinsForAlignmentRange(List, BiConsumer)
	 */
	default void findBinsForAlignmentRange(Range range, Consumer<Bin> consumer) {
		//range NPE is checked by overlapping bins method
		Objects.requireNonNull(consumer);
		int[] candidateBins = SamUtil.getCandidateOverlappingBins(range);
		for(Bin bin: getBins()) {
			if(Arrays.binarySearch(candidateBins, bin.getBinNumber()) >=0) {
				consumer.accept(bin);
			}
		}
	}
	/**
	 * Find all {@link Bin}s that overlap with the given alignment Ranges
	 * and pass them to the given consumer.  It is possible that the same Bin
	 * will be found for multiple ranges.
	 * @param range the list ranges to look for; can not be null or contain any null ranges.
	 * @param consumer the consumer of Bins found and the corresponding Range for that bin; can not be null.  
	 * 
	 * @throws NullPointerException if any parameter is null.
	 * @since 6.0
	 */
	default void findBinsForAlignmentRange(List<Range> ranges, BiConsumer<Range, Bin> consumer) {
		//range NPE is checked by overlapping bins method
		Objects.requireNonNull(consumer);
		Map<Integer, List<Range>> rangesByBins = new HashMap<Integer, List<Range>>();
		for(Range r : ranges) {
			int[] candidateBins = SamUtil.getCandidateOverlappingBins(r);
			for(int i=0; i< candidateBins.length; i++) {
				rangesByBins.computeIfAbsent(candidateBins[i], k-> new ArrayList<>()).add(r);
			}
		}
		
		for(Bin bin: getBins()) {
			List<Range> mappedRanges = rangesByBins.get(bin.getBinNumber());
			if(mappedRanges !=null) {
				for(Range r : mappedRanges) {
					consumer.accept(r, bin);
				}
			}
		}
	}
}
