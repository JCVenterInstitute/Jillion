/**
 * 
 */
package org.jcvi;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Comparator;

/**
 * A <code>RangeDepartureComparator</code> compares a pair of {@link Range}s
 * and assigns the lower comparative value to the Range which ends earlier.
 * In the case of two ranges having identical end coordinates, the one
 * with the start end coordinate (the longer range) will be ranked lower.
 * Empty ranges are considered lower in comparative value than any non-empty
 * Range.
 * 
 * @author jsitz@jcvi.org
 */
public class RangeDepartureComparator implements Serializable, Comparator<Range> 
{
    /** The Serial Version UID */
    private static final long serialVersionUID = -7860544714796941304L;

    public RangeDepartureComparator() 
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
}
