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
package org.jcvi.common.core.assembly.contig.ace;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedUtil;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class AcePlacedReadAdapter implements AcePlacedRead{

    private final PlacedRead placedRead;
    private final PhdInfo phdInfo;
    private final int ungappedFullLength;
    /**
     * @param placedRead
     */
    public AcePlacedReadAdapter(PlacedRead placedRead,Date phdDate, File traceFile, int ungappedFullLength) {
      this(placedRead,
    		  ConsedUtil.generatePhdInfoFor(traceFile, placedRead.getId(), phdDate),
    		  ungappedFullLength);
    }
    public AcePlacedReadAdapter(PlacedRead placedRead,PhdInfo info, int ungappedFullLength) {
        this.placedRead = placedRead;
        this.phdInfo= info;
        this.ungappedFullLength = ungappedFullLength;
    }
	

    @Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }
    @Override
    public long convertReferenceIndexToValidRangeIndex(long referenceIndex) {
        return placedRead.convertReferenceIndexToValidRangeIndex(referenceIndex);
    }
    @Override
    public long convertValidRangeIndexToReferenceIndex(long validRangeIndex) {
        return placedRead.convertValidRangeIndexToReferenceIndex(validRangeIndex);
    }
    @Override
    public Direction getSequenceDirection() {
        return placedRead.getSequenceDirection();
    }
    @Override
    public Map<Integer, NucleotideGlyph> getSnps() {
        return placedRead.getSnps();
    }
    @Override
    public Range getValidRange() {
        return placedRead.getValidRange();
    }
    @Override
    public NucleotideSequence getEncodedGlyphs() {
        return placedRead.getEncodedGlyphs();
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
    public long getStart() {
        return placedRead.getStart();
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
        return ungappedFullLength;
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
        result = prime * result + ungappedFullLength;
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
        if (ungappedFullLength != other.ungappedFullLength) {
            return false;
        }
        return true;
    }
    
    
}
