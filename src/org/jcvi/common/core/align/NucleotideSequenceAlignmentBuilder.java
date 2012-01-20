package org.jcvi.common.core.align;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;

public class NucleotideSequenceAlignmentBuilder extends AbstractSequenceAlignmentBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment, NucleotideSequenceBuilder>{

	@Override
	protected NucleotideSequenceBuilder createSequenceBuilder() {
		return new NucleotideSequenceBuilder();
	}

	
	

	@Override
	public NucleotideSequenceAlignmentBuilder addMatch(
			Nucleotide match) {
		super.addMatch(match);
		return this;
	}

	@Override
	public NucleotideSequenceAlignmentBuilder addMatches(
			Iterable<Nucleotide> matches) {
		super.addMatches(matches);
		return this;
	}

	@Override
	public NucleotideSequenceAlignmentBuilder addMismatch(
			Nucleotide query, Nucleotide subject) {
		super.addMismatch(query, subject);
		return this;
	}

	@Override
	public NucleotideSequenceAlignmentBuilder addGap(
			Nucleotide query, Nucleotide subject) {
		super.addGap(query, subject);
		return this;
	}

	@Override
	public NucleotideSequenceAlignmentBuilder reverse() {
		super.reverse();
		return this;
	}



	private final class NucleotideSequenceAlignmentImpl extends SequenceAlignmentImpl implements NucleotideSequenceAlignment{
		

		public NucleotideSequenceAlignmentImpl(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				NucleotideSequence queryAlignment,
				NucleotideSequence subjectAlignment) {
			super(percentIdentity, alignmentLength, numMismatches, numGap, queryAlignment,
					subjectAlignment);
		}

		
		
		
	}



	@Override
	protected NucleotideSequenceAlignment createAlignment(
			double percentIdentity, int alignmentLength, int numMismatches,
			int numGap, NucleotideSequence queryAlignment,
			NucleotideSequence subjectAlignment) {
		return new NucleotideSequenceAlignmentImpl(percentIdentity, alignmentLength, numMismatches, numGap, queryAlignment, subjectAlignment);
	}




	@Override
	protected Iterable<Nucleotide> parse(String sequence) {
		return Nucleotides.parse(sequence);
	}
}
