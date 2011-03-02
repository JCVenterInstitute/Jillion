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

package org.jcvi.assembly.trim.lucy;

import org.jcvi.Range;

/**
 * @author dkatzel
 *
 *
 */
public class LucyDebugTrimRecord {
  
    private final String id;
    /**
     * CLR
     */
    private final Range clearRange;
    /**
     * CLB
     */
    private final Range clearBadQualityRange;
    /**
     * CLV
     */
    private final Range clearVectorRange;
    /**
     * @param id
     * @param clearRange
     * @param clearBadQualityRange
     * @param clearVectorRange
     */
    public LucyDebugTrimRecord(String id, Range clearRange,
            Range clearBadQualityRange, Range clearVectorRange) {
        this.id = id;
        this.clearRange = clearRange;
        this.clearBadQualityRange = clearBadQualityRange;
        this.clearVectorRange = clearVectorRange;
    }
    public String getId() {
        return id;
    }
    public Range getClearRange() {
        return clearRange;
    }
    public Range getClearBadQualityRange() {
        return clearBadQualityRange;
    }
    public Range getClearVectorRange() {
        return clearVectorRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((clearBadQualityRange == null) ? 0 : clearBadQualityRange
                        .hashCode());
        result = prime * result
                + ((clearRange == null) ? 0 : clearRange.hashCode());
        result = prime
                * result
                + ((clearVectorRange == null) ? 0 : clearVectorRange.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (!(obj instanceof LucyDebugTrimRecord)) {
            return false;
        }
        LucyDebugTrimRecord other = (LucyDebugTrimRecord) obj;
        if (clearBadQualityRange == null) {
            if (other.clearBadQualityRange != null) {
                return false;
            }
        } else if (!clearBadQualityRange.equals(other.clearBadQualityRange)) {
            return false;
        }
        if (clearRange == null) {
            if (other.clearRange != null) {
                return false;
            }
        } else if (!clearRange.equals(other.clearRange)) {
            return false;
        }
        if (clearVectorRange == null) {
            if (other.clearVectorRange != null) {
                return false;
            }
        } else if (!clearVectorRange.equals(other.clearVectorRange)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
