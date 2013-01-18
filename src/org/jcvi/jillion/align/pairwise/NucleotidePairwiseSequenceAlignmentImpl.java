package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

final class NucleotidePairwiseSequenceAlignmentImpl extends AbstractPairwiseSequenceAlignment<Nucleotide, NucleotideSequence> implements NucleotidePairwiseSequenceAlignment{
	/**
	 * Initial size of String buffer for String created y {@link #toString()}.
	 */
	private static final int TO_STRING_BUFFER_SIZE = 300;

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
		StringBuilder builder = new StringBuilder(TO_STRING_BUFFER_SIZE);
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
		builder.append(']');
		return builder.toString();
	}

	

	
}