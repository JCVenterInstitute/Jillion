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
package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.internal.core.util.GrowableLongArray;
/**
 * {@code Ranges} is a helper class
 * for operating on a collection
 * of Range objects.
 * @author dkatzel
 *
 */
public final class Ranges {
	//private constructor.
	private Ranges(){}
	/**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * This is the same as {@link #merge(Collection, int) merge(rangesToMerge,0)} 
     * @param rangesToMerge the collection of Ranges to merge;
     * 
     * @return a new list of merged Ranges.
     * @see #merge(Collection, int)
     */
    public static List<Range> merge(Collection<? extends Rangeable> rangesToMerge){
        return merge(rangesToMerge,0);
    }
    /**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * For example 2 ranges [0-2] and [1-4] could be merged into a single
     * range [0-4].
     * @param rangesToMerge the ranges to be merged together.
     * @param maxDistanceBetweenAdjacentRanges the maximum distance between the end of one range
     * and the start of another in order
     * to be merged.
     * @return a new list of merged Ranges.
     * @throws IllegalArgumentException if maxDistanceBetweenAdjacentRanges &lt; 0.
     */
    public static List<Range> merge(Collection<? extends Rangeable> rangesToMerge, int maxDistanceBetweenAdjacentRanges){
        if(maxDistanceBetweenAdjacentRanges <0){
            throw new IllegalArgumentException("cluster distance can not be negative");
        }
        List<Range> sortedCopy = rangesToMerge.stream()
        								.map(Rangeable::asRange)
        								.sorted(Range.Comparators.ARRIVAL)
        								.collect(Collectors.toList());
       

        mergeAnyRangesThatCanBeCombined(sortedCopy, maxDistanceBetweenAdjacentRanges);
        return sortedCopy;
    }
    
    /**
     * Convert all the set bits in the given BitSet into contiguous ranges.
     * For example if there are set bits at offsets 0,1,2 and 4,5 then 
     * the Ranges [0-2] and [4-5] will be returned.
     * 
     * @apiNote This is the same as {@link #asRanges(BitSet, int) asRanges(bits, 0)}.
     * 
     * @param bits the {@link BitSet} to convert into Ranges; can not be null but may have no bits set.
     * 
     * @return a new list of Ranges; will never be null but may be empty.
     * 
     * @throws NullPointerException if bits is null
     * 
     * @see #asRanges(BitSet, int)
     * @since 5.3
     */
    public static List<Range> asRanges(BitSet bits){
        return asRanges(bits, 0);
    }
    /**
     * Convert all the set bits in the given BitSet into contiguous ranges.
     * For example if there are set bits at offsets 0,1,2 and 4,5 then:
     * <ol>
     * <li>if maxDistanceBetweenAdjacentRanges <2, then the Ranges [0-2] and [4-5] will be returned</li>
     * <li>if maxDistanceBetweenAdjacentRanges >=2 then one Range [0-5] is returned</li>
     * </ol>
     * 
     * @param bits the {@link BitSet} to convert into Ranges; can not be null but may have no bits set.
     * @param maxDistance the maximum distance between the end of one range
     * and the start of another in order to be merged.
     * 
     * @return a new list of Ranges; will never be null but may be empty.
     * 
     * @throws IllegalArgumentException if maxDistance &lt; 0.
     * @throws NullPointerException if bits is null
     * 
     * @since 5.3
     */
    public static List<Range> asRanges(BitSet bits, int maxDistance){
        if(maxDistance < 0){
            throw new IllegalArgumentException("maxDistance can not be negative: " + maxDistance);
        }
        if(bits.isEmpty()){
            return Collections.emptyList();
        }
        int i = bits.nextSetBit(0);
        Range.Builder currentBuilder = new Range.Builder(i,i);
        
        List<Range> ret = new ArrayList<>();
        for (; i >= 0; i = bits.nextSetBit(i+1)) {
            // operate on index i here
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
            int delta = i - (int) currentBuilder.getEnd();
            if( delta -1 > maxDistance){
                ret.add(currentBuilder.build());
                currentBuilder = new Range.Builder(i,i);
            }else{
                currentBuilder.expandEnd(delta);
            }
        }
        ret.add(currentBuilder.build());
        return ret;
    }
    /**
     * Convert all offsets in the <strong>sorted</strong> array into contiguous ranges.
     * For example if the array is <tt> {0,1,2,4,5}</tt> then 
     * the Ranges [0-2] and [4-5] will be returned.
     * 
     * @apiNote This is the same as {@link #asRanges(int[], int) asRanges(sortedOffsets, 0)}.
     * 
     * @param sortedOffsets the array of SORTED values to convert into Ranges; can not be null but may be empty.
     * The array must be sorted from smallest to largest.
     * 
     * @return a new list of Ranges; will never be null but may be empty.
     * 
     * @throws NullPointerException if bits is null
     * @throws IllegalArgumentException if sortedOffsets is not sorted from smallest to largest.
     * 
     * @see #asRanges(int[], int)
     * @since 5.3
     */
    public static List<Range> asRanges(int[] sortedOffsets){
        return asRanges(sortedOffsets, 0);
    }
    /**
     * Convert all offsets in the <strong>sorted</strong> array into contiguous ranges.
     * For example if the array is <tt> {0,1,2,4,5}</tt> then:
     * <ol>
     * <li>if maxDistanceBetweenAdjacentRanges <2, then the Ranges [0-2] and [4-5] will be returned</li>
     * <li>if maxDistanceBetweenAdjacentRanges >=2 then one Range [0-5] is returned</li>
     * </ol>
     * 
     * @param sortedOffsets the array of SORTED values to convert into Ranges; can not be null but may be empty.
     * The array must be sorted from smallest to largest.
     * 
     * @param maxDistance the maximum distance between the end of one range
     * and the start of another in order to be merged.
     * 
     * @return a new list of Ranges; will never be null but may be empty.
     * 
     * @throws IllegalArgumentException if maxDistance &lt; 0.
     * @throws NullPointerException if array is null
     * @throws IllegalArgumentException if sortedOffsets is not sorted from smallest to largest.
     * @since 5.3
     */
    public static List<Range> asRanges(int[] sortedOffsets, int maxDistance){
        if(maxDistance < 0){
            throw new IllegalArgumentException("maxDistance can not be negative: " + maxDistance);
        }
        if(sortedOffsets.length ==0){
            return Collections.emptyList();
        }
        int lastOffset = sortedOffsets[0];
        Range.Builder currentBuilder = new Range.Builder(lastOffset,lastOffset);
        
        List<Range> ret = new ArrayList<>();
        for (int i=1; i<sortedOffsets.length; i++) {
            lastOffset = sortedOffsets[i];
            int delta = lastOffset - (int) currentBuilder.getEnd();
            if(delta <0){
                //not sorted!
                throw new IllegalArgumentException("input array must be sorted from smallest to largest");
            }
            if( delta -1 > maxDistance){
                ret.add(currentBuilder.build());
                currentBuilder = new Range.Builder(lastOffset,lastOffset);
            }else{
                currentBuilder.expandEnd(delta);
            }
        }
        ret.add(currentBuilder.build());
        return ret;
    }
    
    /**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * For example 2 ranges [0-2] and [1-4] could be merged into a single
     * range [0-4].
     * @param rangesToMerge the ranges to be merged together.
     * @param maxClusterDistance the maximum distance between the end of one range
     * and the start of another in order
     * to be merged.
     * @return a new list of merged Ranges.
     * @throws IllegalArgumentException if clusterDistance &lt; 0.
     */
    public static List<Range> mergeIntoClusters(Collection<? extends Rangeable> rangesToMerge, int maxClusterDistance){
        List<Range> tempRanges = merge(rangesToMerge);
        return privateMergeRangesIntoClusters(tempRanges,maxClusterDistance);

    }
    private static List<Range> privateMergeRangesIntoClusters(List<? extends Rangeable> rangesToMerge, int maxClusterDistance){
        if(maxClusterDistance <0){
            throw new IllegalArgumentException("max cluster distance can not be negative");
        }
        List<Range> sortedSplitCopy = new ArrayList<Range>();
        for(Rangeable range : rangesToMerge){
            sortedSplitCopy.addAll(range.asRange().split(maxClusterDistance));
        }        
        
        privateMergeAnyRangesThatCanBeClustered(sortedSplitCopy, maxClusterDistance);
        return sortedSplitCopy;
    }
    
    private static void privateMergeAnyRangesThatCanBeClustered(List<Range> rangesToMerge, int maxClusterDistance) {
        boolean merged;
        do{
            merged = false;
            for(int i=0; i<rangesToMerge.size()-1; i++){
                Range range = rangesToMerge.get(i);
                Range nextRange = rangesToMerge.get(i+1);
                final Range combinedRange = createInclusiveRange(range,nextRange);
                if(combinedRange.getLength()<= maxClusterDistance){
                    //can be combined
                    replaceWithCombined(rangesToMerge,range, nextRange);
                    merged= true;
                    break;
                }                
            }            
        }while(merged);
    }
    
    private static void mergeAnyRangesThatCanBeCombined(List<Range> rangesToMerge, int clusterDistance) {
        boolean merged;
        do{
            merged = false;
            for(int i=0; i<rangesToMerge.size()-1; i++){
                Range range = rangesToMerge.get(i);
                Range clusteredRange = Range.of(range.getBegin()-clusterDistance, range.getEnd()+clusterDistance);
                Range nextRange = rangesToMerge.get(i+1);
                if(clusteredRange.intersects(nextRange) || new Range.Builder(clusteredRange).shift(1).build().intersects(nextRange)){
                    replaceWithCombined(rangesToMerge,range, nextRange);
                    merged= true;
                    break;
                }
            }
        }while(merged);
    }
    private static void replaceWithCombined(List<Range> rangeList, Range range, Range nextRange) {
        final Range combinedRange = createInclusiveRange(range,nextRange);
        int index =rangeList.indexOf(range);
        rangeList.remove(range);
        rangeList.remove(nextRange);
        rangeList.add(index, combinedRange);
        
    }
    
    /**
     * Return a single
     * Range that covers the entire span
     * of the given Ranges.
     * <p>
     * For example: passing in 2 Ranges [0,10] and [20,30]
     * will return [0,30]
     * @param ranges a collection of ranges
     * @return a new Range that covers the entire span of
     * input ranges.
     */
    public static Range createInclusiveRange(Collection<Range> ranges){       
        return createInclusiveRange(ranges.iterator());
    }
    
    private static Range createInclusiveRange(Iterator<Range> iter){
        if(!iter.hasNext()){
            return new Range.Builder().build();
        }
        
        Range firstRange =iter.next();
        long currentLeft = firstRange.getBegin();
        long currentRight = firstRange.getEnd();
        while(iter.hasNext()){
            Range range = iter.next();
            if(range.getBegin() < currentLeft){
                currentLeft = range.getBegin();
            }
            if(range.getEnd() > currentRight){
                currentRight = range.getEnd();
            }
        }
        return Range.of(currentLeft, currentRight);
    }
    
    /**
     * Return a single
     * Range that covers the entire span
     * of the given Ranges.
     * <p>
     * For example: passing in 2 Ranges [0,10] and [20,30]
     * will return [0,30]
     * @param ranges a collection of ranges
     * @return a new Range that covers the entire span of
     * input ranges.
     */
    public static <T> Range createInclusiveRange(Collection<T> collection, Function<T, Range> mapper){
        return createInclusiveRange(IteratorUtil.map(collection.iterator(), mapper));
    }
    
    private static Range createInclusiveRange(Range... ranges){
    	return createInclusiveRange(Arrays.asList(ranges));
    }
    /**
     * Check if the target Range intersects any range in the given list.
     * 
     * @param ranges the list of Ranges to check; can not be null but may be empty.
     * @param target the intersection target Range.
     * 
     * @return {@code true} if the target intersects any Range; {@code false} otherwise.
     * 
     * @apiNote this is the same as:
     * <pre>
     * for(Range r : ranges){
     *     if(r.intersects(target)){
     *         return true;
     *     }
     * }
     * return false;
     * </pre>
     * @since 5.3
     * 
     * @throws NullPointerException if any parameter is null or any Range in the given list is null.
     */
    public static boolean intersects(Collection<Range> ranges, Range target) {
        for(Range r : ranges){
            if(r.intersects(target)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the target coordinate intersects any range in the given list.
     * 
     * @param ranges the list of Ranges to check; can not be null but may be empty.
     * @param targetCoordinate the coordinate ( 0-based) to check.
     * 
     * @return {@code true} if the target intersects any Range; {@code false} otherwise.
     * 
     * @apiNote this is the same as:
     * <pre>
     * for(Range r : ranges){
     *     if(r.intersects(targetCoordinate)){
     *         return true;
     *     }
     * }
     * return false;
     * </pre>
     * @since 6.0
     * 
     * @throws NullPointerException if any parameter is null or any Range in the given list is null.
     */
    public static boolean intersects(Collection<Range> ranges, long targetCoordinate) {
        for(Range r : ranges){
            if(r.intersects(targetCoordinate)){
                return true;
            }
        }
        return false;
    }
  
    
    /**
     * Check if the target Range intersects any range in the given list.
     * 
     * @param ts the collection of objects to check; can not be null but may be empty.
     * @param mapper the Mapper function to convert the type in the input collection into a Range.
     * if the mapper returns a null Range, then that element is skipped.
     * @param target the intersection target Range.
     * 
     * @return {@code true} if the target intersects any Range; {@code false} otherwise.
     * 
     * @since 5.3
     * 
     * @throws NullPointerException if any parameter is null or any Range in the given list is null.
     */
    public static <T> boolean intersects(Collection<T> ts, Function<T, Range> mapper, Range target) {
        for(T t : ts){
            Range r = mapper.apply(t);
            if(r ==null){
                continue;
            }
            if(r.intersects(target)){
                return true;
            }
        }
        return false;
    }
    /**
     * Return a list of ranges that intersect both groups of input ranges;
     * 
     * @param subjectRange a single Range
     * @param queryRanges
     * @return a List of Ranges that are the intersections of both lists.
     * @since 6.0
     */
	public static List<Range> union(Rangeable subjectRange, Collection<? extends Rangeable> queryRanges) {
		Objects.requireNonNull(subjectRange);
		Objects.requireNonNull(queryRanges);
		List<Range> intersections = new ArrayList<>();
		
		Range sRange = subjectRange.asRange();
		for(Rangeable q : queryRanges) {
			Range intersection = sRange.intersection(q.asRange());
			if(!intersection.isEmpty()) {
				intersections.add(intersection);
			}
		}
		
		if(intersections.isEmpty()) {
			return Collections.emptyList();
		}
		return Ranges.merge(intersections);
	}
    /**
     * Return a list of ranges that intersect both groups of input ranges;
     * 
     * @param subjectRanges
     * @param queryRanges
     * @return a List of Ranges that are the intersections of both lists.
     */
	public static List<Range> union(Collection<? extends Rangeable> subjectRanges, Collection<? extends Rangeable> queryRanges) {
		Objects.requireNonNull(subjectRanges);
		Objects.requireNonNull(queryRanges);
		
		List<Range> intersections = new ArrayList<>();
		for(Rangeable s : subjectRanges) {
			Range sRange = s.asRange();
			for(Rangeable q : queryRanges) {
				Range intersection = sRange.intersection(q.asRange());
				if(!intersection.isEmpty()) {
					intersections.add(intersection);
				}
			}
		}
		if(intersections.isEmpty()) {
			return Collections.emptyList();
		}
		return Ranges.merge(intersections);
	
	}
	/**
	 * Return the list of Ranges of everything that is in A but not in B.
	 * 
	 * @param a the list of ranges in A.
	 * @param b the list of ranges in B.
	 * @return
	 * @since 6.0
	 * @throws NullPointerException if a or b are {@code null}.
	 */
	public static List<Range> complement(Rangeable a, Collection<? extends Rangeable> b){
		List<Range> union = Ranges.union(a, b);
		List<Range> complement = new ArrayList<>();
		
		complement.addAll( a.asRange().complement(union));
		
		return Ranges.merge(complement);
	}
	/**
	 * Return the list of Ranges of everything that is in A but not in B.
	 * 
	 * @param a the list of ranges in A.
	 * @param b the list of ranges in B.
	 * @return
	 * @since 6.0
	 * @throws NullPointerException if a or b are {@code null}.
	 */
	public static List<Range> complement(Collection<? extends Rangeable> a, Collection<? extends Rangeable> b){
		List<Range> union = Ranges.union(a, b);
		List<Range> complement = new ArrayList<>();
		for(Rangeable s : a) {
			complement.addAll( s.asRange().complement(union));
		}
		return Ranges.merge(complement);
	}
}
