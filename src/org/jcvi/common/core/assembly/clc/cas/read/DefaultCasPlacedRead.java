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
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;

final class DefaultCasPlacedRead implements CasPlacedRead{

    private final long startOffset;
    private final Direction dir;
    private final ReadInfo readInfo;
    private final ReferenceMappedNucleotideSequence sequence;
    private final String id;
    public DefaultCasPlacedRead(String id, ReferenceMappedNucleotideSequence sequence, long startOffset,Range validRange, 
            Direction dir, int ungappedFullLength){
        if(id==null){
            throw new NullPointerException("id can not be null");
        }
        if(sequence==null){
            throw new NullPointerException("sequence can not be null");
        }
        if(validRange ==null){
            throw new NullPointerException("validRange can not be null");
        }
        if(dir ==null){
            throw new NullPointerException("direction can not be null");
        }
        this.id= id;
        this.sequence = sequence;
        this.startOffset = startOffset;
        this.dir= dir;
        this.readInfo = new ReadInfo(validRange, ungappedFullLength);
    }
    
    
    @Override
	public ReadInfo getReadInfo() {
		return readInfo;
	}


	@Override
    public int getUngappedFullLength() {
        return readInfo.getUngappedFullLength();
    }


    @Override
    public long getGappedEndOffset() {
        return startOffset+getGappedLength()-1;
    }
    @Override
    public long getGappedLength() {
        return sequence.getLength();
    }
    @Override
    public long getGappedStartOffset() {
        return startOffset;
    }
    @Override
    public ReferenceMappedNucleotideSequence getNucleotideSequence() {
        return sequence;
    }
    @Override
    public String getId() {
        return id;
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


	@Override
	public String toString() {
		return "DefaultCasPlacedRead [id=" + id + ", startOffset="
				+ startOffset + ", dir=" + dir + ", readInfo=" + readInfo
				+ ", sequence=" + sequence + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((readInfo == null) ? 0 : readInfo.hashCode());
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + (int) (startOffset ^ (startOffset >>> 32));
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
		if (!(obj instanceof DefaultCasPlacedRead)) {
			return false;
		}
		DefaultCasPlacedRead other = (DefaultCasPlacedRead) obj;
		if (dir != other.dir) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (readInfo == null) {
			if (other.readInfo != null) {
				return false;
			}
		} else if (!readInfo.equals(other.readInfo)) {
			return false;
		}
		if (sequence == null) {
			if (other.sequence != null) {
				return false;
			}
		} else if (!sequence.equals(other.sequence)) {
			return false;
		}
		if (startOffset != other.startOffset) {
			return false;
		}
		return true;
	}  
    
    
   
}
