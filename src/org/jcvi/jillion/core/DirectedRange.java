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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.Range.CoordinateSystem;


/**
 * {@code DirectedRange} is a composite object that associates
 * a {@link Range} with a {@link Direction}.
 * 
 * @author dkatzel
 *
 *
 */
public final class DirectedRange implements Rangeable, Serializable{
    

	private static final long serialVersionUID = 6483275803274837762L;
	/**
     * Regular expression in the form (left) .. (right).
     */
    private static Pattern DOT_PATTERN = Pattern.compile("(-?\\d+)\\s*\\.\\.\\s*(-?\\d+)");
    /**
     * Regular expression in the form (left) - (right).
     */
    private static Pattern DASH_PATTERN = Pattern.compile("(-?\\d+)\\s*-\\s*(-?\\d+)");
    private static Pattern COMMA_PATTERN = Pattern.compile("(-?\\d+)\\s*,\\s*(-?\\d+)");

    private final Range range;
    private final Direction direction;
    /**
     * Parse a range given as a string using the given {@link CoordinateSystem}
     * and determine the direction by the orientation of the start and 
     * end coordinates. 
     * <p>
     * Current supported String formats are :
     * <ul>
     * <li>start - end</li>
     * <li>end - start</li>
     * <li>start , end</li>
     * <li>end , start</li>
     * <li>start .. end</li>
     * <li>end .. start</li>
     * </ul>
     * @param rangeAsString the range; can not be null.
     * @param coordinateSystem the coordinate system to use; can not be null.
     * @return a new {@link DirectedRange}.
     * @throws NullPointerException if either input is null.
     * @throws IllegalArgumentException if given string does not conform
     * to the allowed formats.
     */
    public static DirectedRange parse(String rangeAsString, CoordinateSystem coordinateSystem){
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
     * Create a new DirectedRange object for the given range
     * in the forward direction.
     * @param range the range to use; can not be null.
     * @return a new DirectedRange will never be null.
     * @throws NullPointerException if range is null.
     */
    public static DirectedRange create(Range range){
        return create(range,Direction.FORWARD);
    }
    /**
     * Create a new DirectedRange object for the given range
     * in the given direction.
     * @param range the range to use; can not be null.
     * @param direction the direction to use; can not be null.
     * @return a new DirectedRange will never be null.
     * @throws NullPointerException if either range or direction is null.
     */
    public static DirectedRange create(Range range, Direction direction){
        return new DirectedRange(range,direction);
    }

    public static DirectedRange of(long start, long end, Direction direction){
        return create(Range.of(start,end), direction);
    }

    private static DirectedRange convertIntoRange(Matcher dashMatcher, CoordinateSystem coordinateSystem) {
        long first = Long.parseLong(dashMatcher.group(1));
        long second = Long.parseLong(dashMatcher.group(2));
        return convertIntoRange(coordinateSystem, first, second);
                
    }

    private static DirectedRange convertIntoRange(
            CoordinateSystem coordinateSystem, long first, long second) {
        if(first<second){
            Range range =Range.of(coordinateSystem,first,second);
            return new DirectedRange(range, Direction.FORWARD);
        }
        Range range =Range.of(coordinateSystem,second,first);
        return new DirectedRange(range, Direction.REVERSE);
    }
    public static DirectedRange parse(String rangeAsString){
        return parse(rangeAsString,CoordinateSystem.ZERO_BASED);
    }
    public static DirectedRange parse(long firstCoord, long secondCoord){
        return convertIntoRange(CoordinateSystem.ZERO_BASED, firstCoord, secondCoord);
    }
    public static DirectedRange parse(long firstCoord, long secondCoord, CoordinateSystem coordinateSystem){
        return convertIntoRange(coordinateSystem, firstCoord, secondCoord);
    }
    public static DirectedRange parse(String firstCoord, String secondCoord){
        return parse(firstCoord,secondCoord,CoordinateSystem.ZERO_BASED);
    }
    public static DirectedRange parse(String firstCoord, String secondCoord, CoordinateSystem coordinateSystem){
        long first = Long.parseLong(firstCoord);
        long second = Long.parseLong(secondCoord);
        return convertIntoRange(coordinateSystem, first, second);
    }
    
    private DirectedRange(Range range, Direction direction) {
        if(range ==null){
            throw new NullPointerException("range can not be null");
        }
        if(direction ==null){
            throw new NullPointerException("direction can not be null");
        }
        this.range = range;
        this.direction = direction;
    }

    /**
     * @return the range
     */
    public Range getRange() {
        return range;
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + direction.hashCode();
        result = prime * result + range.hashCode();
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DirectedRange)) {
            return false;
        }
        DirectedRange other = (DirectedRange) obj;
        if (direction != other.direction) {
            return false;
        }
        if (!range.equals(other.range)) {
            return false;
        }
        return true;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public String toString() {
        return "DirectedRange [range=" + range + ", direction=" + direction
                + "]";
    }

   
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return getRange();
    }

    
        
}
