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
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.cas.read;

import java.util.Collections;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class DefaultCasPlacedRead implements CasPlacedRead{

    private final Read read;
    private final Range validRange;
    private final long startOffset;
    private final Direction dir;
    private final int ungappedFullLength;
    public DefaultCasPlacedRead(Read read, long startOffset,Range validRange, 
            Direction dir, int ungappedFullLength){
        this.read= read;
        this.validRange = validRange;
        this.startOffset = startOffset;
        this.dir= dir;
        this.ungappedFullLength = ungappedFullLength;
    }
    
    
    @Override
    public int getUngappedFullLength() {
        return ungappedFullLength;
    }


    @Override
    public long getEnd() {
        return startOffset+getLength()-1;
    }
    @Override
    public long getLength() {
        return read.getLength();
    }
    @Override
    public long getStart() {
        return startOffset;
    }
    @Override
    public NucleotideSequence getNucleotideSequence() {
        return read.getNucleotideSequence();
    }
    @Override
    public String getId() {
        return read.getId();
    }
    @Override
    public String toString() {
        return "DefaultCasPlacedRead [startOffset=" + startOffset
                + ", validRange=" + validRange + ", dir=" + dir + ", read="
                + read + "]";
    }
    @Override
    public long toGappedValidRangeOffset(long referenceIndex) {
        
        long validRangeIndex= referenceIndex - getStart();
        checkValidRange(validRangeIndex);
        return validRangeIndex;
    }
    @Override
    public long toReferenceOffset(long validRangeIndex) {
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
    @Override
    public Direction getDirection() {
        return dir;
    }
    @Override
    public Map<Integer, Nucleotide> getSnps() {
        return Collections.emptyMap();
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((read == null) ? 0 : read.hashCode());
        result = prime * result + (int) (startOffset ^ (startOffset >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof DefaultCasPlacedRead)){
            return false;            
        }
        DefaultCasPlacedRead other = (DefaultCasPlacedRead) obj;
        if (read == null) {
            if (other.read != null){
                return false;
            }
        } else if (!read.equals(other.read)){
            return false;
        }
        if (startOffset != other.startOffset){
            return false;
        }
        return true;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedRead o) {
        return asRange().compareTo(o.asRange());       
    }


    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return Range.buildRange(getStart(), getEnd());
    }
    private boolean isAGap(int gappedValidRangeIndex) {
        return getNucleotideSequence().getGapIndexes().contains(Integer.valueOf(gappedValidRangeIndex));
    }
    @Override
    public int convertGappedValidRangeIndexToUngappedValidRangeIndex(
            int gappedValidRangeIndex) {
        
        if(isAGap(gappedValidRangeIndex)){
            //we are given a gap
            //which we can't convert into an ungapped index
            throw new IllegalArgumentException(gappedValidRangeIndex + " is a gap");
        }
        int numberOfGaps = getNucleotideSequence().getNumberOfGapsUntil(gappedValidRangeIndex);
        return gappedValidRangeIndex-numberOfGaps;
    }

    @Override
    public Range convertGappedValidRangeToUngappedValidRange(
            Range gappedValidRange) {
       return Range.buildRange(
               convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       AssemblyUtil.getLeftFlankingNonGapIndex(getNucleotideSequence(),(int)gappedValidRange.getStart())),
               convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       AssemblyUtil.getLeftFlankingNonGapIndex(getNucleotideSequence(), (int)gappedValidRange.getEnd()))
                
        );
    }

    @Override
    public Range convertUngappedValidRangeToGappedValidRange(
            Range ungappedValidRange) {
        return  Range.buildRange(
                convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedValidRange.getStart()),
                convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedValidRange.getEnd()));
                
    }
    
    @Override
    public int convertUngappedValidRangeIndexToGappedValidRangeIndex(
            int ungappedValidRangeIndex) {
        NucleotideSequence nucleotideSequence = getNucleotideSequence();
        int numberOfGaps = nucleotideSequence.getNumberOfGapsUntil(nucleotideSequence.getGappedOffsetFor(ungappedValidRangeIndex));
        return ungappedValidRangeIndex+numberOfGaps;
    }
}
