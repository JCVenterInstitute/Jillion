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
/*
 * Created on Jul 18, 2007
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.util.Caches;
import org.jcvi.jillion.internal.core.util.JillionUtil;


/**
 * A <code>Range</code> is a pair of coordinate values which describe a
 * contiguous subset of a sequence of values.  <code>Range</code>s are
 * immutable.  Changes to a <code>Range</code> are done using {@link Builder Range.Builder} 
 * to create new instances.
 * <p>
 * <code>Range</code>s have a begin value and an end
 * value.  The start value will always be less than or equal to the end value.
 * The minimum start value of a Range is {@link Long#MIN_VALUE}  and the max end
 * value of a Range is {@link Long#MAX_VALUE}. Also due to limitations
 * to Java primitives, Ranges can not have a length > {@link Long#MAX_VALUE}.
 *  Any attempt to build Ranges beyond
 * those values will throw Exceptions.
 * <p>
 * The Range coordinates are 0-based inclusive.  Thus, a <code>Range</code>
 * of 20 to 30 has a size of 11, not 10, and a <code>Range</code> of 42 to 42
 * will have a size of 1 not 0.  This is done to conform with the overwhelming
 * majority use of inclusive ranges in Bioinformatics. The implications of this are particularly important when thinking about the
 * desire to represent no range at all.  A <code>Range</code> of 0 to 0 still
 * has a size of 1.  In order to represent a <code>Range</code> with length 0,
 * you need to create a Range of length 0 by either using the static factory method
 * {@link Range#ofLength(long) Range.ofLength(0)} or create a new
 * {@link Builder Range.Builder} instance with either the empty constructor
 * or use the methods on the builder to shrink the range to an empty length.
 * <p>
 * Often, Bioinformatics formats use non-0-based coordinates. Other coordinate system start and end values can be queried
 * via the {@link #getBegin(CoordinateSystem)} and {@link #getEnd(CoordinateSystem)} methods.  
 * A different {@link CoordinateSystem} can be also be specified at construction time
 * via the {@link Range#of(CoordinateSystem, long, long)} method.  If this method is used,
 * the input values will automatically get converted into 0-based coordinates.
 * <p/>
 * Ranges can be constructed using either {@link Builder Range.Builder} 
 * of through several convenience static factory methods including
 *  {@link Range#of(long)}, {@link Range#of(long, long)} ,
 *  {@link Range#of(CoordinateSystem, long, long)} and {@link Range#ofLength(long)}.
 *  All of these methods use {@link Builder Range.Builder}  internally.
 *  <p/>
 *  The actual implementation of Range returned by these methods or the {@link Builder Range.Builder} 
 *  might vary based on input values in order to decrease memory usage.  (For example a Range that is very short
 *  could represent the length as a byte instead of a long.  Or if the range is in positive
 *  coordinates then memory could be saved by using unsigned values instead of signed. etc).
 *  In addition, since Ranges are immutable,
 * it is not guaranteed that the Range object returned by these creation methods
 * is a new instance since Ranges are often cached (Flyweight pattern).  Therefore;
 * <strong> Range objects should not be used
 * for synchronization locks.</strong>  Range objects are cached and shared, synchronizing
 * on the same object as other, unrelated code can cause deadlock.
 * <pre> 
 * &#047;&#047;don't do this
 * private static Range range = Range.ofLength(10);
 * ...
 *   synchronized(range){ .. }
 * ...
 * </pre>
 * @author dkatzel
 * @author jsitz@jcvi.org
 * 
 * @see CoordinateSystem
 * @see #Range.Builder
 * 
 */
public abstract class Range implements Rangeable,Iterable<Long>
{
	/**
	 * 2^8 -1.
	 */
	private static final int UNSIGNED_BYTE_MAX = 255;
	/**
	 * 2^16 -1.
	 */
	private static final int UNSIGNED_SHORT_MAX = 65535;
	/**
	 * 2^32 -1.
	 */
	private static final long UNSIGNED_INT_MAX = 4294967295L;
	/**
	 * Initial size of our cache of ranges {@link #CACHE}.
	 */
    private static final int INITIAL_CACHE_SIZE = 1024;

    /**
     * Regular expression in the form (left) .. (right).
     */
    private static Pattern DOT_PATTERN = Pattern.compile("(\\d+)\\s*\\.\\.\\s*(\\d+)");
    /**
     * Regular expression in the form (left) - (right).
     */
    private static Pattern DASH_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
    /**
     * Regular expression in the form (left) , (right).
     */
    private static Pattern COMMA_PATTERN = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");
    /**
     * Cache of previously built ranges.  
     * This cache uses  {@link SoftReference}s
     * so memory can be reclaimed if needed.
     */
    private static final Map<String, Range> CACHE;
    
    
    /**
     * {@code Comparators} is an enum of common Range
     * {@link Comparator} implementations.
     * @author dkatzel
     *
     *
     */
    public enum Comparators implements Comparator<Range>{
        /**
         * Compares a pair of {@link Range}s
         * and assigns the lower comparative value to the Range which begins earlier.
         * In the case of two ranges having identical start coordinates, the one
         * with the lower end coordinate (the shorter range) will be ranked lower.
         * 
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
                final int startComparison = JillionUtil.compare(first.getBegin(),second.getBegin());
                if (startComparison == 0)
                {
                    return JillionUtil.compare(first.getEnd(), second.getEnd());
                }
                return startComparison;
            }
        },
        /**
         * Compares a pair of {@link Range}s
         * and assigns the lower comparative value to the Range which ends earlier.
         * In the case of two ranges having identical end coordinates, the one
         * with the lower start coordinate (the longer range) will be ranked lower.
         * 
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
                final int endComparison = JillionUtil.compare(first.getEnd(),second.getEnd());
                if (endComparison == 0)
                {
                    return JillionUtil.compare(first.getBegin(),second.getBegin());
                }
                return endComparison;
            }
        },
        /**
         * Compares Ranges by length
         * and orders them longest to shortest. Ranges
         * of the same length are considered equal.
         */
        LONGEST_TO_SHORTEST{

            @Override
            public int compare(Range o1, Range o2) {
                return -1 * JillionUtil.compare(o1.getLength(), o2.getLength());
            }
            
        },
        /**
         * Compares Ranges by length
         * and orders them shortest to longest.
         * Ranges
         * of the same length are considered equal.
         */
        SHORTEST_TO_LONGEST{

            @Override
            public int compare(Range o1, Range o2) {
                return JillionUtil.compare(o1.getLength(),o2.getLength());
            }
            
        }
        ;
     
    }
    /**
     * Enumeration of available range coordinate systems.
     * <p/>
     * Different file formats or conventions use
     * different numbering systems in bioinformatics utilities.
     * All Range objects use the same internal system to be inter-operable
     * but users may want ranges to be input or output into different
     * coordinate systems to fit their needs.  CoordinateSystem implementations
     * can be used to translate to and from the various bioinformatics coordinate
     * systems to simplify working with multiple coordinate systems at the same time.
     * @see Range#getBegin(CoordinateSystem)
     * @see Range#getEnd(CoordinateSystem)
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
    	 * has a position of length.
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
         * Get the shortened "tag" name for this <code>CoordinateSystem</code>.
         * to be used in the toString value.
         * @return A two-letter abbreviation for this <code>CoordinateSystem</code>.
         */
        public String getAbbreviatedName() 
        {
            return abbreviatedName;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        @Override
        public String toString() 
        {
            return displayName;
        }

        /**
         * Get the start coordinate in this system from the 
         * equivalent zero-based start coordinate.
         * @param zeroBasedStart start coordinate in 0-based
         * coordinate system.
         */
        private long getLocalStart(long zeroBasedStart) {
            return zeroBasedStart + zeroBaseToCoordinateSystemStartAdjustmentValue;
        }
        /**
         * Get the end coordinate in this system from the 
         * equivalent zero-based end coordinate.
         * @param zeroBasedEnd the end coordinate in 0-based
         * coordiante system.
         */
        private long getLocalEnd(long zeroBasedEnd) {
            return zeroBasedEnd + zeroBaseToCoordinateSystemEndAdjustmentValue;
        }

        /**
         * Get 0-base start coordinate
        * from this coordinate system start location.
         */
        private long getStart(long localStart) {
            return localStart + coordinateSystemToZeroBaseStartAdjustmentValue;
        }
        /**
         * Get 0-base end location
        * from this coordinate system  end location.
         */
        private long getEnd(long localEnd) {
            return localEnd + coordinateSystemToZeroBaseEndAdjustmentValue;
        }

    }
    
    /**
     * Initialize cache with a soft reference cache that will grow as needed.
     */
    static{
         CACHE = Caches.<String, Range>createSoftReferencedValueCache(INITIAL_CACHE_SIZE);
    }
    /**
     * Factory method to get a {@link Range} object in
     * the {@link CoordinateSystem#ZERO_BASED} coordinate system.
     * If end == start -1 then this method will return an empty range.
     * This method is not guaranteed to return new instances and may return
     * a cached instance instead (flyweight pattern).
     * @param start start coordinate inclusive.
     * @param end end coordinate inclusive.
     * @return a {@link Range}; never null but might 
     * not be a new instance.
     * @throws IllegalArgumentException if {@code end < start -1} 
     * or if the resulting range length > {@link Long#MAX_VALUE}.
     */
    public static Range of(long start, long end){
        return new Range.Builder(start,end).build();
    }
    /**
     * Factory method to build a {@link Range} object.
     * of length 1 with the given coordinate in 
     * the {@link CoordinateSystem#ZERO_BASED} coordinate system.
     * @param singleCoordinate only coordinate in this range.
     * @return a {@link Range}; never null but might 
     * not be a new instance.
     */
    public static Range of(long singleCoordinate){
        return new Range.Builder(1)
        			.shift(singleCoordinate)
        			.build();
    }

    /**
     * Factory method to build a {@link Range} object
     * with the given coordinates
     * specified in the given coordinate system. If after converting 
     * the coordinates into 0-based coordinate,
     * {@code end = start -1}, then
     * the returned range is equivalent to an empty range
     * at the start coordinate.
     * @param coordinateSystem the {@link CoordinateSystem} to use.
     * @param localStart the start coordinate in the given coordinateSystem.
     * @param localEnd the end coordinate in the given coordinateSystem.
     * @return a non-empty Range instance.  This is not guaranteed to be a 
     * new instance since Ranges use the flyweight pattern
     * to reuse the same objects.
     * @throws NullPointerException if coordinateSystem is null.
     * @throws IllegalArgumentException if length is negative
     *  @throws IndexOutOfBoundsException if the combination 
     * of start and length values would cause the Range to extend
     * beyond {@link Long#MAX_VALUE}.
     */
    public static Range of(CoordinateSystem coordinateSystem,long localStart, long localEnd){
        return new Range.Builder(coordinateSystem, localStart, localEnd)
        			.build();    	
    }
    /**
     * Builds a new Range instance whose implementation depends
     * on the input start and end coordinates.  The implementation
     * that can take up the fewest number of bytes is chosen.
     * @param zeroBasedStart
     * @param zeroBasedEnd
     * @return a new Range instance.
     */
    private static Range buildNewRange(long zeroBasedStart, long zeroBasedEnd){
    	
    	
    	if(zeroBasedStart >=0){
    		//can use unsigned
    		long length = zeroBasedEnd - zeroBasedStart+1;
    		return buildNewUnsignedRange(zeroBasedStart, zeroBasedEnd,length);
    	}
    	
    	return buildNewSignedRange(zeroBasedStart, zeroBasedEnd);
    }
    /**
     * Create a new Range instance that requires signed values
     * (probably because the range has negative coordinates).
     *  The implementation
     * that can take up the fewest number of bytes is chosen.
     * @param zeroBasedStart
     * @param zeroBasedEnd
     * @return a new Range instance.
     */
	private static Range buildNewSignedRange(long zeroBasedStart,
			long zeroBasedEnd) {

    	if(canFitInSignedByte(zeroBasedStart, zeroBasedEnd)){
    		return new ByteRange((byte)zeroBasedStart, (byte)zeroBasedEnd);
    	}else if(canFitInSignedShort(zeroBasedStart,zeroBasedEnd)){
    		return new ShortRange((short)zeroBasedStart, (short)zeroBasedEnd);
    	}else if(canFitInSignedInt(zeroBasedStart,zeroBasedEnd)){
    		return new IntRange((int)zeroBasedStart, (int)zeroBasedEnd);
    	}    	
    	return new LongRange(zeroBasedStart, zeroBasedEnd);
	}
	
	private static boolean canFitInSignedByte(long start, long end){
		return start <= Byte.MAX_VALUE && start >=Byte.MIN_VALUE
    			&& end <= Byte.MAX_VALUE && end >=Byte.MIN_VALUE;
	}
	private static boolean canFitInSignedShort(long start, long end){
		return start <= Short.MAX_VALUE && start >=Short.MIN_VALUE
    			&& end <= Short.MAX_VALUE && end >=Short.MIN_VALUE;
	}
	private static boolean canFitInSignedInt(long start, long end){
		return start <= Integer.MAX_VALUE && start >=Integer.MIN_VALUE
    			&& end <= Integer.MAX_VALUE && end >=Integer.MIN_VALUE;
	}
	/**
	 * Create a new Range instance which can use unsigned
	 * values to save memory.  The implementation
     * that can take up the fewest number of bytes is chosen.
	 * @param zeroBasedStart
	 * @param zeroBasedEnd
	 * @param length
	 * @return
	 */
	private static Range buildNewUnsignedRange(long zeroBasedStart,
			long zeroBasedEnd, long length) {
		
		//JVM spec of computing size of objects
		//in heap includes padding
		//to keep objects a multiple of 8 bytes.
		//This means that not all byte-short-int-long combinations
		//actually affect the object size.
		if(zeroBasedStart <= UNSIGNED_BYTE_MAX){			
			if(length <= UNSIGNED_SHORT_MAX){
				return new UnsignedByteStartShortLengthRange((short) zeroBasedStart, (int)length);
			}
			if(length <= UNSIGNED_INT_MAX){
				return new UnsignedByteStartIntLengthRange((short) zeroBasedStart, length);
			}
			return new UnsignedByteStartLongLengthRange((short) zeroBasedStart, length);
		}
		
		if(zeroBasedStart <= UNSIGNED_SHORT_MAX){
			if(length <= UNSIGNED_SHORT_MAX){
				return new UnsignedShortStartShortLengthRange((int) zeroBasedStart, (int)length);
			}
			if(length <= UNSIGNED_INT_MAX){
				return new UnsignedShortStartIntLengthRange((int) zeroBasedStart, length);
			}
			return new UnsignedShortStartLongLengthRange((int) zeroBasedStart, length);
		}
		if(zeroBasedStart <= UNSIGNED_INT_MAX){
			if(length <= UNSIGNED_INT_MAX){
				return new UnsignedIntStartIntLengthRange(zeroBasedStart, length);
			}
			return new UnsignedIntStartLongLengthRange(zeroBasedStart, length);
		}
		if(length <= UNSIGNED_INT_MAX){
			return new LongStartIntLengthRange(zeroBasedStart, length);
		}
		return new LongRange(zeroBasedStart, zeroBasedEnd);

	}
	private static Range buildNewEmptyRange(long zeroBasedStart) {
		if(zeroBasedStart >=0){
			if(zeroBasedStart <=Byte.MAX_VALUE){
				return new EmptyByteRange((byte)zeroBasedStart);
			}else if(zeroBasedStart <=Short.MAX_VALUE){
				return new EmptyShortRange((short)zeroBasedStart);
			}else if(zeroBasedStart <=Integer.MAX_VALUE){
				return new EmptyIntRange((int)zeroBasedStart);
			}
		}
		//anything negative or > unsigned int should be stored as a long
		return new EmptyLongRange(zeroBasedStart);
	}
    private static synchronized Range getFromCache(Range range) {
       if(range==null){
    	   throw new NullPointerException("can not add null range to cache");
       }
        String hashcode = createCacheKeyFor(range);
       
        //contains() followed by get() is not atomic;
        //we could gc in between - so only do a get
        //and check if null.
        Range cachedRange= CACHE.get(hashcode);
        if(cachedRange !=null){
        	return cachedRange;
        }
        //not in cache so put it in
        CACHE.put(hashcode,range);
        return range;

    }
    private static String createCacheKeyFor(Range r){
        //Range's toString() should be fine
        //to ensure uniqueness in our cache.
        return r.toString();
    }

    /**
     * Create a non-empty Range object in the Zero based coordinate
     * system starting at 0 and with the given length.
     * @param length the length of this range.
     * @return a non-empty Range instance  whose {@link Range#getBegin()}
     * will return {@code 0} and {@link Range#getLength()}
     * will return the pass in value.  This is not guaranteed to be a 
     * new instance since Ranges use the flyweight pattern
     * to reuse the same objects.
     * @throws IllegalArgumentException if length is negative
     */
    public static Range ofLength(long length){
        return new Range.Builder(length).build();
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
        return Range.of(coordinateSystem,Long.parseLong(dashMatcher.group(1)), 
                Long.parseLong(dashMatcher.group(2))
                );
    }


    private Range(){
    	//can not instantiate
    }

    
    @Override
	public abstract int hashCode();
    /**
     * Two {@link Range}s are equal
     * if they have the same begin and end
     * values.
     */
	@Override
	public abstract boolean equals(Object obj);
	
	/**
     * Fetch the first coordinate in this Range. This is the same as 
     * {@link #getBegin(CoordinateSystem)
     * getBegin(ZERO_BASED)}.
     *
     * @return The left-hand (starting) coordinate.
     * 
     */
    public abstract long getBegin();
    /**
     * Fetch the first coordinate using the given 
     * {@link CoordinateSystem}.  
     *
     * @return The first coordinate.
     * @throws NullPointerException if the given {@link CoordinateSystem} is null.
     */
    public long getBegin(CoordinateSystem coordinateSystem) {
    	if(coordinateSystem==null){
    		throw new NullPointerException("CoordinateSystem can not be null");
    	}
        return coordinateSystem.getLocalStart(getBegin());
    }
    /**
     * Fetch the 0-based last coordinate.
     * This is the same as {@link #getEnd(CoordinateSystem)
     * getEnd(ZERO_BASED)}.
     *
     * @return The right-hand (ending) coordinate.
     */
    public abstract long getEnd();
    
    
    /**
     * Fetch the right (end) coordinate using the given 
     * {@link CoordinateSystem}.
     *
     * @return The right-hand (ending) coordinate.
     * @throws NullPointerException if the given {@link CoordinateSystem} is null.
     */
    public long getEnd(CoordinateSystem coordinateSystem) {
    	if(coordinateSystem==null){
    		throw new NullPointerException("CoordinateSystem can not be null");
    	}
        return coordinateSystem.getLocalEnd(getEnd());
    }

    /**
     * Checks if this range is empty (has length of 0).
     * 
     * @return <code>true</code> if the range is empty, <code>false</code>
     * otherwise.
     */
    public boolean isEmpty(){
    	return false;
    }
    /**
     * Checks to see if the this <code>Range</code> is contained within
     * the given <code>Range</code>.  This does not require this <code>Range</code>
     * to be a strict subset of the target.  More precisely: a
     * <code>Range</code> is always a sub-range of itself.
     *
     * @param range The <code>Range</code> to compare to; can not be null.
     * @return <code>true</code> if every value in this <code>Range</code> is
     * found in the given comparison <code>Range</code>.
     * @throws NullPointerException if range is null.
     */
    public boolean isSubRangeOf(Range range) {
        if(range==null){
            throw new NullPointerException("range can not be null");
        }
        return getBegin()>=range.getBegin() && getEnd()<=range.getEnd();
       
    }
    

    /**
     * Checks to see if the given {@link Range} intersects this one.
     * An empty range will never intersect any other range
     * (even itself)
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

        return !(this.getBegin() > target.getEnd() || this.getEnd() < target.getBegin());
    }
    /**
     * Calculates the intersection of this {@link Range} and a second one.
     * 
     * <p>
     * The intersection of an empty Range with any other Range is always the
     * empty Range.
     *
     * @param other The second {@link Range} to compare
     * @return A {@link Range} object spanning only the range of values covered
     * by both this Range and the other {@link Range}.
     */
    public Range intersection(Range other)
    {
        if (other == null){
            throw new IllegalArgumentException("Null Range used in intersection operation.");
        }
        if(isEmpty()){
        	return this;
        }
        if (other.isEmpty()){
        	return other;
        }
        long intersectionStart = Math.max(other.getBegin(), this.getBegin());
		long intersectionEnd = Math.min(other.getEnd(), this.getEnd());
		//this mess is so we don't deal with underflow
		//if start is Long.MIN_VALUE
		long length = intersectionEnd - intersectionStart+1;
		if(length<=-1){
			return new Range.Builder().build();
		}
		//length >=0 can be handled by builder
		//we include length of 0 since 
		//that might have a different start coordinate
			return new Range.Builder(length)
						.shift(intersectionStart)
						.build();
		
    }
    /**
     * Get the List of Ranges that represents the 
     * {@code this - other}.  This is similar to the 
     * Set of all coordinates that don't intersect.
     * @param other the range to complement with.
     * @return
     */
    public List<Range> complement(Range other){
        //this - other
        //anything in this that doesn't intersect with other
        Range intersection = intersection(other);
        if(intersection.isEmpty()){
            return Arrays.asList(this);
        }
        List<Range> complementedRanges = new ArrayList<Range>();
        if(intersection.getBegin()!=Long.MIN_VALUE){
	        Range beforeOther = Range.of(getBegin(), intersection.getBegin()-1);
	        if(!beforeOther.isEmpty()){
	            complementedRanges.add(beforeOther);
	        }
        }
        
        if(intersection.getEnd()!=Long.MAX_VALUE){        	
        	Range afterOther= Range.of(intersection.getEnd()+1, getEnd());
        	if(!afterOther.isEmpty()){
                complementedRanges.add(afterOther);
            }
        }
        
       
        
        return Ranges.merge(complementedRanges);
    }
    
    public List<Range> complementFrom(Collection<Range> ranges){
        List<Range> universe = Ranges.merge(new ArrayList<Range>(ranges));
        List<Range> complements = new ArrayList<Range>(universe.size());
        for(Range range : universe){
            complements.addAll(range.complement(this));
        }
        return Ranges.merge(complements);
    }

    /**
     * Checks to see if this <code>Range</code> starts before the given
     * comparison <code>Range</code>.
     *
     * @param other The other <code>Range</code> to compare to.
     * @return <code>true</code> if the begin coordinate of this
     * <code>Range</code> is less than the  begin coordinate of the
     * other <code>Range</code>.
     * @throws NullPointerException if other is null.
     */
    public boolean startsBefore(Range other)
    {
        if (other == null){
            throw new NullPointerException("Null Range used in range comparison operation.");
        }

        return this.getBegin() < other.getBegin();
    }

    /**
     * Checks to see if this <code>Range</code> ends before the given target.
     *
     * @param other The target <code>Range</code> to check against.
     * @return <code>true</code> if this <code>Range</code> has an end value
     * which occurs before (and not at the same point as) the target
     * <code>Range</code>.
     * @throws NullPointerException if other is null.
     */
    public boolean endsBefore(Range other)
    {
        if (other == null){
            throw new NullPointerException("Null Range used in range comparison operation.");
        }
        
        return this.getEnd() < other.getBegin();
    } 
   
    /**
     * Convenience method that delegates to
     * {@link #toString(CoordinateSystem)} using {@link CoordinateSystem#ZERO_BASED}.
     * 
     * @see #toString(CoordinateSystem)
     * 
     */
    @Override
    public String toString()
    {
        return toString(CoordinateSystem.ZERO_BASED);
    }
    /**
     * Returns a String representation of this Range in given coordinate system.
     * The actual format is {@code [localStart .. localEnd]/systemAbbreviatedName}
     * @throws NullPointerException if coordinateSystem is null.
     */
    public String toString(CoordinateSystem coordinateSystem)
    {
    	if(coordinateSystem ==null){
    		throw new NullPointerException("coordinateSystem can not be null");
    	}
        return String.format("[ %d .. %d ]/%s", 
        		coordinateSystem.getLocalStart(getBegin()) ,
        		coordinateSystem.getLocalEnd(getEnd()),
                coordinateSystem.getAbbreviatedName());
    }
   

    @Override
    public Iterator<Long> iterator() {
        return new RangeIterator(this);
    }
    
   
    /**
     * Splits a Range into a List of possibly several adjacent Range objects
     * where each of the returned ranges has a max length specified.
     * @param maxSplitLength the max length any of the returned split ranges can be.
     * @return a List of split Ranges; never null or empty but may
     * just be a single element if this Range is smaller than the max length
     * specified.
     * @throws IllegalArgumentException if maxSplitLength
     * <1.
     */
    public List<Range> split(long maxSplitLength){
    	if(maxSplitLength <1){
    		throw new IllegalArgumentException("max splitLength must be >= 1");
    	}
    	List<Range> list = new ArrayList<Range>();
        if(getLength()<maxSplitLength){
            list.add(this);
        }else{
	        long currentStart=getBegin();	        
	        long end = getEnd();
			while(currentStart<=end){
	            long endCoordinate = Math.min(end, currentStart+maxSplitLength-1);
	            list.add(Range.of(currentStart, endCoordinate));
	            currentStart = currentStart+maxSplitLength;
	        }
        }
        return list;
    }
   
    /**
     * Get the length of this range.
     * @return the length;
     * will always be >=0.
     */
    public long getLength() {
    	 return getEnd() - getBegin() + 1;
    }
    /**
    * {@inheritDoc} 
    * <p/>
    * Returns this since it is already a Range.
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
            from = range.getBegin();
            to = range.getEnd();
            index = from;
        }
        @Override
        public boolean hasNext() {
        	//have to handle special case where end
        	//coordinate is Long.MAX_VALUE since
        	//all longs are <= MAX value
        	//see Java Puzzlers Puzzle #26 for more info
        	if(to == Long.MAX_VALUE){
        		//if we wrap around to a number
        		//less than our starting point
        		//we know we overflowed
        		//so we have passed MAX_VALUE
        		//(or we are an empty range which
        		//wouldn't have a next anyway)
        		return index >from;
        	}else{
        		return index<=to;
        	}
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
    /**
     * Range implementation that stores the 
     * start and end coordinates as longs.
     * @author dkatzel
     *
     */
    private static final class LongRange extends Range{
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
        
    	private LongRange(long start, long end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (end ^ (end >>> 32));
			result = prime * result + (int) (start ^ (start >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj){
				return true;
			}
			if (obj == null){
				return false;
			}
			if (getClass() != obj.getClass()){
				return false;
			}
			LongRange other = (LongRange) obj;
			if (end != other.end){
				return false;
			}
			if (start != other.start){
				return false;
			}
			return true;
		}
    	
    	
    }
    /**
     * Range implementation that stores the 
     * start and end coordinates as ints.
     * @author dkatzel
     *
     */
    private static final class IntRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final int start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int end;
        
    	private IntRange(int start, int end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj){
				return true;
			}
			if (obj == null){
				return false;
			}
			if (getClass() != obj.getClass()){
				return false;
			}
			IntRange other = (IntRange) obj;
			if (end != other.end){
				return false;
			}
			if (start != other.start){
				return false;
			}
			return true;
		}
    }
    
   
    /**
     * Range implementation that stores the 
     * start and end coordinates as shorts.
     * @author dkatzel
     *
     */
    private static final class ShortRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  short end;
        
    	private ShortRange(short start, short end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			ShortRange other = (ShortRange) obj;
			if (end != other.end) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
   
    /**
     * Range implementation that stores the 
     * start and end coordinates as bytes.
     * @author dkatzel
     *
     */
    private static final class ByteRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  byte end;
        
    	private ByteRange(byte start, byte end){
    		 this.start = start;
	        this.end = end;
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return end;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			ByteRange other = (ByteRange) obj;
			if (end != other.end) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    	

    }
    
    
    
    /**
     * Range implementation that stores the 
     * start coordinates as an unsigned byte
     * and the length as an unsigned short.
     * This is commonly used for next-gen length
     * valid ranges or next-gen reads placed in the beginning
     * of contigs/scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedByteStartShortLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  short length;
        
    	private UnsignedByteStartShortLengthRange(short start, int length){
    		this.start = IOUtil.toSignedByte(start);
	        this.length = IOUtil.toSignedShort(length);
	       
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedByte(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getLength() {
            return IOUtil.toUnsignedShort(length);
        }

    	@Override
    	public long getEnd(){
    		return getBegin() + getLength() -1;
    	}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedByteStartShortLengthRange other = (UnsignedByteStartShortLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
   
    
    /**
     * Range implementation that stores the 
     * start coordinates as an unsigned byte
     * and the length as an unsigned int.
     * This is commonly used for contigs
     * placed in the beginning
     * of scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedByteStartIntLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
    	private UnsignedByteStartIntLengthRange(short start, long length){
    		this.start = IOUtil.toSignedByte(start);
	        this.length = IOUtil.toSignedInt(length);
	       
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedByte(start);
        }

    	@Override
        public long getLength() {
            return IOUtil.toUnsignedInt(length);
        }

    	@Override
    	public long getEnd(){
    		return getBegin() + getLength() -1;
    	}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedByteStartIntLengthRange other = (UnsignedByteStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start coordinates as an unsigned byte
     * and the length as a long.
     * This is commonly used for large contig
     * or scaffold ranges.
     * @author dkatzel
     *
     */
    private static final class UnsignedByteStartLongLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final byte start;

        /**
         * The end coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  long length;
        
    	private UnsignedByteStartLongLengthRange(short start, long length){
    		this.start = IOUtil.toSignedByte(start);
	        this.length = length;
	       
    	}
    	
    	/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedByte(start);
        }

    	@Override
        public long getLength() {
            return length;
        }

    	@Override
    	public long getEnd(){
    		return getBegin() + getLength() -1;
    	}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (length ^ (length >>> 32));
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedByteStartLongLengthRange other = (UnsignedByteStartLongLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
		
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned shorts
     * and the length as an unsigned byte.
     * This is probably the most common read valid
     * range for next-gen sequencing.
     * @author dkatzel
     *
     */
    private static final class UnsignedShortStartShortLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  short length;
        
    	private UnsignedShortStartShortLengthRange(int start, int length){
    		 this.start = IOUtil.toSignedShort(start);
	        this.length = IOUtil.toSignedShort(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedShort(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedShort(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getBegin()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedShortStartShortLengthRange other = (UnsignedShortStartShortLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned shorts
     * and the length as an unsigned byte.
     * This is probably the most common read valid
     * range for next-gen sequencing.
     * @author dkatzel
     *
     */
    private static final class UnsignedShortStartIntLengthRange extends Range{
    

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;
        
    	private UnsignedShortStartIntLengthRange(int start, long length){
    		 this.start = IOUtil.toSignedShort(start);
	        this.length = IOUtil.toSignedInt(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedInt(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedShort(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getBegin()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedShortStartIntLengthRange other = (UnsignedShortStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned shorts
     * and the length as an long.
     * This is often used to placed
     * contigs in scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedShortStartLongLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final short start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  long length;
        
    	private UnsignedShortStartLongLengthRange(int start, long length){
    		 this.start = IOUtil.toSignedShort(start);
	        this.length = length;
    	}
    	
    	@Override
		public long getLength() {
			return length;
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedShort(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getBegin()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (length ^ (length >>> 32));
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedShortStartLongLengthRange other = (UnsignedShortStartLongLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as unsigned int
     * and the length as an unsigned byte.
     * This is often used for placing contigs
     * at the middle/ends of scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedIntStartIntLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final int start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
    	private UnsignedIntStartIntLengthRange(long start, long length){
    		 this.start = IOUtil.toSignedInt(start);
	        this.length = IOUtil.toSignedInt(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedInt(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedInt(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getBegin()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedIntStartIntLengthRange other = (UnsignedIntStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    
    /**
     * Range implementation that stores the 
     * start as an unsigned int
     * and the length as an long.
     * This is often used to placed
     * contigs in scaffolds.
     * @author dkatzel
     *
     */
    private static final class UnsignedIntStartLongLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final int start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  long length;
        
    	private UnsignedIntStartLongLengthRange(long start, long length){
    		 this.start = IOUtil.toSignedInt(start);
	        this.length = length;
    	}
    	
    	@Override
		public long getLength() {
			return length;
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return IOUtil.toUnsignedInt(start);
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getBegin()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (length ^ (length >>> 32));
			result = prime * result + start;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			UnsignedIntStartLongLengthRange other = (UnsignedIntStartLongLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}
    }
    /**
     * Range implementation that stores the 
     * start as signed long
     * and the length as an unsigned byte.
     * This is often used for placing contigs
     * at the middle/ends of scaffolds.
     * @author dkatzel
     *
     */
    private static final class LongStartIntLengthRange extends Range{
        /**
         * The start coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final long start;

        /**
         * The length coordinate.
         * This coordinate stored relative to the zero base coordinate system
         */
        private final  int length;
        
    	private LongStartIntLengthRange(long start, long length){
    		 this.start = start;
	        this.length = IOUtil.toSignedInt(length);
    	}
    	
    	@Override
		public long getLength() {
			return IOUtil.toUnsignedInt(length);
		}

		/**
         * Fetch the left (start) coordinate This is the same as 
         * {@link #getBegin(CoordinateSystem)
         * getStart(ZERO_BASED)}.
         *
         * @return The left-hand (starting) coordinate.
         * 
         */
    	@Override
        public long getBegin() {
            return start;
        }
    	 /**
         * Fetch the 0-based right (end) coordinate.
         * This is the same as {@link #getEnd(CoordinateSystem)
         * getEnd(ZERO_BASED)}.
         *
         * @return The right-hand (ending) coordinate.
         */
    	@Override
        public long getEnd() {
            return getBegin()+getLength()-1;
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + length;
			result = prime * result + (int) (start ^ (start >>> 32));
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			LongStartIntLengthRange other = (LongStartIntLengthRange) obj;
			if (length != other.length) {
				return false;
			}
			if (start != other.start) {
				return false;
			}
			return true;
		}

		
    }
  
    
    
    private static final class EmptyByteRange extends Range{
    	private final byte coordinate;
    	
    	EmptyByteRange(byte coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getBegin() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + coordinate;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyByteRange other = (EmptyByteRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
    	
    }
    
    private static final class EmptyShortRange extends Range{
    	private final short coordinate;
    	
    	EmptyShortRange(short coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getBegin() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + coordinate;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyShortRange other = (EmptyShortRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
    }
    
    private static final class EmptyIntRange extends Range{
    	private final int coordinate;
    	
    	EmptyIntRange(int coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getBegin() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + coordinate;
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyIntRange other = (EmptyIntRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
    }
    
    private static final class EmptyLongRange extends Range{
    	private final long coordinate;
    	
    	EmptyLongRange(long coordinate){
    		this.coordinate = coordinate;
    	}

		@Override
		public long getBegin() {
			return coordinate;
		}

		@Override
		public long getEnd() {
			return coordinate-1;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (coordinate ^ (coordinate >>> 32));
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
			if (getClass() != obj.getClass()) {
				return false;
			}
			EmptyLongRange other = (EmptyLongRange) obj;
			if (coordinate != other.coordinate) {
				return false;
			}
			return true;
		}
		
    }
    
    /**
     * {@code Builder} is a mutable object
     * that allows clients to create a 
     * {@link Range} object using the current
     * specification.  
     * <p/>
     * <strong>Note:</strong> {@link Builder#build()} is not guaranteed to return new instances and may return
     * a cached instance instead (flyweight pattern).
     * <p/>
     * This class is not thread-safe.
     * @author dkatzel
     *
     */
    public static final class Builder {
    	
    	private long begin;
    	private long end;
    	private final CoordinateSystem inputCoordinateSystem;
    	/**
    	 * Create a new Builder instance
    	 * which is initialized to an 
    	 * empty range with a coordinate at the
    	 * origin (zero for {@link CoordinateSystem#ZERO_BASED}).
    	 */
    	public Builder(){
    		this(0);
    	}
    	public Builder(Builder copy){
    		this(copy.inputCoordinateSystem, copy.begin, copy.end);
    	}
    	/**
    	 * Create a new Builder instance
    	 * which is initialized to the given
    	 * begin and end coordinates in zero-based
    	 * coordinate space.
    	 * This is equivalent to 
    	 * {@link Builder#Builder(CoordinateSystem, long, long)
    	 * new Builder(CoordinateSystem.ZERO_BASED, begin,end)}.
    	 * @param begin the initial  inclusive begin coordinate of the range in zero based coordinates. 
    	 * @param end the initial inclusive end coordinate in zero based coordinates. 
    	 * @see Builder#Builder(CoordinateSystem, long, long)
    	 *  @throws IllegalArgumentException if the given
    	 * begin and end coordiantes cause the
    	 * length to be negative.
    	 */
    	public Builder(long begin, long end){
    		this(CoordinateSystem.ZERO_BASED, begin,end);
    	}
    	/**
    	 * Create a new Builder instance
    	 * which is initialized to the given
    	 * begin and end coordinates in the
    	 * given coordinate space.
    	 * @param cs the {@link CoordinateSystem} these coordinates are
    	 * given in; can not be null.
    	 * @param begin the initial  begin coordinate of the range in the given
    	 * {@link CoordinateSystem}. 
    	 * @param end the initial end coordinate of the range in the given
    	 * {@link CoordinateSystem}.  
    	 * @throws NullPointerException if cs is null.
    	 * @throws IllegalArgumentException if the given
    	 * begin and end coordiantes cause the
    	 * length to be negative.
    	 */
    	public Builder(CoordinateSystem cs,long begin, long end){
    		if(cs ==null){
    			throw new NullPointerException("CoordinateSystem can not be null");
    		}
    		assertValidCoordinates(begin,end);
    		this.begin = cs.getStart(begin);
    		this.end = cs.getEnd(end);
    		this.inputCoordinateSystem = cs;
    		
    	}
    	private void assertValidCoordinates(long begin, long end){
    		long length = end-begin+1;
    		if(length<0){
    			throw new IllegalArgumentException("length can not be negative");
    		}
    	}
    	/**
    	 * Create a new Builder instance
    	 * which is initialized to an 
    	 * range with a coordinate at the
    	 * origin (zero for {@link CoordinateSystem#ZERO_BASED})
    	 * and a length of the given length.
    	 * @param length the initial length of the range;
    	 * can not be {@literal <0}.
    	 * @throws IllegalArgumentException if length {@literal <0}.
    	 */
    	public Builder(long length){
    		if(length <0){
    			throw new IllegalArgumentException("must be >=0");
    		}
    		begin=0;
    		end = length-1;
    		inputCoordinateSystem = CoordinateSystem.ZERO_BASED;
    	}
    	/**
    	 * Create a new Builder instance
    	 * which is initialized to have the same
    	 * begin and end coordinates as the given {@link Range}.
    	 * @param range the range to copy;
    	 * can not be null.
    	 */
    	public Builder(Range range){
    		if(range ==null){
    			throw new NullPointerException("range can not be null");
    		}
    		begin=range.getBegin();
    		end = range.getEnd();
    		inputCoordinateSystem = CoordinateSystem.ZERO_BASED;
    	}

    	/**
    	 * Shift the entire range by the given
    	 * amount of units. Both the begin and end coordinates
    	 * will be adjusted by the given value.  The length 
    	 * will remain the same. 
    	 * @param units the amount to shift. If this number
    	 * is positive, then the begin and end coordinates
    	 * will be increased; if this number is negative, 
    	 * then the begin and end coordinates will be decreased.
    	 * A value of 0 will cause no changes.
    	 * @return this.
    	 */
    	public Builder shift(long units){
    		begin+=units;
    		end+=units;
    		return this;
    	}
    	/**
    	 * Shrink the begin value by the given
    	 * amount of units.  This will add the given number
    	 * of  units to the begin coordinate which will also cause
    	 * the range's length to be shrunk by the given amount.
    	 * @param units the amount to shrink to the left. If this number
    	 * is negative, then that is the equivalent of growing
    	 * to the left by the given number of units
    	 * @return this.
    	 * @see #expandBegin(long)
    	 * @throws IllegalArgumentException if shrinking the begin
    	 * coordinate by the given amount causes the range's
    	 * length to be negative.
    	 */
    	public Builder contractBegin(long units){
    		long newBegin = begin+units;
    		assertValidCoordinates(newBegin,end);
    		begin = newBegin;
    		return this;
    	}
    	/**
    	 * Shrink the end value by the given
    	 * amount of units.  This will subtract the given number
    	 * of  units to the end coordinate which will also cause
    	 * the range's length to be shrunk by the given amount.
    	 * @param units the amount to shrink to the right. If this number
    	 * is negative, then that is the equivalent of growing
    	 * to the right by the given number of units
    	 * @return this.
    	 * @see #expandEnd(long)
    	 * @throws IllegalArgumentException if shrinking the end
    	 * coordinate by the given amount causes the range's
    	 * length to be negative.
    	 */
    	public Builder contractEnd(long units){
    		long newEnd = end-units;
    		assertValidCoordinates(begin,newEnd);
    		end -=units;
    		return this;
    	}
    	/**
    	 * Grows the begin value by the given
    	 * amount of units.  This will subtract the given number
    	 * of  units to the begin coordinate which will also cause
    	 * the range's length to be grown by the given amount.
    	 * @param units the amount to grow to the left. If this number
    	 * is negative, then that is the equivalent of shrinking
    	 * to the left by the given number of units
    	 * @return this.
    	 * @see #contractBegin(long)
    	 */
    	public Builder expandBegin(long units){
    		begin-=units;
    		return this;
    	}
    	/**
    	 * Grows the end value by the given
    	 * amount of units.  This will subtract the given number
    	 * of  units to the end coordinate which will also cause
    	 * the range's length to be grown by the given amount.
    	 * @param units the amount to grow to the right. If this number
    	 * is negative, then that is the equivalent of shrinking
    	 * to the right by the given number of units
    	 * @return this.
    	 * @see #contractEnd(long)
    	 */
    	public Builder expandEnd(long units){
    		end +=units;
    		return this;
    	}
    	/**
    	 * Create a copy of this Builder using the current values.
    	 * Any futher modifications to either the original Builder
    	 * or the copy will not affect the other.
    	 * @return a new instance.
    	 */
    	public Builder copy(){
    		return new Builder(this);
    	}
    	/**
    	 * Use the current begin, end and length
    	 * values of this Builder to return an instance
    	 * of a {@link Range} object with the same values.
    	 * This method is not guaranteed to return new instances and may return
	     * a cached instance instead (flyweight pattern).
	     * @return a {@link Range}; never null but might 
	     * not be a new instance.
	     * @throws IllegalArgumentException if {@code end < begin -1} 
	     * or if the resulting range length > {@link Long#MAX_VALUE}.
    	 */
    	public Range build(){
    		long length = end-begin+1;
    		if(length<0){
    			throw new IllegalArgumentException("length can not be negative");
    		}
    		if(begin >0){
        		long maxLength = Long.MAX_VALUE - begin+1;
        		if(maxLength < length){
        			throw new IndexOutOfBoundsException(
        					String.format("given length %d would make range [%d - ? ] beyond max allowed end offset",
        							end, begin));
        		}
        	}
    		
    		final Range range;
            if(end >= begin) {
                range= buildNewRange(begin,end);            
            } else if (end == begin-1) {
                range = buildNewEmptyRange(begin);
            } else {
                throw new IllegalArgumentException(String.format("Range coordinates %d, %d are not valid %s coordinates", 
                		inputCoordinateSystem.getLocalStart(begin), inputCoordinateSystem.getLocalEnd(end), inputCoordinateSystem));
            }
            return getFromCache(range);
    	}
    	
    }
    
}
