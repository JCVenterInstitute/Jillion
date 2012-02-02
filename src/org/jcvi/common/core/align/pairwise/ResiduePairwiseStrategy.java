package org.jcvi.common.core.align.pairwise;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.align.AminoAcidSequenceAlignment;
import org.jcvi.common.core.align.AminoAcidSequenceAlignmentBuilder;
import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.common.core.align.SequenceAlignment;
import org.jcvi.common.core.align.SequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
/**
 * {@code ResiduePairwiseStrategy} uses the Strategy Pattern
 * to provide {@link Residue} specific implementations
 * of functions that are needed to generate a {@link PairwiseSequenceAlignment}.
 * @author dkatzel
 *
 * * @param <R> the type of {@link Residue} used in this aligner.
 * @param <S> the {@link Sequence} type input into this aligner.
 * @param <A> the {@link SequenceAlignment} type returned by this aligner.
 * @param <P> the {@link PairwiseSequenceAlignment} type returned by this aligner.
 */
abstract class ResiduePairwiseStrategy<R extends Residue, S extends Sequence<R>, A extends SequenceAlignment<R,S>,P extends PairwiseSequenceAlignment<R,S>> {

	
	public static ResiduePairwiseStrategy<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment, NucleotidePairwiseSequenceAlignment>
		getNucleotideStrategy(){
			return NucleotidePairwiseStrategy.INSTANCE;
	}
	
	public static ResiduePairwiseStrategy<AminoAcid, AminoAcidSequence, AminoAcidSequenceAlignment, AminoAcidPairwiseSequenceAlignment>
		getAminoAcidStrategy(){
			return AminoAcidPairwiseStrategy.INSTANCE;
	}
	/**
	 * Get the list of {@link Residue}s
	 * <strong>IN THE ORDINAL ORDER</strong>.
	 * @return the List of all the {@link Residue}s
	 * in the ordinal order such that
	 * {@code  r == getResidueList().get(r.ordinal())}.
	 */
	protected abstract List<R> getResidueList();
	
	protected abstract P wrapPairwiseAlignment(PairwiseSequenceAlignment<R, S> alignment);

	/**
	 * Get the {@link Residue} instance that represents a gap.
	 * @return a {@link Residue}; never null.
	 */
	protected abstract R getGap();
	/**
	 * Create a new instance of the type of
	 * {@link SequenceAlignmentBuilder} required by this implementation.
	 * @param builtFromTraceback is this alignment going to be built via
	 * a traceback method.  Currently always set to {@code true}.
	 * @return a new {@link SequenceAlignmentBuilder} that can be built
	 * via a traceback if specified.
	 */
	protected abstract SequenceAlignmentBuilder<R, S,A> createSequenceAlignmentBuilder(boolean builtFromTraceback);

	
	private static final class NucleotidePairwiseStrategy extends ResiduePairwiseStrategy<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment, NucleotidePairwiseSequenceAlignment>{

		private static final NucleotidePairwiseStrategy INSTANCE = new NucleotidePairwiseStrategy();
		@Override
		protected List<Nucleotide> getResidueList() {
			return Arrays.asList(Nucleotide.values());
		}

		@Override
		protected NucleotidePairwiseSequenceAlignment wrapPairwiseAlignment(
				PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> alignment) {
			return new NucleotidePairwiseSequenceAlignmentImpl(alignment);
		}

		@Override
		protected Nucleotide getGap() {
			return Nucleotide.Gap;
		}

		@Override
		protected SequenceAlignmentBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment> createSequenceAlignmentBuilder(
				boolean builtFromTraceback) {
			return new NucleotideSequenceAlignmentBuilder(builtFromTraceback);
		}
		
	}
	
	private static final class AminoAcidPairwiseStrategy extends ResiduePairwiseStrategy<AminoAcid, AminoAcidSequence, AminoAcidSequenceAlignment, AminoAcidPairwiseSequenceAlignment>{
		private static final AminoAcidPairwiseStrategy INSTANCE = new AminoAcidPairwiseStrategy();

		@Override
		protected List<AminoAcid> getResidueList() {
			return Arrays.asList(AminoAcid.values());
		}

		@Override
		protected AminoAcidPairwiseSequenceAlignment wrapPairwiseAlignment(
				PairwiseSequenceAlignment<AminoAcid, AminoAcidSequence> alignment) {
			return new AminoAcidPairwiseSequenceAlignmentImpl(alignment);
		}

		@Override
		protected AminoAcid getGap() {
			return AminoAcid.Gap;
		}

		@Override
		protected SequenceAlignmentBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceAlignment> createSequenceAlignmentBuilder(
				boolean builtFromTraceback) {
			return new AminoAcidSequenceAlignmentBuilder(builtFromTraceback);
		}
		
	}
	
}
