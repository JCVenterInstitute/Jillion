/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 *
 *  This file is part of JCVI Java Common
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
package org.jcvi;

/**
 * {@code RangeCoordinateSystem} is a way to specify the coordinate system
 * used by ranges and convert from one coordinate system into another.  
 * This is helpful for being able to work with Ranges which
 * use different coordinate systems.
 * 
 * Conversions are performed by being able to convert any RangeCoordinateSystem
 * "local" coordinates into equivalent zero-based inclusive coordinates.
 * @author aresnick
 * @author dkatzel
 */
public interface RangeCoordinateSystem {

    // get range coordinate system start and end locations
    // from range zero base start and end locations
    /**
     * Get the start coordinate in this system from the 
     * equivalent zero-based start coordinate.
     */
    long getLocalStart(long start);
    /**
     * Get the end coordinate in this system from the 
     * equivalent zero-based end coordinate.
     */
    long getLocalEnd(long end);

    // get zero base start and end locations
    // from range coordinate system start and end locations
    /**
     * Get the zero-based inclusive start coordinate from the 
     * equivalent start coordinate in this system.
     */
    long getStart(long localStart);
    /**
     * Get the zero-based inclusive end coordinate from the 
     * equivalent end coordinate in this system.
     */
    long getEnd(long localEnd);
    /**
     * Get the abbreviated name for this System to be displayed as part of the
     * {@link Range#toString()}.
     * @return the name of the system to appear inside the toString.
     */
    String getAbbreviatedName();
}
