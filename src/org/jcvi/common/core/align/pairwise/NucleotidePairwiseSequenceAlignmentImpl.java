package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

final class NucleotidePairwiseSequenceAlignmentImpl extends AbstractPairwiseSequenceAlignment<Nucleotide, NucleotideSequence> implements NucleotidePairwiseSequenceAlignment{

	public NucleotidePairwiseSequenceAlignmentImpl(
			PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> delegate) {
		super(delegate);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NucleotidePairwiseSequenceAlignment){
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NucleotidePairwiseSequenceAlignmentImpl [getPercentIdentity()=");
		builder.append(getPercentIdentity());
		builder.append(", getAlignmentLength()=");
		builder.append(getAlignmentLength());
		builder.append(", getNumberOfMismatches()=");
		builder.append(getNumberOfMismatches());
		builder.append(", getNumberOfGapOpenings()=");
		builder.append(getNumberOfGapOpenings());
		builder.append(", getGappedQueryAlignment()=");
		builder.append(getGappedQueryAlignment());
		builder.append(", getGappedSubjectAlignment()=");
		builder.append(getGappedSubjectAlignment());
		builder.append(", getQueryRange()=");
		builder.append(getQueryRange());
		builder.append(", getSubjectRange()=");
		builder.append(getSubjectRange());
		builder.append(", getScore()=");
		builder.append(getScore());
		builder.append("]");
		return builder.toString();
	}

	

	
}
