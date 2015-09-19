/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas.read;

import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;

public final class DefaultCasPlacedRead implements CasPlacedRead{

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
		return Range.of(getGappedStartOffset(), getGappedEndOffset());
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
		result = prime * result + dir.hashCode();
		result = prime * result + id.hashCode();
		result = prime * result
				+ readInfo.hashCode();
		result = prime * result
				+ sequence.hashCode();
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
		if (!id.equals(other.id)) {
			return false;
		}
		if (!readInfo.equals(other.readInfo)) {
			return false;
		}
		if (!sequence.equals(other.sequence)) {
			return false;
		}
		if (startOffset != other.startOffset) {
			return false;
		}
		return true;
	}  
    
    
   
}
