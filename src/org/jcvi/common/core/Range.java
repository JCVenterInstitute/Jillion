/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 *
 * 	This file is part of JCVI Java Common
 *
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jul 18, 2007
 *
 * @author dkatzel
 */
package org.jcvi.common.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.util.CommonUtil;


/**
 * A <code>Range</code> is a pair of coordinate values which describe a
 * contiguous subset of a sequence of values.  <code>Range</code>s are
 * immutable.  Changes to a <code>Range</code> are done using various methods
 * which generate new <code>Range</code> instances.
 * <p>
 * <code>Range</code>s have a start (or left) value and an end (or right)
 * value.  The start value will always be less than or equal to the end value.
 * <p>
 * <code>Range</code>s in are always inclusive.  Thus, a <code>Range</code>
 * of 20 to 30 has a size of 11, not 10, and a <code>Range</code> of 42 to 42
 * will have a size of 1 not 0.  This is done to conform with the overwhelming
 * majority use of inclusive ranges in Bioinformatics.
 * <p>
 * The implications of this are particularly important when thinking about the
 * desire to represent no range at all.  A <code>Range</code> of 0 to 0 still
 * has a size of 1.  In order to represent a <code>Range</code> with size 0,
 * you need to explicitly use an empty range.
 * @author jsitz
 * @author dkatzel
 */
public final class Range implements Placed<Range>,Iterable<Long>
{
    /**
     * {@code Comparators} is an enum of common Range
     * {@link Comparator} implementations.
     * @author dkatzel
     *
     *
     */
    public enum Comparators implements Comparator<Range>{
        /**
         * A <code>Arrival</code> compares a pair of {@link Range}s
         * and assigns the lower comparative value to the Range which begins earlier.
         * In the case of two ranges having identical start coordinates, the one
         * with the lower end coordinate (the shorter range) will be ranked lower.
         * Empty ranges are considered lower in comparative value than any non-empty
         * Range.
         * 
         * @author jsitz@jcvi.org
         * @author dkatzel
         */
        ARRIVAL{
            @Override
            public int compare(Range first, Range second) 
            {
                /*
                 * We don't accept null values for comparison.
                 */
                if (first == null){
                    throw new NullPointerException("The first parameter in the comparison is null.");
                }
                if (second == null){
                    throw new NullPointerException("The second parameter in the comparison is null.");
                }

                /*
                 * Compare first by the start values, then by the end values, if the ranges start
                 * in the same place.
                 */
                final int startComparison = Long.valueOf(first.getStart()).compareTo(Long.valueOf(second.getStart()));
                if (startComparison == 0)
                {
                    return Long.valueOf(first.getEnd()).compareTo(Long.valueOf(second.getEnd()));
                }
                return startComparison;
            }
        },
        /**
         * A <code>RangeDepartureComparator</code> compares a pair of {@link Range}s
         * and assigns the lower comparative value to the Range which ends earlier.
         * In the case of two ranges having identical end coordinates, the one
         * with the start end coordinate (the longer range) will be ranked lower.
         * Empty ranges are considered lower in comparative value than any non-empty
         * Range.
         * 
         * @author jsitz@jcvi.org
         * @author dkatzel
         */
        DEPARTURE{
            @Override
            public int compare(Range first, Range second) 
            {
                /*
                 * We don't accept null values for comparison.
                 */
                if (first == null){
                    throw new NullPointerException("The first parameter in the comparison is null.");
                }
                if (second == null){
                    throw new NullPointerException("The second parameter in the comparison is null.");
                }
                
                /*
                 * Compare first by the end values, then by the start values, if the ranges end
                 * in the same place.
                 */
                final int endComparison = Long.valueOf(first.getEnd()).compareTo(Long.valueOf(second.getEnd()));
                if (endComparison == 0)
                {
                    return Long.valueOf(first.getStart()).compareTo(Long.valueOf(second.getStart()));
                }
                return endComparison;
            }
        },
        /**
         * {@code LONGEST_TO_SHORTEST} compares Ranges by length
         * and orders them longest to shortest.
         * @author dkatzel
         */
        LONGEST_TO_SHORTEST{

            @Override
            public int compare(Range o1, Range o2) {
                return -1 * Long.valueOf(o1.getLength()).compareTo(o2.getLength());
            }
            
        },
        /**
         * {@code LONGEST_TO_SHORTEST} compares Ranges by length
         * and orders them shortest to longest.
         * @author dkatzel
         */
        SHORTEST_TO_LONGEST{

            @Override
            public int compare(Range o1, Range o2) {
                return Long.valueOf(o1.getLength()).compareTo(o2.getLength());
            }
            
        }
        ;
    }
    /**
     * Enumeration of available range coordinate systems.
     */
    public enum CoordinateSystem {
        /**
         * Zero-based coordinate systems are exactly like
         * array index offsets.  CoordinateSystem starts at 0
         * and the last element in the range has an offset
         * of {@code length() -1}.
         * <pre> 
         * coordinate system    0  1  2  3  4  5
         *                    --|--|--|--|--|--|
         * range elements       0  1  2  3  4  5
         * </pre>
         */
    	ZERO_BASED("Zero Based", "0B", 0, 0, 0, 0),
    	/**
    	 * Residue based coordinate system is a "1s based"
    	 * position system where there first element has a 
    	 * position of 1 and the last element in the range
    	 * as a position of length.
    	 *  <pre> 
         * coordinate system    1  2  3  4  5  6
         *                    --|--|--|--|--|--|
         * range elements       0  1  2  3  4  5
         * </pre>
    	 */
        RESIDUE_BASED("Residue Based", "RB", 1, 1, -1, -1),
        /**
         * Spaced based coordinate systems count the "spaces"
         * between elements.  The first element has a coordinate
         * of 0 while the last element in the range has a position 
         * of length.
         * <pre> 
         * coordinate system   0  1  2  3  4  5  6
         *                    --|--|--|--|--|--|--
         * range elements       0  1  2  3  4  5
         * </pre>
         */
        SPACE_BASED("Space Based", "SB", 0, 1, 0, -1);

        /** The full name used to display this coordinate system. */
        private String displayName;
        
        /** An abbreviated name to use as a printable <code>Range</code> annotation. */
        private String abbreviatedName;

        private long zeroBaseToCoordinateSystemStartAdjustmentValue;
        private long zeroBaseToCoordinateSystemEndAdjustmentValue;

        private long coordinateSystemToZeroBaseStartAdjustmentValue;
        private long coordinateSystemToZeroBaseEndAdjustmentValue;

        /**
         * Builds a <code>CoordinateSystem</code>.
         *
         * @param displayName The full name used to display this coordinate system.
         * @param abbreviatedName An abbreviated name to use as a printable <code>Range</code>
         * annotation.
         * @param zeroBaseToCoordinateSystemStartAdjustmentValue
         * @param zeroBaseToCoordinateSystemEndAdjustmentValue
         * @param coordinateSystemToZeroBaseStartAdjustmentValue
         * @param coordinateSystemToZeroBaseEndAdjustmentValue
         */
        private CoordinateSystem(String displayName,
                                 String abbreviatedName,
                                 long zeroBaseToCoordinateSystemStartAdjustmentValue,
                                 long zeroBaseToCoordinateSystemEndAdjustmentValue,
                                 long coordinateSystemToZeroBaseStartAdjustmentValue,
                                 long coordinateSystemToZeroBaseEndAdjustmentValue) {
            this.displayName = displayName;
            this.abbreviatedName = abbreviatedName;
            this.zeroBaseToCoordinateSystemStartAdjustmentValue = zeroBaseToCoordinateSystemStartAdjustmentValue;
            this.zeroBaseToCoordinateSystemEndAdjustmentValue = zeroBaseToCoordinateSystemEndAdjustmentValue;
            this.coordinateSystemToZeroBaseStartAdjustmentValue = coordinateSystemToZeroBaseStartAdjustmentValue;
            this.coordinateSystemToZeroBaseEndAdjustmentValue = coordinateSystemToZeroBaseEndAdjustmentValue;
        }

        /**
         * Fetches the shortened "tag" name for this <code>CoordinateSystem</code>.
         * 
         * @return A two-letter abbreviation for this <code>CoordinateSystem</code>.
         */
        public String getAbbreviatedName() 
        {
            return abbreviatedName;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString() 
        {
            return displayName;
        }

        /**
         * Get the start coordinate in this system from the 
         * equivalent zero-based start coordinate.
         */
        private long getLocalStart(long start) {
            return start + zeroBaseToCoordinateSystemStartAdjustmentValue;
        }

        private long getLocalEnd(long end) {
            return end + zeroBaseToCoordinateSystemEndAdjustmentValue;
        }

        /**
         * Get zero base start and end locations
        * from range coordinate system start and end locations
         */
        private long getStart(long localStart) {
            return localStart + coordinateSystemToZeroBaseStartAdjustmentValue;
        }

        private long getEnd(long localEnd) {
            return localEnd + coordinateSystemToZeroBaseEndAdjustmentValue;
        }

    }

    /**
     * Regular expression in the form (left) .. (right).
     */
    private static Pattern DOT_PATTERN = Pattern.compile("(\\d+)\\s*\\.\\.\\s*(\\d+)");
    /**
     * Regular expression in the form (left) - (right).
     */
    private static Pattern DASH_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
    private static Pattern COMMA_PATTERN = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");
    
    
    private static final Comparator<Range> DEFAULT_COMPARATOR = Comparators.ARRIVAL;

    /**
     * Factory method to build a {@link Range} object in
     * the {@link CoordinateSystem#ZERO_BASED} coordinate system.
     * If end == start -1 then this method will return an {@link EmptyRange}.
     * @param start start coordinate inclusive.
     * @param end end coordinate inclusive.
     * @return a {@link Range}.
     * @throws IllegalArgumentException if <code>end &lt; start -1</code>
     */
    public static Range buildRange(long start, long end){
        return buildRange(CoordinateSystem.ZERO_BASED,start,end);
    }
    /**
     * Factory method to build a {@link Range} object.
     * of length 1 with the given coordinate in 
     * the {@link CoordinateSystem#ZERO_BASED} coordinate system.
     * @param singleCoordinate only coordinate in this range.
     * @return a {@link Range}.
     */
    public static Range buildRange(long singleCoordinate){
        return buildRange(CoordinateSystem.ZERO_BASED,singleCoordinate);
    }
    
    /**
     * Factory method to build a {@link Range} object.
     * of length 1 with the given coordinate in 
     * the given coordinate system.
     * @param coordinateSystem the {@link CoordinateSystem} to use.
     * @param singleCoordinate only coordinate in this range.
     * @return a {@link Range}.
     * @throws NullPointerException if the coordinateSystem is null.
     */
    public static Range buildRange(CoordinateSystem coordinateSystem, long singleCoordinate){
        return buildRangeOfLength(coordinateSystem,singleCoordinate,1);
    }
    /**
     * Factory method to build a {@link Range} object.
     * with the following local start and end
     * coordinates in  
     * the given coordinate system.
     * @param coordinateSystem the {@link CoordinateSystem} to use.
     * @param localStart the start coordinate in the given coordinateSystem.
     * @param localEnd the end coordinate in the given coordinateSystem.
     * @return a {@link Range}.
     * @throws NullPointerException if the coordinateSystem is null.
     * @throws IllegalArgumentException if localEnd < localStart -1.
     */
    public static Range buildRange(CoordinateSystem coordinateSystem,long localStart, long localEnd){
        if ( coordinateSystem == null ) {
            throw new NullPointerException("Cannot build null coordinate system range");
        }

        long zeroBasedStart = coordinateSystem.getStart(localStart);
        long zeroBasedEnd = coordinateSystem.getEnd(localEnd);

        if(zeroBasedEnd >= zeroBasedStart) {
            return new Range(zeroBasedStart,zeroBasedEnd,coordinateSystem);
        } else if (zeroBasedEnd == zeroBasedStart-1) {
            return buildEmptyRange(zeroBasedStart,zeroBasedEnd,coordinateSystem);
        } else {
            throw new IllegalArgumentException("Range coordinates" + localStart + "," + localEnd
                + " are not valid " + coordinateSystem + " coordinates");
        }
    }
    /**
     * Create a new Range object which represents
     * the range values but converted into a different
     * coordinate system
     * @param coordinateSystem the coordinate system to convert to.
     * @return a new Range with the same start and end but converted
     * to the new coordinate system.
     * @throws NullPointerException if the coordinateSystem is null.
     */
    public Range convertRange(CoordinateSystem coordinateSystem) {
        if ( coordinateSystem == null ) {
            throw new NullPointerException("Cannot convert to a null range coordinate system");
        }
        if(coordinateSystem.equals(this.coordinateSystem)){
            return this;
        }
        
        
        return new Range(this.getStart(),this.getEnd(),coordinateSystem);
    }
    /**
     * Build and empty range in the zero-based coordinate system
     * at coordinate 0.
     * @return a new Empty Range.
     */
    public static Range buildEmptyRange(){
        return buildEmptyRange(0);
    }
    /**
     * Build and empty range in the zero-based coordinate system
     * at the given coordinate.
     * @param coordinate the coordinate to set this empty range to.
     * @return a new Empty Range.
     */
    public static Range buildEmptyRange(long coordinate){
        return buildEmptyRange(Range.CoordinateSystem.ZERO_BASED,coordinate);
    }
    /**
     * Build and empty range in the given coordinate system
     * at the given coordinate.
     * @param coordinate the coordinate to set this empty range to.
     * @return a new Empty Range.
     * @throws NullPointerException if the coordinateSystem is null.
     */
    public static Range buildEmptyRange(CoordinateSystem coordinateSystem,long coordinate){
        if ( coordinateSystem == null ) {
            throw new NullPointerException("Cannot build null coordinate system range");
        }
        
        long zeroBasedStart = coordinateSystem.getStart(coordinate);

        return buildEmptyRange(zeroBasedStart,zeroBasedStart-1,coordinateSystem);
    }
    private static Range buildEmptyRange(long start,long end,CoordinateSystem coordinateSystem) {
        return new Range(start,end,coordinateSystem);
    }
    /**
     * Build a new Range object of in the Zero based coordinate
     * system starting at 0 and with the given length.
     * @param length the length of this range.
     * @return a new Range.
     */
    public static Range buildRangeOfLength(long length){
        return buildRangeOfLength(0,length);
    }
    /**
     * Build a new Range object of in the Zero based coordinate
     * system at the given start offset with the given length.
     * @param start the start coordinate of this new range.
     * @param length the length of this range.
     * @return a new Range.
     */
    public static Range buildRangeOfLength(long start, long length){
        return buildRangeOfLength(CoordinateSystem.ZERO_BASED, start, length);
    }
    /**
     * Build a new Range object of in the given coordinate
     * system at the given start offset with the given length.
     * @param coordinateSystem the coordinate system to use.
     * @param localStart the start coordinate of this new range.
     * @param length the length of this range.
     * @return a new Range.
     * @throws NullPointerException if coordinateSystem is null.
     */
    public static Range buildRangeOfLength(CoordinateSystem coordinateSystem,long localStart, long length){
    	if ( coordinateSystem == null ) {
            throw new NullPointerException("Cannot build null coordinate system range");
        }
    	long zeroBasedStart = coordinateSystem.getStart(localStart);
        Range zeroBasedRange = buildRange(CoordinateSystem.ZERO_BASED,zeroBasedStart,zeroBasedStart+length-1);
        return zeroBasedRange.convertRange(coordinateSystem);
    }
    public static Range buildRangeOfLengthFromEndCoordinate(long end, long rangeSize){
        return buildRangeOfLengthFromEndCoordinate(CoordinateSystem.ZERO_BASED,end,rangeSize);
    }
    public static Range buildRangeOfLengthFromEndCoordinate(CoordinateSystem system,long end, long rangeSize){
        long zeroBasedEnd = system.getEnd(end);
        Range zeroBasedRange = buildRange(CoordinateSystem.ZERO_BASED,zeroBasedEnd-rangeSize+1,zeroBasedEnd);
        return zeroBasedRange.convertRange(system);
    }
    /**
     * Return a single
     * Range that covers the entire span
     * of the given Ranges.
     * <p>
     * For example: passing in 2 Ranges [0,10] and [20,30]
     * will return [0,30]
     * @param ranges varargs of Ranges
     * @return a new Range that covers the entire span of
     * input ranges.
     */
    public static Range buildInclusiveRange(Range... ranges){
        return buildInclusiveRange(Arrays.asList(ranges));
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
    public static Range buildInclusiveRange(Collection<Range> ranges){
        if(ranges.isEmpty()){
            return buildEmptyRange();
        }
        Iterator<Range> iter =ranges.iterator();
        Range firstRange =iter.next();
        long currentLeft = firstRange.getStart();
        long currentRight = firstRange.getEnd();
        while(iter.hasNext()){
            Range range = iter.next();
            if(range.getStart() < currentLeft){
                currentLeft = range.getStart();
            }
            if(range.getEnd() > currentRight){
                currentRight = range.getEnd();
            }
        }
        return buildRange(currentLeft, currentRight);
    }
    /**
     * Parses a string in the format &lt;left&gt;[.. | - ]&lt;right&gt;. 
     * Any whitespace between the left and right parameters is ignored.
     * <br>
     * Examples:
     * <ul>
     * <li>24 .. 35</li>
     * <li>24-35</li>
     * <li>24,35</li>
     * </ul>
     * 
     * @param rangeAsString
     * @return a {@link Range}.
     * @throws IllegalArgumentException if the given String does not
     * match the correct format.
     */
    public static Range parseRange(String rangeAsString, CoordinateSystem coordinateSystem){
        Matcher dotMatcher =DOT_PATTERN.matcher(rangeAsString);
        if(dotMatcher.find()){
            return convertIntoRange(dotMatcher,coordinateSystem);
        }
        Matcher dashMatcher = DASH_PATTERN.matcher(rangeAsString);
        if(dashMatcher.find()){
            return convertIntoRange(dashMatcher,coordinateSystem);
        }
        Matcher commaMatcher = COMMA_PATTERN.matcher(rangeAsString);
        if(commaMatcher.find()){
            return convertIntoRange(commaMatcher,coordinateSystem);
        }
        throw new IllegalArgumentException("can not parse "+ rangeAsString +" into a Range");
    }
    /**
     * Parses a string in the format &lt;left&gt;[.. | - ]&lt;right&gt;. 
     * Any whitespace between the left and right parameters is ignored.
     * <br>
     * Examples:
     * <ul>
     * <li>24 .. 35</li>
     * <li>24-35</li>
     * <li>24,35</li>
     * </ul>
     * 
     * @param rangeAsString
     * @return a {@link Range}.
     * @throws IllegalArgumentException if the given String does not
     * match the correct format.
     */
    public static Range parseRange(String rangeAsString){
        return parseRange(rangeAsString, CoordinateSystem.ZERO_BASED);
    }
    
    private static Range convertIntoRange(Matcher dashMatcher, CoordinateSystem coordinateSystem) {
        return Range.buildRange(coordinateSystem,Long.parseLong(dashMatcher.group(1)), 
                Long.parseLong(dashMatcher.group(2))
                );
    }
    /**
     * The start coordinate.
     * This coordinate stored relative to the zero base coordinate system
     */
    private final long start;

    /**
     * The end coordinate.
     * This coordinate stored relative to the zero base coordinate system
     */
    private final  long end;
    
    private final boolean isEmpty;

    /**
     * Object used to convert zero base coordinate system values to
     * the appropriate coordinate system values
     */
    private final CoordinateSystem coordinateSystem;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (start ^ (start >>> 32));
        result = prime * result + (int) (end ^ (end >>> 32));
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (!(obj instanceof Range)){
            return false;
        }
        final Range other = (Range) obj;
        return CommonUtil.similarTo(getStart(), other.getStart())
        && CommonUtil.similarTo(getEnd(), other.getEnd());
        
    }

    /**
     * Creates a new <code>Range</code> with the given coordinates.
     *
     * @param start The inclusive start coordinate.
     * @param end The inclusive end coordinate.
     */
    private Range(long start, long end, CoordinateSystem rangeCoordinateSystem) {
        this.start = start;
        this.end = end;
        this.isEmpty = end-start==-1;
        this.coordinateSystem = rangeCoordinateSystem;
    }
    /**
     * Fetch the left (start) coordinate.
     *
     * @return The left-hand (starting) coordinate.
     */
    public long getStart() {
        return start;
    }

    /**
     * Fetch the right (end) coordinate.
     *
     * @return The right-hand (ending) coordinate.
     */
    public long getEnd() {
        return end;
    }

    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }


    public long getLocalStart() {
        return coordinateSystem.getLocalStart(start);
    }

    public long getLocalEnd() {
        return coordinateSystem.getLocalEnd(end);
    }

    /**
     * Calculate the size of the <code>Range</code>.  All <code>Range</code>s
     * are inclusive.
     *
     * @return The inclusive count of values between the left and right
     * coordinates.
     */
    public long size() {
        return end - start + 1;
    }
    /**
     * Create a new Range of the same size
     * but shifted to the left the specified number of units.
     * @param units number of units to shift
     * @return a new Range (not null)
     */
    public Range shiftLeft(long units){
        return Range.buildRangeOfLength(this.start-units, this.size())
                .convertRange(coordinateSystem);
    }
    /**
     * Create a new Range of the same size
     * but shifted to the right the specified number of units.
     * @param units number of units to shift
     * @return a new Range (not null)
     */
    public Range shiftRight(long units){
        return Range.buildRangeOfLength(this.start+units, this.size())
            .convertRange(coordinateSystem);
    }
    /**
     * Checks if this range is empty.
     *
     * @return <code>true</code> if the range is empty, <code>false</code>
     * otherwise.
     */
    public boolean isEmpty()
    {
        return isEmpty;
    }

    /**
     * Checks to see if the given target <code>Range</code> is contained within
     * this <code>Range</code>.  This does not require this <code>Range</code>
     * to be a strict subset of the target.  More precisely: a
     * <code>Range</code> is always a sub-range of itself.
     *
     * @param range The <code>Range</code> to compare to.
     * @return <code>true</code> if every value in this <code>Range</code> is
     * found in the given comparison <code>Range</code>.
     */
    public boolean isSubRangeOf(Range range) {
        if(range==null){
            return false;
        }
        
        /* We are always a subrange of ourselves */
        if (this.equals(range))
        {
            return true;
        }
        return isCompletelyInsideOf(range);
       
    }
    private boolean isCompletelyInsideOf(Range range) {
        return (start>range.start && end<range.end) ||
           (start==range.start && end<range.end) ||
           (start>range.start && end==range.end);
    }

    /**
     * Checks to see if the given {@link Range} intersects this one.
     *
     * @param target The {@link Range} to check.
     * @return <code>true</code> if the coordinates of the two ranges overlap
     * each other in at least one point.
     */
    public boolean intersects(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in intersection operation.");
        }
        if(isEmpty()){
            return false;
        }
        if (target.isEmpty())
        {
            /*
             * Instead of defining empty set semantics here, we do it in the
             * EmptyRange class
             * -jsitz
             */
            return target.intersects(this);
        }

        return !(this.start > target.end || this.end < target.start);
    }
    public boolean intersects(long coordinate){
        return coordinate >= this.start && coordinate <=this.end;
    }
    /**
     * Calculates the intersection of this {@link Range} and a second one.
     * <p>
     * The intersection of an empty list with any other list is always the
     * empty list.  The intersection of
     *
     * @param target The second {@link Range} to compare
     * @return A {@link Range} object spanning only the range of values covered
     * by both {@link Range}s.
     */
    public Range intersection(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in intersection operation.");
        }
        if(isEmpty()){
            return this;
        }
        if (target.isEmpty())
        {
            return target.intersection(this);
        }

        try{
            return Range.buildRange(Math.max(target.getStart(), this.start),
                            Math.min(target.getEnd(), this.end)).convertRange(getCoordinateSystem());
        }
        catch(IllegalArgumentException e){
            return buildEmptyRange().convertRange(getCoordinateSystem());
        }

    }
    /**
     * Get the List of Ranges that represents the 
     * {@code this - other}.  This is similar to the 
     * Set of all coordinates that don't intersect.
     * @param other the range to compliment with.
     * @return
     */
    public List<Range> compliment(Range other){
        //this - other
        //anything in this that doesn't intersect with other
        Range intersection = intersection(other);
        if(intersection.isEmpty()){
            return Arrays.asList(this);
        }
        
        Range beforeOther = Range.buildRange(getStart(), intersection.getStart()-1);
        Range afterOther = Range.buildRange(intersection.getEnd()+1, getEnd());
        List<Range> complimentedRanges = new ArrayList<Range>();
        if(!beforeOther.isEmpty()){
            complimentedRanges.add(beforeOther);
        }
        if(!afterOther.isEmpty()){
            complimentedRanges.add(afterOther);
        }
        return Range.mergeRanges(complimentedRanges);
    }
    
    public List<Range> complimentFrom(Collection<Range> ranges){
        List<Range> universe = Range.mergeRanges(new ArrayList<Range>(ranges));
        List<Range> compliments = new ArrayList<Range>(universe.size());
        for(Range range : universe){
            compliments.addAll(range.compliment(this));
        }
        return Range.mergeRanges(compliments);
    }

    /**
     * Checks to see if this <code>Range</code> starts before the given
     * comparison <code>Range</code>.
     *
     * @param target The <code>Range</code> to compare to.
     * @return <code>true</code> if the left-hand coordinate of this
     * <code>Range</code> is less than the left-hand coordinate of the
     * comparison <code>Range</code>.
     */
    public boolean startsBefore(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in range comparison operation.");
        }

        return this.getStart() < target.getStart();
    }

    /**
     * Checks to see if this <code>Range</code> ends before the given target.
     *
     * @param target The target <code>Range</code> to check against.
     * @return <code>true</code> if this <code>Range</code> has an end value
     * which occurs before (and not at the same point as) the target
     * <code>Range</code>.
     */
    public boolean endsBefore(Range target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in range comparison operation.");
        }
        if (isEmpty || target.isEmpty())
        {
            return false;
        }
        
        return this.getEnd() < target.getStart();
    }

    /**
     * Constructs the union of two <code>Range</code>s.  If the {@link Range}s
     * are disjoint (<code>this.{@link #intersects(Range)}</code> is
     * <code>false</code>), then the result cannot be contained in a single
     * <code>Range</code>.
     *
     * @param target The <code>Range</code> to union with this one.
     * @return An array of <code>Range</code>s containing a {@link Range} to
     * cover all values covered by either this or the comparison {@link Range}.
     * <code>Range</code>s which intersect will have a union array with a
     * single element, while disjoint <code>Range</code>s will have a union
     * array of two elements.
     */
    public Range[] union(Range target)
    {
        
        if (target == null)
        {
            throw new IllegalArgumentException("Null Range used in union operation.");
        }
        if(isEmpty()){
            return new Range[]{target};
        }
        if (target.isEmpty())
        {
            /*
             * Instead of defining empty set semantics here, we do it in the
             * EmptyRange class
             * -jsitz
             */
            return target.union(this);
        }
        if (this.intersects(target))
        {
            return handleUnionIntersection(target);
        }
        
        return handleDisjointUnion(target);   
    }
    
    /**
     * Modifies the extent of a range by simultaneously adjusting its coordinates by specified
     * amounts.  This method is primarily intended to increase the size of the
     * <code>Range</code>, and as such, positive values will result in a <code>Range</code>
     * which is longer than the current <code>Range</code>.  Negative values may also be used,
     * with appropriately opposite results, and positive and negative deltas may be mixed to 
     * produce a traslation/scaling effect.
     * 
     * @param fromStart The number of positions to extend the start of the range.
     * @param fromEnd The number of positions to extend the end of the range.
     * @return A new <code>Range</code> in the same {@link RangeCoordinateSystem}, with modified
     * coordinates.
     */
    public Range grow(long fromStart, long fromEnd)
    {
        return Range.buildRange(this.getCoordinateSystem(), this.getStart() - fromStart, this.getEnd() + fromEnd);
    }
    
    /**
     * Modifies the extend of a <code>Range</code> by adjusting its coordinates.  This is 
     * directly related to the {@link #grow(long, long)} method.  It simply passes the 
     * numerical negation of the values given here.
     * <p>
     * This is done as a convenience to make code easier to read.  Usually this method will be
     * called with variables in the parameters and it will not be immediately obvious that the
     * end result is intended to be a smaller <code>Range</code>.  This method should be used to
     * make this situation more clear.
     * 
     * @param fromStart The number of positions to extend the start of the range.
     * @param fromEnd The number of positions to extend the end of the range.
     * @return A new <code>Range</code> in the same {@link RangeCoordinateSystem}, with modified
     * coordinates.
     */
    public Range shrink(long fromStart, long fromEnd)
    {
        return this.grow(-fromStart, -fromEnd);
    }
    
    private Range[] handleDisjointUnion(Range target) {
        Range[] ranges = new Range[2];

        if (this.startsBefore(target))
        {
            ranges[0] = this;
            ranges[1] = target;
        }
        else
        {
            ranges[0] = target;
            ranges[1] = this;
        }
        return ranges;
    }
    private Range[] handleUnionIntersection(Range target) {
        return new Range[]{Range.buildRange(
                Math.min(this.getStart(), target.getStart()),
               Math.max(this.getEnd(), target.getEnd())),};
    }

    /**
     * Returns a String represenatation of this Range in local coordinates.
     * The actual format is {@code [localStart - localEnd]/systemAbbreviatedName}
     */
    @Override
    public String toString()
    {
        return String.format("[ %d - %d ]/%s", 
                this.getLocalStart() ,this.getLocalEnd() ,
            getCoordinateSystem().getAbbreviatedName());
    }
  
    
   

    @Override
    public Iterator<Long> iterator() {
        return new RangeIterator(this);
    }
    /**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * This is the same as {@link #mergeAnyRangesThatCanBeCombined(List, int) mergeAnyRangesThatCanBeCombined(rangesToMerge,0)} 
     * @param rangesToMerge
     * @return a new list of merged Ranges.
     * @see #mergeAnyRangesThatCanBeCombined(List, int)
     */
    public static List<Range> mergeRanges(List<Range> rangesToMerge){
        return mergeRanges(rangesToMerge,CoordinateSystem.ZERO_BASED);
    }
    public static List<Range> mergeRanges(List<Range> rangesToMerge, CoordinateSystem coordinateSystem){
        return mergeRanges(rangesToMerge,0,coordinateSystem);
    }
    /**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * For example 2 ranges [0-2] and [1-4] could be merged into a single
     * range [0-4].
     * @param rangesToMerge the ranges to be merged together.
     * @param maxDistanceBetweenAdjacentRanges the maximum distance between the end of one range
     * and the start of another inorder
     * to be merged.
     * @return a new list of merged Ranges.
     * @throws IllegalArgumentException if clusterDistance <0.
     */
    public static List<Range> mergeRanges(List<Range> rangesToMerge, int maxDistanceBetweenAdjacentRanges){
        return mergeRanges(rangesToMerge,maxDistanceBetweenAdjacentRanges,CoordinateSystem.ZERO_BASED);
    }
    /**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * For example 2 ranges [0-2] and [1-4] could be merged into a single
     * range [0-4].
     * @param rangesToMerge the ranges to be merged together.
     * @param maxDistanceBetweenAdjacentRanges the maximum distance between the end of one range
     * and the start of another inorder
     * to be merged.
     * @return a new list of merged Ranges.
     * @throws IllegalArgumentException if clusterDistance <0.
     */
    public static List<Range> mergeRanges(List<Range> rangesToMerge, int maxDistanceBetweenAdjacentRanges,CoordinateSystem coordinateSystem){
        if(maxDistanceBetweenAdjacentRanges <0){
            throw new IllegalArgumentException("cluster distance can not be negative");
        }
        List<Range> sortedCopy = new ArrayList<Range>(rangesToMerge);
        Collections.sort(sortedCopy);

        mergeAnyRangesThatCanBeCombined(sortedCopy, maxDistanceBetweenAdjacentRanges,coordinateSystem);
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
    public static List<Range> mergeRangesIntoClusters(List<Range> rangesToMerge, int maxClusterDistance){
        return mergeRangesIntoClusters(rangesToMerge, maxClusterDistance, CoordinateSystem.ZERO_BASED);

    }
    public static List<Range> mergeRangesIntoClusters(List<Range> rangesToMerge, int maxClusterDistance,
            CoordinateSystem coordinateSystem){
        List<Range> tempRanges = Range.mergeRanges(rangesToMerge,coordinateSystem);
        return privateMergeRangesIntoClusters(tempRanges,maxClusterDistance,coordinateSystem);

    }
    private static List<Range> privateMergeRangesIntoClusters(List<Range> rangesToMerge, int maxClusterDistance,
            CoordinateSystem coordinateSystem){
        if(maxClusterDistance <0){
            throw new IllegalArgumentException("max cluster distance can not be negative");
        }
        List<Range> sortedSplitCopy = new ArrayList<Range>();
        for(Range range : rangesToMerge){
            sortedSplitCopy.addAll(range.split(maxClusterDistance,coordinateSystem));
        }        
        
        privateMergeAnyRangesThatCanBeClustered(sortedSplitCopy, maxClusterDistance,coordinateSystem);
        return sortedSplitCopy;
    }
    public List<Range> split(long maxSplitLength){
        return split(maxSplitLength, getCoordinateSystem());
    }
    public List<Range> split(long maxSplitLength, CoordinateSystem coordinateSystem){
        if(size()<maxSplitLength){
            return Collections.singletonList(this.convertRange(coordinateSystem));
        }
        long currentStart=getStart();
        List<Range> list = new ArrayList<Range>();
        while(currentStart<=getEnd()){
            long endCoordinate = Math.min(getEnd(), currentStart+maxSplitLength-1);
            list.add(Range.buildRange(currentStart, endCoordinate)
                        .convertRange(coordinateSystem));
            currentStart = currentStart+maxSplitLength;
        }
        return list;
    }
    private static void privateMergeAnyRangesThatCanBeClustered(List<Range> rangesToMerge, int maxClusterDistance,
            CoordinateSystem coordinateSystem) {
        boolean merged;
        do{
            merged = false;
            for(int i=0; i<rangesToMerge.size()-1; i++){
                Range range = rangesToMerge.get(i);
                Range nextRange = rangesToMerge.get(i+1);
                final Range combinedRange = Range.buildInclusiveRange(range,nextRange);
                if(combinedRange.size()<= maxClusterDistance){
                    //can be combined
                    replaceWithCombined(rangesToMerge,range, nextRange,coordinateSystem);
                    merged= true;
                    break;
                }                
            }            
        }while(merged);
    }
    
    private static void mergeAnyRangesThatCanBeCombined(List<Range> rangesToMerge, int clusterDistance,CoordinateSystem coordinateSystem) {
        boolean merged;
        do{
            merged = false;
            for(int i=0; i<rangesToMerge.size()-1; i++){
                Range range = rangesToMerge.get(i);
                Range clusteredRange = Range.buildRange(range.getStart()-clusterDistance, range.getEnd()+clusterDistance);
                Range nextRange = rangesToMerge.get(i+1);
                if(clusteredRange.intersects(nextRange) || clusteredRange.shiftRight(1).intersects(nextRange)){
                    replaceWithCombined(rangesToMerge,range, nextRange,coordinateSystem);
                    merged= true;
                    break;
                }
            }
        }while(merged);
    }
    private static void replaceWithCombined(List<Range> rangeList, Range range, Range nextRange,CoordinateSystem coordinateSystem) {
        final Range combinedRange = Range.buildInclusiveRange(range,nextRange).convertRange(coordinateSystem);
        int index =rangeList.indexOf(range);
        rangeList.remove(range);
        rangeList.remove(nextRange);
        rangeList.add(index, combinedRange);
        
    }
    @Override
    public int compareTo(Range that) 
    {
        return Range.DEFAULT_COMPARATOR.compare(this, that);
    }

    @Override
    public long getLength() {
        return size();
    }
    /**
    * @return this.
    */
    @Override
    public Range asRange() {
        return this;
    }
    
    private static class RangeIterator implements Iterator<Long>{
        private final long from;
        private final long to;
        private long index;
        
        public RangeIterator(Range range){
            from = range.getStart();
            to = range.getEnd();
            index = from;
        }
        @Override
        public boolean hasNext() {
            return index<=to;
        }

        @Override
        public Long next() {
            return index++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("can not remove from Range");
            
        }
        
    }
}
