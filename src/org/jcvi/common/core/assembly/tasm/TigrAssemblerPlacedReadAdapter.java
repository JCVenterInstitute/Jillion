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

package org.jcvi.common.core.assembly.tasm;

import java.util.EnumMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.jcvi.common.core.symbol.residue.nt.ReferenceEncodedNucleotideSequence;
/**
 * {@code TigrAssemblerPlacedReadAdapter} is a Adapter which allows
 * a PlacedRead to conform to the TigrAssemblerPlacedRead interface.
 * @author dkatzel
 *
 */
public class TigrAssemblerPlacedReadAdapter implements TigrAssemblerPlacedRead{

	private final AssembledRead delegatePlacedRead;
	
	/**
	 * Adapt the given placedRead into a TigrAssemblerPlacedRead.
	 * @param delegatePlacedRead the PlacedRead instance to adapt
	 * (may not be null).
	 * @throws NullPointerException if delegatePlacedRead is null.
	 */
	public TigrAssemblerPlacedReadAdapter(AssembledRead delegatePlacedRead) {
		this.delegatePlacedRead = delegatePlacedRead;
		generateAttributes();
	}
	/**
	 * Rather than waste memory, we will regenerate
	 * attributes on the fly
	 * @return
	 */
	private Map<TigrAssemblerReadAttribute, String> generateAttributes() {
		Map<TigrAssemblerReadAttribute, String> attributes = new EnumMap<TigrAssemblerReadAttribute, String>(TigrAssemblerReadAttribute.class);
		attributes.put(TigrAssemblerReadAttribute.NAME, getId());
		
		//TODO is asm_lend / asm_rend ungapped or gapped?
		//try ungapped?
		int asmLend =1+ getNucleotideSequence().getUngappedOffsetFor((int)getGappedStartOffset());
		int asmRend =1+ getNucleotideSequence().getUngappedOffsetFor((int)getGappedEndOffset());
		attributes.put(TigrAssemblerReadAttribute.CONTIG_LEFT, ""+asmLend);
		attributes.put(TigrAssemblerReadAttribute.CONTIG_RIGHT, ""+asmRend);
		attributes.put(TigrAssemblerReadAttribute.CONTIG_START_OFFSET, ""+(this.getGappedStartOffset()));
		attributes.put(TigrAssemblerReadAttribute.GAPPED_SEQUENCE, Nucleotides.asString(this.getNucleotideSequence().asList()));
		
		Range validRange = this.getReadInfo().getValidRange();
		if(this.getDirection()== Direction.FORWARD){
			attributes.put(TigrAssemblerReadAttribute.SEQUENCE_LEFT, ""+(validRange.getBegin()+1));
			attributes.put(TigrAssemblerReadAttribute.SEQUENCE_RIGHT, ""+(validRange.getEnd()+1));
		}else{
			//reverse gets left and right flipped
			attributes.put(TigrAssemblerReadAttribute.SEQUENCE_RIGHT, ""+(validRange.getBegin()+1));
			attributes.put(TigrAssemblerReadAttribute.SEQUENCE_LEFT, ""+(validRange.getEnd()+1));
		}
		return attributes;
	}

	@Override
	public String getAttributeValue(TigrAssemblerReadAttribute attribute) {
		return generateAttributes().get(attribute);
	}

	@Override
	public Map<TigrAssemblerReadAttribute, String> getAttributes() {
		return generateAttributes();
	}

	@Override
	public boolean hasAttribute(TigrAssemblerReadAttribute attribute) {
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
	public ReferenceEncodedNucleotideSequence getNucleotideSequence() {
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
		
		if (!(obj instanceof TigrAssemblerPlacedRead)) {
			return true;
		}
		TigrAssemblerPlacedRead otherTigrRead = (TigrAssemblerPlacedRead) obj;
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
