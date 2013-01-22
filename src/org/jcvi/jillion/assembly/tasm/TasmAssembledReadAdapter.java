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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import java.util.EnumMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
/**
 * {@code TasmAssembledReadAdapter} is a Adapter which allows
 * an {@link AssembledRead} to conform to the {@link TasmAssembledRead}
 *  interface.
 * @author dkatzel
 *
 */
public final class TasmAssembledReadAdapter implements TasmAssembledRead{

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
		generateAttributes();
	}
	/**
	 * Rather than waste memory, we will regenerate
	 * attributes on the fly
	 * @return
	 */
	private Map<TasmReadAttribute, String> generateAttributes() {
		Map<TasmReadAttribute, String> attributes = new EnumMap<TasmReadAttribute, String>(TasmReadAttribute.class);
		attributes.put(TasmReadAttribute.NAME, getId());
		NucleotideSequence consensus =delegatePlacedRead.getNucleotideSequence().getReferenceSequence();
		
		
		int ungappedConsensusStart =1+ consensus.getUngappedOffsetFor((int)getGappedStartOffset());
		int ungappedConsensusEnd =1+ consensus.getUngappedOffsetFor((int)getGappedEndOffset());
		attributes.put(TasmReadAttribute.CONTIG_LEFT, ""+ungappedConsensusStart);
		attributes.put(TasmReadAttribute.CONTIG_RIGHT, ""+ungappedConsensusEnd);
		attributes.put(TasmReadAttribute.CONTIG_START_OFFSET, ""+(this.getGappedStartOffset()));
		attributes.put(TasmReadAttribute.GAPPED_SEQUENCE, this.getNucleotideSequence().toString());
		
		Range validRange = this.getReadInfo().getValidRange();
		if(this.getDirection()== Direction.FORWARD){
			attributes.put(TasmReadAttribute.SEQUENCE_LEFT, ""+(validRange.getBegin()+1));
			attributes.put(TasmReadAttribute.SEQUENCE_RIGHT, ""+(validRange.getEnd()+1));
		}else{
			//reverse gets left and right flipped
			attributes.put(TasmReadAttribute.SEQUENCE_RIGHT, ""+(validRange.getBegin()+1));
			attributes.put(TasmReadAttribute.SEQUENCE_LEFT, ""+(validRange.getEnd()+1));
		}
		return attributes;
	}

	@Override
	public String getAttributeValue(TasmReadAttribute attribute) {
		return generateAttributes().get(attribute);
	}

	@Override
	public Map<TasmReadAttribute, String> getAttributes() {
		return generateAttributes();
	}

	@Override
	public boolean hasAttribute(TasmReadAttribute attribute) {
		switch( attribute){
			case BEST :
			case COMMENT:
			case DB: return false;
			default :
				return true;
		}		
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
		if (!(obj instanceof AssembledRead)) {
			return false;
		}
		AssembledRead other = (AssembledRead) obj;
		
		if (!delegatePlacedRead.equals(other)) {
			return false;
		}
		
		if (!(obj instanceof TasmAssembledRead)) {
			return true;
		}
		TasmAssembledRead otherTigrRead = (TasmAssembledRead) obj;
		return !generateAttributes().equals(otherTigrRead.getAttributes());
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
