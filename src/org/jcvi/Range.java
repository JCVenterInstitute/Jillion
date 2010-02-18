/*
 * Created on Jul 18, 2007
 *
 * @author dkatzel
 */
package org.jcvi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.assembly.Placed;


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
 * you need to explicitly use an {@link EmptyRange}.
 *
 * @see EmptyRange
 * @author jsitz
 * @author dkatzel
 */
public class Range implements Placed,Iterable<Long>, Comparable<Range>
{
    /**
     * Enumeration of available range coordinate systems
     */
    public enum CoordinateSystem {
        ZERO_BASED("Zero Based", "0B"),
        RESIDUE_BASED("Residue Based", "RB"),
        SPACE_BASED("Space Based", "SB");

        /** The full name used to display this coordinate system. */
        private String displayName;
        
        /** An abbreviated name to use as a printable <code>Range</code> annotation. */
        private String abbreviatedName;
        
        /**
         * Builds a <code>CoordinateSystem</code>.
         * 
         * @param displayName The full name used to display this coordinate system.
         * @param abbreviatedName An abbreviated name to use as a printable <code>Range</code> 
         * annotation.
         */
        private CoordinateSystem(String displayName, String abbreviatedName) 
        {
            this.displayName = displayName;
            this.abbreviatedName = abbreviatedName;
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
    }
    private static final Map<CoordinateSystem, CoordinateConverter> SYSTEM_TO_CONVERTER_MAP;
    /**
     * Regular expression in the form (left) .. (right).
     */
    private static Pattern DOT_PATTERN = Pattern.compile("(\\d+)\\s*\\.\\.\\s*(\\d+)");
    /**
     * Regular expression in the form (left) - (right).
     */
    private static Pattern DASH_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
    private static Pattern COMMA_PATTERN = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");
    
    
    private static final Comparator<Range> DEFAULT_COMPARATOR = new RangeArrivalComparator();
    
    static{
        SYSTEM_TO_CONVERTER_MAP = new EnumMap<CoordinateSystem, CoordinateConverter>(CoordinateSystem.class);
        SYSTEM_TO_CONVERTER_MAP.put(CoordinateSystem.RESIDUE_BASED, ResidueBasedConverter.getInstance());
        SYSTEM_TO_CONVERTER_MAP.put(CoordinateSystem.SPACE_BASED, SpaceBasedConverter.getInstance());
        SYSTEM_TO_CONVERTER_MAP.put(CoordinateSystem.ZERO_BASED, ZeroBasedConverter.getInstance());
        
    }
    /**
     * Factory method to build a {@link Range} object.
     * if end == start -1 then this method will return an {@link EmptyRange}.
     * @param start start coordinate inclusive.
     * @param end end coordinate inclusive.
     * @return a {@link Range}.
     * @throws IllegalArgumentException if <code>end &lt; start -1</code>
     */
    public static Range buildRange(long start, long end){
        return buildRange(CoordinateSystem.ZERO_BASED,start,end);
    }

    public static Range buildRange(CoordinateSystem coordinateSystem,long start, long end){
        if ( coordinateSystem == null ) {
            throw new IllegalArgumentException("Cannot build null coordinate system range");
        }

        CoordinateConverter converter = SYSTEM_TO_CONVERTER_MAP.get(coordinateSystem);
        if(converter == null){
            throw new IllegalArgumentException("Do not know how to build a " + coordinateSystem + " range");
        }
        long zeroBasedStart = converter.getStart(start);
        long zeroBasedEnd = converter.getEnd(end);
        


        if(zeroBasedEnd >= zeroBasedStart) {
            return new Range(zeroBasedStart,zeroBasedEnd,converter);
        } else if (zeroBasedEnd == zeroBasedStart-1) {
            return buildEmptyRange(zeroBasedStart,zeroBasedEnd,converter);
        } else {
            throw new IllegalArgumentException("Range coordinates" + start + "," + end
                + " are not valid " + coordinateSystem + " coordinates");
        }
    }
    public Range copy(){
        return buildRange(this.getCoordinateSystem(),this.getLocalStart(),this.getLocalEnd());
    }
    public Range convertRange(CoordinateSystem coordinateSystem) {
        if ( coordinateSystem == null ) {
            throw new IllegalArgumentException("Cannot convert to a null coordinate system");
        }

        CoordinateConverter converter = SYSTEM_TO_CONVERTER_MAP.get(coordinateSystem);
        if(converter == null){
            throw new IllegalArgumentException("Do not know how to convert to a " + coordinateSystem + " range");
        }

        if ( this.isEmpty() ) {
            return new EmptyRange(this.getStart(),this.getEnd(),converter);
        } 
        return new Range(this.getStart(),this.getEnd(),converter);
    }
    public static Range buildEmptyRange(){
        return buildEmptyRange(0);
    }
    public static Range buildEmptyRange(long coordinate){
        return buildEmptyRange(Range.CoordinateSystem.ZERO_BASED,coordinate);
    }
    public static Range buildEmptyRange(CoordinateSystem coordinateSystem,long coordinate){
        if ( coordinateSystem == null ) {
            throw new IllegalArgumentException("Cannot build null coordinate system range");
        }
        
        CoordinateConverter converter = SYSTEM_TO_CONVERTER_MAP.get(coordinateSystem);
        if(converter == null){
            throw new IllegalArgumentException("Do not know how create a " + coordinateSystem + " empty range");
        }
        long zeroBasedStart = converter.getStart(coordinate);      

        return buildEmptyRange(zeroBasedStart,zeroBasedStart-1,converter);
    }
    private static Range buildEmptyRange(long start,long end,CoordinateConverter converter) {
        return new EmptyRange(start,end,converter);
    }
    public static Range buildRangeOfLength(long start, long length){
        return buildRangeOfLength(CoordinateSystem.ZERO_BASED, start, length);
    }
    public static Range buildRangeOfLength(CoordinateSystem system,long start, long length){
        return buildRange(system,start, start+length-1);
    }
    public static Range buildRangeOfLengthFromEndCoordinate(long end, long rangeSize){
        return Range.buildRange(end-rangeSize+1,end);
    }
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
     * @param ranges
     * @return
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

    /**
     * Object used to convert zero base coordinate system values to
     * the appropriate coordinate system values
     */
    private final CoordinateConverter coordinateConverter;

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
    private Range(long start, long end, CoordinateConverter coordinateConverter) {
        this.start = start;
        this.end = end;
        this.coordinateConverter = coordinateConverter;
    }

    private Range(Range range){
        this.start = range.getStart();
        this.end = range.getEnd();
        this.coordinateConverter = range.coordinateConverter;
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
        return coordinateConverter.getCoordiateSystem();
    }

    public long getLocalStart() {
        return coordinateConverter.getLocalStart(start);
    }

    public long getLocalEnd() {
        return coordinateConverter.getLocalEnd(end);
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
        return Range.buildRangeOfLength(this.start-units, this.size());
    }
    /**
     * Create a new Range of the same size
     * but shifted to the right the specified number of units.
     * @param units number of units to shift
     * @return a new Range (not null)
     */
    public Range shiftRight(long units){
        return Range.buildRangeOfLength(this.start+units, this.size());
    }
    /**
     * Checks if this range is empty.
     *
     * @return <code>true</code> if the range is empty, <code>false</code>
     * otherwise.
     */
    public boolean isEmpty()
    {
        return false;
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

        if (target.isEmpty())
        {
            /*
             * Instead of defining empty set semantics here, we do it in the
             * EmptyRange class
             * -jsitz
             */
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

        if (target.isEmpty())
        {
            return false;
        }

        return this.getEnd() < target.getStart();
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

        if (target.isEmpty())
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "[ " + this.getLocalStart() + " - " + this.getLocalEnd() + " ]/"
            + this.getCoordinateSystem().getAbbreviatedName();
    }
  
    
    /**
     * The <code>EmptyRange</code> is a special case of the {@link Range} class.
     * It signifies the case where the range must be empty and have a size of 0.
     * Because the normal {@link Range} class defines an explicitly inclusive
     * range, it is difficult to select a start/stop pair which behaves
     * suitably and in an easy-to-understand manner.
     * <p>
     * Though it can be treated just like any other {@link Range} object, the
     * <code>EmptyRange</code> class is implemented as a singleton.  All instances
     * of the <code>EmptyRange</code> are comparably and referentially equal.
     * <p>
     * A number of {@link Range} comparison and modification routines will
     * delegate to methods in this class when working with <code>EmptyRange</code>
     * objects.  This gathers all of the special-case handling of things like
     * intersections and unions in a single code location.
     *
     * @author jsitz
     * @author dkatzel
     */
    private static final class EmptyRange extends Range
    {

        /**
         * Creates a new <code>EmptyRange</code>.
         */
        private EmptyRange(long start,long end,CoordinateConverter coordinateConverter){
            super(start,end,coordinateConverter);
        }

        /**
         * {@inheritDoc}
         * <p>
         * The <code>EmptyRange</code> never ends before any other {@link Range}.
         *
         * @return <code>false</code>.
         * @see Range#endsBefore(Range)
         */
        @Override
        public boolean endsBefore(Range target)
        {
            return false;
        }

        /**
         * {@inheritDoc}
         * <p>
         * The intersection of the <code>EmptyRange</code> and any other
         * {@link Range} is always the <code>EmptyRange</code>.
         *
         * @return The <code>EmptyRange</code>.
         * @see Range#intersection(Range)
         */
        @Override
        public Range intersection(Range target)
        {
            return this;
        }

        /**
         * {@inheritDoc}
         * <p>
         * The <code>EmptyRange</code> does not intersect any other {@link Range}.
         *
         * @return <code>false</code>
         * @see Range#intersects(Range)
         */
        @Override
        public boolean intersects(Range target)
        {
            return false;
        }

        /**
         * {@inheritDoc}
         * <p>
         * The <code>EmptyRange</code> is always empty.
         *
         * @return <code>true</code>
         * @see Range#isEmpty()
         */
        @Override
        public boolean isEmpty()
        {
            return true;
        }

        /**
         * {@inheritDoc}
         * <p>
         * The <code>EmptyRange</code> is only a subrange of itself.
         *
         * @return <code>true</code> if the comparison range is also the
         * <code>EmptyRange</code>, otherwise <code>false</code>.
         * @see Range#isSubRangeOf(Range)
         */
        @Override
        public boolean isSubRangeOf(Range range)
        {
            /* Only empty ranges are subranges of the empty range */
            if (range.isEmpty())
            {
                return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         * <p>
         * The <code>EmptyRange</code> always has a size of 0.
         *
         * @return <code>0</code>
         * @see Range#size()
         */
        @Override
        public long size()
        {
            return 0;
        }

        /**
         * {@inheritDoc}
         * <p>
         * The <code>EmptyRange</code> never starts before any other {@link Range}.
         *
         * @return <code>false</code>
         * @see Range#startsBefore(Range)
         */
        @Override
        public boolean startsBefore(Range target)
        {
            return false;
        }

        /**
         * {@inheritDoc}
         * <p>
         * The union of any {@link Range} and the <code>EmptyRange</code> is
         * always equal to just the other {@link Range}.
         *
         * @return An array of {@link Range}s containing the other {@link Range}.
         * @see Range#union(Range)
         */
        @Override
        public Range[] union(Range target)
        {
            return new Range[]{ target };
        }

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
        return mergeRanges(rangesToMerge,0);
    }
    /**
     * Combine the given Ranges into fewer ranges that cover the same region.
     * For example 2 ranges [0-2] and [1-4] could be merged into a single
     * range [0-4].
     * @param rangesToMerge the ranges to be merged together.
     * @param clusterDistance the maximum distance between the end of one range
     * and the start of another inorder
     * to be merged.
     * @return a new list of merged Ranges.
     * @throws IllegalArgumentException if clusterDistance <0.
     */
    public static List<Range> mergeRanges(List<Range> rangesToMerge, int clusterDistance){
        if(clusterDistance <0){
            throw new IllegalArgumentException("cluster distance can not be negative");
        }
        List<Range> sortedCopy = new ArrayList<Range>(rangesToMerge);
        Collections.sort(sortedCopy);

        mergeAnyRangesThatCanBeCombined(sortedCopy, clusterDistance);
        return sortedCopy;
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
    @Override
    public int compareTo(Range that) 
    {
        return Range.DEFAULT_COMPARATOR.compare(this, that);
    }    

    public interface CoordinateConverter {
        public long getLocalStart(long start);
        public long getLocalEnd(long end);
        public long getStart(long localStart);
        public long getEnd(long localEnd);
        public Range.CoordinateSystem getCoordiateSystem();
    }

    private static class ZeroBasedConverter implements CoordinateConverter {
        private static final ZeroBasedConverter instance = new ZeroBasedConverter();

        private ZeroBasedConverter() {}

        public static ZeroBasedConverter getInstance() {
            return instance;
        }

        public long getLocalStart(long start) {
            return start;
        }

        public long getLocalEnd(long end) {
            return end;
        }

        public CoordinateSystem getCoordiateSystem() {
            return CoordinateSystem.ZERO_BASED;
        }

        @Override
        public long getStart(long localStart) {
            return localStart;
        }

        @Override
        public long getEnd(long localEnd) {
            return localEnd;
        }
    }

    private static class SpaceBasedConverter implements CoordinateConverter {
        private static final SpaceBasedConverter instance = new SpaceBasedConverter();

        private SpaceBasedConverter() {}

        public static SpaceBasedConverter getInstance() {
            return instance;
        }

        public long getLocalStart(long start) {
            return start;
        }

        public long getLocalEnd(long end) {
            return end+1;
        }

        public CoordinateSystem getCoordiateSystem() {
            return CoordinateSystem.SPACE_BASED;
        }

        @Override
        public long getStart(long localStart) {
            return localStart;
        }

        @Override
        public long getEnd(long localEnd) {
            return localEnd-1;
        }
    }

    private static class ResidueBasedConverter implements CoordinateConverter {
        private static final ResidueBasedConverter instance = new ResidueBasedConverter();

        private ResidueBasedConverter() {}

        public static ResidueBasedConverter getInstance() {
            return instance;
        }
        
        public long getLocalStart(long start) {
            return start+1;
        }

        public long getLocalEnd(long end) {
            return end+1;
        }

        public CoordinateSystem getCoordiateSystem() {
            return CoordinateSystem.RESIDUE_BASED;
        }

        @Override
        public long getStart(long localStart) {
            return localStart-1;
        }

        @Override
        public long getEnd(long localEnd) {
            return localEnd-1;
        }
    }

    @Override
    public long getLength() {
        return size();
    }
}
