package org.jcvi;

/**
 * User: aresnick
 * Date: Feb 17, 2010
 * Time: 4:34:03 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public interface RangeCoordinateSystem {

    // get range coordinate system start and end locations
    // from range zero base start and end locations
    long getLocalStart(long start);
    long getLocalEnd(long end);

    // get zero base start and end locations
    // from range coordinate system start and end locations
    long getStart(long localStart);
    long getEnd(long localEnd);

    String getAbbreviatedName();
}
