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
package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
     * This is the same as {@link #merge(Collection, int) merge(rangesToMerge,0)} 
     * @param rangesToMerge
     * @return a new list of merged Ranges.
     * @see #merge(Collection, int)
     */
    public static List<Range> merge(Collection<Range> rangesToMerge){
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
    public static List<Range> merge(Collection<Range> rangesToMerge, int maxDistanceBetweenAdjacentRanges){
        if(maxDistanceBetweenAdjacentRanges <0){
            throw new IllegalArgumentException("cluster distance can not be negative");
        }
        List<Range> sortedCopy = new ArrayList<Range>(rangesToMerge);
        Collections.sort(sortedCopy, Range.Comparators.ARRIVAL);

        mergeAnyRangesThatCanBeCombined(sortedCopy, maxDistanceBetweenAdjacentRanges);
        return sortedCopy;
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
     * @throws IllegalArgumentException if clusterDistance <0.
     */
    public static List<Range> mergeIntoClusters(Collection<Range> rangesToMerge, int maxClusterDistance){
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
        if(ranges.isEmpty()){
            return new Range.Builder().build();
        }
        Iterator<Range> iter =ranges.iterator();
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
    
    private static Range createInclusiveRange(Range... ranges){
    	return createInclusiveRange(Arrays.asList(ranges));
    }
}
