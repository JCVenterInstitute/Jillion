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
import org.jcvi.sequence.SequenceDirection;

public class VirtualPlacedReadAdapter<T extends PlacedRead> implements VirtualPlacedRead<T>{
    private final T realRead;
    
    public VirtualPlacedReadAdapter(T realRead){
        this.realRead = realRead;
    }
    @Override
    public int getRealIndexOf(int virtualIndex) {
        return virtualIndex;
    }

    @Override
    public T getRealPlacedRead() {
        return realRead;
    }

    @Override
    public Map<Integer, NucleotideGlyph> getSnps() {
        return realRead.getSnps();
    }

    @Override
    public Range getValidRange() {
        return realRead.getValidRange();
    }

    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return realRead.getEncodedGlyphs();
    }

    @Override
    public String getId() {
        return realRead.getId();
    }

    @Override
    public long getLength() {
        return realRead.getLength();
    }

    @Override
    public long getEnd() {
        return realRead.getEnd();
    }

    @Override
    public long getStart() {
        return realRead.getStart();
    }
    @Override
    public int getVirtualIndexOf(int realIndex) {
        return realIndex;
    }
    @Override
    public SequenceDirection getSequenceDirection() {
        return realRead.getSequenceDirection();
    }
    @Override
    public String toString() {
        return "VirtualPlacedReadAdapter [realRead=" + realRead.getId() + "]";
    }
    @Override
    public long convertReferenceIndexToValidRangeIndex(long referenceIndex) {
        return realRead.convertReferenceIndexToValidRangeIndex(referenceIndex);
    }
    @Override
    public long convertValidRangeIndexToReferenceIndex(long validRangeIndex) {
        return realRead.convertValidRangeIndexToReferenceIndex(validRangeIndex);
    }

}
