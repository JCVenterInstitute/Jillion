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
/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.List;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;

/**
 * A {@code CoverageMap} is an Object that contains coverage information
 * for a contiguous range of offset values. Coverage is defined
 * as the number of elements that are span a given offset.
 * The coverage at each offset
 * may be different since a different number of objects may 
 * span different locations.  Since adjacent offsets often 
 * have the same coverage information, contiguous regions that contain the same exact
 * elements (and therefore the same exact coverage) are combined into 
 * {@link CoverageRegion} objects. Adjacent {@link CoverageRegion}s may
 * have the same depth of coverage but will not contain the same exact
 * elements.
 * <p>
 * For example, a CoverageMap of a  {@link org.jcvi.jillion.assembly.Contig} 
 * will show where each of its {@link org.jcvi.jillion.assembly.AssembledRead}s align
 * to the contig consensus. Each consensus offset will have a different level of coverage
 * because each consensus offset will have a different number of reads aligned to it.
 * @author dkatzel
 *
 * @param <T> The Type of element in the coverage map.
 */
public interface CoverageMap <T extends Rangeable> extends Iterable<CoverageRegion<T>>{
	/**
	 * Get the number of {@link CoverageRegion}s.
	 * @return the number of regions will always be 
	 * &ge; 0.
	 */
    int getNumberOfRegions();
    /**
     * Get the ith {@link CoverageRegion}.
     * @param i the index into this coverage map;
     * where 0 &le; i &lt; {@link #getNumberOfRegions()} 
     * @return the ith {@link CoverageRegion} will never be null
     * but may have 0 depth of coverage. 
     * @throws IndexOutOfBoundsException if i &lt; 0 or i &ge; {@link #getNumberOfRegions()}.
     */
    CoverageRegion<T> getRegion(int i);
    /**
     * Does this CoverageMap have any CoverageRegions.
     * @return {@code true} if {@link #getNumberOfRegions()} &lt; 0;
     * {@code false} otherwise.
     */
    boolean isEmpty();
    /**
     * Get the {@link CoverageMapStats} for this coverage map.
     * Some implementations may cache these values so it is more
     * efficient to call this method then manually compute them everytime
     * yourself.
     * 
     * @return the {@link CoverageMapStats} for this map; will never be null.
     */
    CoverageMapStats getStats();
    
    /**
     * Get the average coverage depth at each offset
     * in the coverage map. This is the same as (but may be more
     * efficient than):
     * <pre>
     * {@code
     	long totalLength = 0L;
     	long totalCoverage =0L;
     	for(CoverageRegion<?> region : this){
        	long rangeLength =region.asRange().getLength();
        	totalLength += rangeLength;
        	totalCoverage += region.getCoverageDepth() * rangeLength;
        }
        avgCoverage = totalLength==0? 0D : totalCoverage/(double)totalLength;
        }
        </pre>
     * @return the average coverage depth will always be 
     * &ge; 0.
     */
    double getAverageCoverage();
    /**
     * Get the lowest coverage depth of 
     * any {@link CoverageRegion}
     * in the coverage map.
     * 
     * @return the lowest coverage depth.
     */
    int getMinCoverage();
    /**
     * Get the highest coverage depth of 
     * any {@link CoverageRegion}
     * in the coverage map.
     * 
     * @return the highest coverage depth.
     */
    int getMaxCoverage();
    /**
     * Get a List of all the {@link CoverageRegion}s in this CoverageMap
     * that intersect the given {@link Range}.
     * 
     * @param range the Range to get all the coverageRegions for.
     * 
     * @return A List of CoverageRegions; if the coverage map does not contain
     * any CoverageRegions that intersect the given range, then the returned
     * List will be empty.  Will never return null.  It is possible that
     * CoverageRegions returned may have 0 depth of coverage.
     * 
     * @throws NullPointerException if range is null.
     */
    List<CoverageRegion<T>> getRegionsWhichIntersect(Range range);
    /**
     * Get the {@link CoverageRegion} that provides coverage
     * for the given offset (in 0-based).  If this coverage map
     * includes the given offset, then the returned {@link CoverageRegion}'s
     * {@link Range} will intersect the given offset.
     * @param offset the offset to 
     * @return a CoverageRegion if this CoverageMap contains this offset;
     * or {@code null} if this coverage map does not contain this offset. 
     * It is possible for the returned CoverageRegion to have 0 depth of coverage.
     */
    CoverageRegion<T> getRegionWhichCovers(long offset);
    /**
     * Create a {@link Stream} of all the {@link CoverageRegion}s
     * in this Coverage Map.
     * @return a new Stream.
     * @since 5.0
     */
    Stream<CoverageRegion<T>> regions();
    /**
     * Create a {@link Stream} of all the {@link CoverageRegion}s
     * in this Coverage Map that intersect the given Range coordinates.
     * This should return an equivalent Stream as
     * <pre>
     * {@code getRegionsWhichIntersect(range).stream()}
     * </pre>
     * but might be more efficient.
     * @param range the Range to get all the coverageRegions for.
     * @return a new Stream
     * @throws NullPointerException if range is null.
     * @since 5.0
     */
    Stream<CoverageRegion<T>> regions(Range range);
}
