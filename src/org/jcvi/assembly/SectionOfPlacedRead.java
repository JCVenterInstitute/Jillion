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
 * Created on Apr 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Map;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class SectionOfPlacedRead<T extends PlacedRead> implements VirtualPlacedRead<T> {
    private String virtualReadId;
    private T actualPlacedRead;
    private Range sectionRange;
    private Range validRange;
    private final int startOffsetIntoRealRead;
    
    public SectionOfPlacedRead(String virtualReadId, T actualPlacedRead, int startOffsetIntoRealRead,Range sectionRange, Range validRange){
        this.virtualReadId = virtualReadId;
        this.actualPlacedRead = actualPlacedRead;
        this.sectionRange = sectionRange;
        this.validRange = validRange;
        this.startOffsetIntoRealRead = startOffsetIntoRealRead;
    }
    @Override
    public int getRealIndexOf(int virtualIndex) {
        return virtualIndex+ startOffsetIntoRealRead;
    }

    @Override
    public int getVirtualIndexOf(int realIndex) {
        return realIndex - startOffsetIntoRealRead;
    }
    
    @Override
    public T getRealPlacedRead() {
        return actualPlacedRead;
    }


    @Override
    public Map<Integer, NucleotideGlyph> getSnps() {
        return actualPlacedRead.getSnps();
    }

    @Override
    public Range getValidRange() {
        return validRange;
    }


    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return actualPlacedRead.getEncodedGlyphs();
    }

    @Override
    public String getId() {
        return virtualReadId;
    }

    @Override
    public long getLength() {
        return sectionRange.size();
    }

    @Override
    public long getEnd() {
        return sectionRange.getEnd();
    }

    @Override
    public long getStart() {
        return sectionRange.getStart();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((actualPlacedRead == null) ? 0 : actualPlacedRead.hashCode());
        result = prime * result
                + ((sectionRange == null) ? 0 : sectionRange.hashCode());
        result = prime * result
                + ((virtualReadId == null) ? 0 : virtualReadId.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SectionOfPlacedRead))
            return false;
        SectionOfPlacedRead other = (SectionOfPlacedRead) obj;
        if (actualPlacedRead == null) {
            if (other.actualPlacedRead != null)
                return false;
        } else if (!actualPlacedRead.equals(other.actualPlacedRead))
            return false;
        if (sectionRange == null) {
            if (other.sectionRange != null)
                return false;
        } else if (!sectionRange.equals(other.sectionRange))
            return false;
        if (virtualReadId == null) {
            if (other.virtualReadId != null)
                return false;
        } else if (!virtualReadId.equals(other.virtualReadId))
            return false;
        return true;
    }
    @Override
    public SequenceDirection getSequenceDirection() {
        return actualPlacedRead.getSequenceDirection();
    }
    @Override
    public long convertReferenceIndexToValidRangeIndex(long referenceIndex) {
        
        long validRangeIndex= referenceIndex - getStart();
        checkValidRange(validRangeIndex);
        return validRangeIndex;
    }
    @Override
    public long convertValidRangeIndexToReferenceIndex(long validRangeIndex) {
        checkValidRange(validRangeIndex);
        return getStart() +validRangeIndex;
    }
    private void checkValidRange(long validRangeIndex) {
        if(validRangeIndex <0){
            throw new IllegalArgumentException("reference index refers to index before valid range");
        }
        if(validRangeIndex > getLength()-1){
            throw new IllegalArgumentException("reference index refers to index after valid range");
        }
    }

}
