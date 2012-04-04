package org.jcvi.common.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     * This is the same as {@link #merge(List, int) merge(rangesToMerge,0)} 
     * @param rangesToMerge
     * @return a new list of merged Ranges.
     * @see #merge(List, int)
     */
    public static List<Range> merge(List<Range> rangesToMerge){
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
     * @throws IllegalArgumentException if clusterDistance <0.
     */
    public static List<Range> merge(List<Range> rangesToMerge, int maxDistanceBetweenAdjacentRanges){
        if(maxDistanceBetweenAdjacentRanges <0){
            throw new IllegalArgumentException("cluster distance can not be negative");
        }
        List<Range> sortedCopy = new ArrayList<Range>(rangesToMerge);
        Collections.sort(sortedCopy);

        mergeAnyRangesThatCanBeCombined(sortedCopy, maxDistanceBetweenAdjacentRanges);
        return sortedCopy;
    }
    /**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * For example 2 ranges [0-2] and [1-4] could be merged into a single
     * range [0-4].
     * @param rangesToMerge the ranges to be merged together.
     * @param clusterDistance the maximum distance between the end of one range
     * and the start of another in order
     * to be merged.
     * @return a new list of merged Ranges.
     * @throws IllegalArgumentException if clusterDistance <0.
     */
    public static List<Range> mergeIntoClusters(List<Range> rangesToMerge, int maxClusterDistance){
        List<Range> tempRanges = merge(rangesToMerge);
        return privateMergeRangesIntoClusters(tempRanges,maxClusterDistance);

    }
    private static List<Range> privateMergeRangesIntoClusters(List<Range> rangesToMerge, int maxClusterDistance){
        if(maxClusterDistance <0){
            throw new IllegalArgumentException("max cluster distance can not be negative");
        }
        List<Range> sortedSplitCopy = new ArrayList<Range>();
        for(Range range : rangesToMerge){
            sortedSplitCopy.addAll(range.split(maxClusterDistance));
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
                final Range combinedRange = Range.buildInclusiveRange(range,nextRange);
                if(combinedRange.size()<= maxClusterDistance){
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
                Range clusteredRange = Range.buildRange(range.getStart()-clusterDistance, range.getEnd()+clusterDistance);
                Range nextRange = rangesToMerge.get(i+1);
                if(clusteredRange.intersects(nextRange) || clusteredRange.shiftRight(1).intersects(nextRange)){
                    replaceWithCombined(rangesToMerge,range, nextRange);
                    merged= true;
                    break;
                }
            }
        }while(merged);
    }
    private static void replaceWithCombined(List<Range> rangeList, Range range, Range nextRange) {
        final Range combinedRange = Range.buildInclusiveRange(range,nextRange);
        int index =rangeList.indexOf(range);
        rangeList.remove(range);
        rangeList.remove(nextRange);
        rangeList.add(index, combinedRange);
        
    }
}
