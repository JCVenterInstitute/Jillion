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
package org.jcvi.jillion.assembly.tigr.tasm;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
/**
 * {@code TasmAssembledReadAdapter} is a Adapter which allows
 * an {@link AssembledRead} to conform to the {@link TasmAssembledRead}
 *  interface.
 * @author dkatzel
 *
 */
final class TasmAssembledReadAdapter implements TasmAssembledRead{

	private final AssembledRead delegatePlacedRead;
	
	/**
	 * Adapt the given placedRead into a {@link TasmAssembledRead}.
	 * @param read the {@link AssembledRead} instance to adapt
	 * (may not be null).
	 * @throws NullPointerException if delegatePlacedRead is null.
	 */
	public TasmAssembledReadAdapter(AssembledRead read) {
		if(read ==null){
			throw new NullPointerException("adapted read can not be null");
		}
		this.delegatePlacedRead = read;
	}
	

	@Override
	public long toGappedValidRangeOffset(long referenceIndex) {
		return delegatePlacedRead.toGappedValidRangeOffset(referenceIndex);
	}

	@Override
	public long toReferenceOffset(long validRangeIndex) {
		return delegatePlacedRead.toReferenceOffset(validRangeIndex);
	}

	@Override
	public Direction getDirection() {
		return delegatePlacedRead.getDirection();
	}

	@Override
	public ReadInfo getReadInfo() {
		return delegatePlacedRead.getReadInfo();
	}

	@Override
	public ReferenceMappedNucleotideSequence getNucleotideSequence() {
		return delegatePlacedRead.getNucleotideSequence();
	}

	@Override
	public String getId() {
		return delegatePlacedRead.getId();
	}

	@Override
	public long getGappedLength() {
		return delegatePlacedRead.getGappedLength();
	}

	@Override
	public long getGappedEndOffset() {
		return delegatePlacedRead.getGappedEndOffset();
	}

	@Override
	public long getGappedStartOffset() {
		return delegatePlacedRead.getGappedStartOffset();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ delegatePlacedRead.hashCode();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TasmAssembledRead)) {
			return false;
		}
		AssembledRead other = (AssembledRead) obj;
		
		if (!delegatePlacedRead.equals(other)) {
			return false;
		}
		
		
		return true;
	}
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return delegatePlacedRead.asRange();
    }
    
    
    @Override
	public Range getGappedContigRange() {
		return delegatePlacedRead.getGappedContigRange();
	}

}
