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
package org.jcvi.assembly;

import java.util.Map;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;


public class DefaultPlacedRead implements PlacedRead {

    private final Read<ReferencedEncodedNucleotideGlyphs> read;
    private final long start;
    private final SequenceDirection sequenceDirection;
    
    
    public DefaultPlacedRead(Read<ReferencedEncodedNucleotideGlyphs> read, long start, SequenceDirection sequenceDirection){
        if(read==null){
            throw new IllegalArgumentException("read can not be null");
        }
        this.read = read;
        this.start= start;
        this.sequenceDirection = sequenceDirection;
    }
    @Override
    public long getLength() {
        return read.getLength();
    }

    @Override
    public long getStart() {
        return start;
    }

    public Read<ReferencedEncodedNucleotideGlyphs> getRead(){
        return read;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + read.hashCode();
        result = prime * result + sequenceDirection.hashCode();
        result = prime * result + (int) (start ^ (start >>> 32));
        return result;
    }
    /**
     * Two PlacedReads are equal if they have the same start value
     * and they have their reads are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj instanceof DefaultPlacedRead){           
            DefaultPlacedRead other = (DefaultPlacedRead) obj;
            return read.equals(other.getRead()) && start== other.getStart() 
            && getSequenceDirection() == other.getSequenceDirection();
        }
        return false;
        
    }
    
    @Override
    public SequenceDirection getSequenceDirection() {
        return sequenceDirection;
    }
    @Override
    public String toString() {
        return "offset = "+ getStart() + " complimented? "+ getSequenceDirection()+"  " + read.toString();
    }
    @Override
    public long getEnd() {
        return getStart()+getLength()-1;
    }

    public Map<Integer, NucleotideGlyph> getSnps(){
        return read.getEncodedGlyphs().getSnps();
    }
    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return read.getEncodedGlyphs();
    }
    @Override
    public String getId() {
        return read.getId();
    }
    @Override
    public Range getValidRange(){
        return read.getEncodedGlyphs().getValidRange();
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
            throw new IllegalArgumentException("reference index refers to index before valid range " + validRangeIndex);
        }
        if(validRangeIndex > getLength()-1){
            throw new IllegalArgumentException("reference index refers to index after valid range");
        }
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedRead o) {
        Range range= Range.buildRange(getStart(), getEnd());
        Range otherRange = Range.buildRange(o.getStart(), o.getEnd());
        return range.compareTo(otherRange);
    }

}
