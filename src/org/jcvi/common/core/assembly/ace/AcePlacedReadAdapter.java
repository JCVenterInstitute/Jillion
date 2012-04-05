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
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class AcePlacedReadAdapter implements AcePlacedRead{

    private final PlacedRead placedRead;
    private final PhdInfo phdInfo;
    /**
     * @param placedRead
     */
    public AcePlacedReadAdapter(PlacedRead placedRead,Date phdDate, File traceFile) {
      this(placedRead,
    		  ConsedUtil.generatePhdInfoFor(traceFile, placedRead.getId(), phdDate));
    }
    public AcePlacedReadAdapter(PlacedRead placedRead,PhdInfo info) {
        this.placedRead = placedRead;
        this.phdInfo= info;
    }
	

    @Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }
    @Override
    public long toGappedValidRangeOffset(long referenceIndex) {
        return placedRead.toGappedValidRangeOffset(referenceIndex);
    }
    @Override
    public long toReferenceOffset(long validRangeIndex) {
        return placedRead.toReferenceOffset(validRangeIndex);
    }
    @Override
    public Direction getDirection() {
        return placedRead.getDirection();
    }
    @Override
    public Map<Integer, Nucleotide> getSnps() {
        return placedRead.getSnps();
    }
    @Override
    public Range getValidRange() {
        return placedRead.getValidRange();
    }
    @Override
    public NucleotideSequence getNucleotideSequence() {
        return placedRead.getNucleotideSequence();
    }
    @Override
    public String getId() {
        return placedRead.getId();
    }
    @Override
    public long getLength() {
        return placedRead.getLength();
    }
    @Override
    public long getEnd() {
        return placedRead.getEnd();
    }
    @Override
    public long getBegin() {
        return placedRead.getBegin();
    }
    @Override
    public String toString() {
        return "AcePlacedReadAdapter [placedRead="
                + placedRead + ", phdInfo=" + phdInfo +  "]";
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedRead o) {
        return placedRead.compareTo(o);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedFullLength() {
        return placedRead.getUngappedFullLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return placedRead.asRange();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((phdInfo == null) ? 0 : phdInfo.hashCode());
        result = prime * result
                + ((placedRead == null) ? 0 : placedRead.hashCode());
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
        if (!(obj instanceof AcePlacedReadAdapter)) {
            return false;
        }
        AcePlacedReadAdapter other = (AcePlacedReadAdapter) obj;
        if (phdInfo == null) {
            if (other.phdInfo != null) {
                return false;
            }
        } else if (!phdInfo.equals(other.phdInfo)) {
            return false;
        }
        if (placedRead == null) {
            if (other.placedRead != null) {
                return false;
            }
        } else if (!placedRead.equals(other.placedRead)) {
            return false;
        }
        return true;
    }
    
  
    
}
