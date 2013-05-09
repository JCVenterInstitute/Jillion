/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
