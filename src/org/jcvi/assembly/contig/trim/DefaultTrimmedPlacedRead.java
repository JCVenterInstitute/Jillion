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
 * Created on Oct 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;

public class DefaultTrimmedPlacedRead<T extends PlacedRead> implements TrimmedPlacedRead<T> {

    private final Range newTrimRange;
    private final T read;
    
    
    /**
     * @param read
     * @param newTrimRange
     */
    public DefaultTrimmedPlacedRead(T read, Range newTrimRange) {
        this.read = read;
        this.newTrimRange = newTrimRange;
    }

    @Override
    public Range getNewTrimRange() {
        return newTrimRange;
    }

    @Override
    public T getRead() {
        return read;
    }

    @Override
    public String toString() {
        return "DefaultTrimmedPlacedRead [read=" + read + ", newTrimRange="
                + newTrimRange + "]";
    }
    
    

}
