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
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.util.Date;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;

public class AceAssembledReadAdapter implements AceAssembledRead{

    private final AssembledRead placedRead;
    private final PhdInfo phdInfo;
    /**
     * Create a new {@link AceAssembledReadAdapter}
     * instance.
     * 
     * @param placedRead the {@link AssembledRead} to adapt; can not be null.
     * 
     * @param phdDate the faked {@link Date} to use as the phd date;
     * can not be null.
     * @param traceFile the {@link File} to point to as the source file
     * that contained this trace data (sanger chromatogram, sff file etc).
     * 
     */
    public AceAssembledReadAdapter(AssembledRead placedRead,Date phdDate, File traceFile) {
      this(placedRead,
    		  ConsedUtil.generateDefaultPhdInfoFor(traceFile, placedRead.getId(), phdDate));
    }
    public AceAssembledReadAdapter(AssembledRead placedRead,PhdInfo info) {
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
    public ReferenceMappedNucleotideSequence getNucleotideSequence() {
        return placedRead.getNucleotideSequence();
    }
    @Override
    public String getId() {
        return placedRead.getId();
    }
    @Override
    public long getGappedLength() {
        return placedRead.getGappedLength();
    }
    @Override
    public long getGappedEndOffset() {
        return placedRead.getGappedEndOffset();
    }
    @Override
    public long getGappedStartOffset() {
        return placedRead.getGappedStartOffset();
    }
    @Override
    public String toString() {
        return "AcePlacedReadAdapter [placedRead="
                + placedRead + ", phdInfo=" + phdInfo +  "]";
    }
    @Override
	public Range getGappedContigRange() {
		return placedRead.getGappedContigRange();
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
        if (!(obj instanceof AceAssembledReadAdapter)) {
            return false;
        }
        AceAssembledReadAdapter other = (AceAssembledReadAdapter) obj;
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
    
	@Override
	public ReadInfo getReadInfo() {
		return placedRead.getReadInfo();
	}
	
    
  
    
}
