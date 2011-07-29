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
 * Created on Sep 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core;


/**
 * {@code Placed} is an interface to be used by any object
 * that has been placed at a particular location along
 * some axis.
 * @author dkatzel
 *
 *
 */
public interface Placed<P extends Placed> extends Comparable<P>{
    /**
     * Get the start coordinate of this placed object
     * on the placed axis.
     * @return the start as a long.
     */
    long getStart();
    /**
     * Get the end coordinate of this placed object
     * on the placed axis.
     * @return the end as a long.
     */
    long getEnd();
    /**
     * Get the length of this placed object
     * on the axis.
     * @return the length of this placed object.
     */
    long getLength();
    /**
     * Convert this Placed instance into a {@link Range}
     * object with the same location values.
     * @return a Range that is the same as
     * this location, never null.
     */
    Range asRange();
    
}
