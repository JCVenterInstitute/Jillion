/**
 * 
 */
package org.jcvi;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Comparator;

/**
 * A <code>RangeArrivalComparator</code> compares a pair of {@link Range}s
 * and assigns the lower comparative value to the Range which begins earlier.
 * In the case of two ranges having identical start coordinates, the one
 * with the lower end coordinate (the shorter range) will be ranked lower.
 * Empty ranges are considered lower in comparative value than any non-empty
 * Range.
 * 
 * @author jsitz@jcvi.org
 */
public class RangeArrivalComparator implements Serializable, Comparator<Range> 
{
    /** The Serial Version UID */
    private static final long serialVersionUID = -5137203626973213666L;

    public RangeArrivalComparator() 
    {
        super();
    }
    
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Range first, Range second) 
    {
        /*
         * We don't accept null values for comparison.
         */
        if (first == null) throw new InvalidParameterException("The first parameter in the comparison is null.");
        if (second == null) throw new InvalidParameterException("The second parameter in the comparison is null.");

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
}
