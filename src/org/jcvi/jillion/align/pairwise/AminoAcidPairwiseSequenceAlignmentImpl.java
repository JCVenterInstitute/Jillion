package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;

class AminoAcidPairwiseSequenceAlignmentImpl extends AbstractPairwiseSequenceAlignment<AminoAcid, AminoAcidSequence> implements AminoAcidPairwiseSequenceAlignment{

	public AminoAcidPairwiseSequenceAlignmentImpl(
			PairwiseSequenceAlignment<AminoAcid, AminoAcidSequence> delegate) {
		super(delegate);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AminoAcidPairwiseSequenceAlignment){
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		//override hashcode 
		//to make programs like PMD happy that I override
		//equals and hashcode
		return super.hashCode();
	}
	
	
}
