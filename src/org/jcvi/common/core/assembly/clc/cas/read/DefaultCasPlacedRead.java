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
package org.jcvi.common.core.assembly.clc.cas.read;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.DefaultReadInfo;
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;

final class DefaultCasPlacedRead implements CasPlacedRead{

    private final Read<ReferenceMappedNucleotideSequence> read;
    private final Range validRange;
    private final long startOffset;
    private final Direction dir;
    private final int ungappedFullLength;
    private final ReadInfo readInfo;
    public DefaultCasPlacedRead(Read<ReferenceMappedNucleotideSequence> read, long startOffset,Range validRange, 
            Direction dir, int ungappedFullLength){
        if(read==null){
            throw new NullPointerException("read can not be null");
        }
        if(validRange ==null){
            throw new NullPointerException("validRange can not be null");
        }
        if(dir ==null){
            throw new NullPointerException("direction can not be null");
        }
        this.read= read;
        this.validRange = validRange;
        this.startOffset = startOffset;
        this.dir= dir;
        this.ungappedFullLength = ungappedFullLength;
        this.readInfo = new DefaultReadInfo(validRange, ungappedFullLength);
    }
    
    
    @Override
	public ReadInfo getReadInfo() {
		return readInfo;
	}


	@Override
    public int getUngappedFullLength() {
        return ungappedFullLength;
    }


    @Override
    public long getGappedEndOffset() {
        return startOffset+getGappedLength()-1;
    }
    @Override
    public long getGappedLength() {
        return read.getNucleotideSequence().getLength();
    }
    @Override
    public long getGappedStartOffset() {
        return startOffset;
    }
    @Override
    public ReferenceMappedNucleotideSequence getNucleotideSequence() {
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
        
        long validRangeIndex= referenceIndex - getGappedStartOffset();
        checkValidRange(validRangeIndex);
        return validRangeIndex;
    }
    @Override
    public long toReferenceOffset(long validRangeIndex) {
        checkValidRange(validRangeIndex);
        return getGappedStartOffset() +validRangeIndex;
    }
    private void checkValidRange(long validRangeIndex) {
        if(validRangeIndex <0){
            throw new IllegalArgumentException("reference index refers to index before valid range");
        }
        if(validRangeIndex > getGappedLength()-1){
            throw new IllegalArgumentException("reference index refers to index after valid range");
        }
    }
    @Override
    public Direction getDirection() {
        return dir;
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
        if (!read.equals(other.read)){
            return false;
        }
        if (startOffset != other.startOffset){
            return false;
        }
        if (dir != other.dir){
            return false;
        }
        if (!validRange.equals(other.validRange)){
            return false;
        }
        return true;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return getGappedContigRange();
    }

    @Override
	public Range getGappedContigRange() {
		return Range.create(getGappedStartOffset(), getGappedEndOffset());
	}  
    
   
}
