package org.jcvi.common.core.align;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

public class NucleotideSequenceAlignmentBuilder extends AbstractSequenceAlignmentBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment, NucleotideSequenceBuilder>{

	public NucleotideSequenceAlignmentBuilder() {
		super();
	}




	public NucleotideSequenceAlignmentBuilder(boolean builtFromTraceback) {
		super(builtFromTraceback);
	}




	@Override
	protected NucleotideSequenceBuilder createSequenceBuilder() {
		return new NucleotideSequenceBuilder();
	}

	
	

	@Override
	protected Nucleotide parse(char base) {
		return Nucleotide.parse(base);
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



	private final class NucleotideSequenceAlignmentImpl extends AbstractSequenceAlignmentImpl implements NucleotideSequenceAlignment{
		

		public NucleotideSequenceAlignmentImpl(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				NucleotideSequence queryAlignment,
				NucleotideSequence subjectAlignment,
				Range queryRange, Range subjectRange) {
			super(percentIdentity, alignmentLength, numMismatches, numGap, queryAlignment,
					subjectAlignment,
					queryRange, subjectRange);
		}

		
		
		
	}



	@Override
	protected NucleotideSequenceAlignment createAlignment(
			double percentIdentity, int alignmentLength, int numMismatches,
			int numGap, NucleotideSequence queryAlignment,
			NucleotideSequence subjectAlignment,
			Range queryRange, Range subjectRange) {
		return new NucleotideSequenceAlignmentImpl(percentIdentity, alignmentLength, 
				numMismatches, numGap, queryAlignment, subjectAlignment,
				queryRange,subjectRange);
	}




	@Override
	protected Iterable<Nucleotide> parse(String sequence) {
		return new NucleotideSequenceBuilder(sequence)
					.build();
	}
}
