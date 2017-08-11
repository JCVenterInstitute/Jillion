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
package org.jcvi.jillion.align.pairwise;

import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.align.NucleotideSequenceAlignment;
import org.jcvi.jillion.align.ProteinSequenceAlignment;
import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.jillion.internal.align.ProteinSequenceAlignmentBuilder;
import org.jcvi.jillion.internal.align.SequenceAlignmentBuilder;
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
abstract class ResiduePairwiseStrategy<R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R, S>, A extends SequenceAlignment<R,S>,P extends PairwiseSequenceAlignment<R,S>> {

	
	public static ResiduePairwiseStrategy<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder, NucleotideSequenceAlignment, NucleotidePairwiseSequenceAlignment>
		getNucleotideStrategy(){
			return NucleotidePairwiseStrategy.INSTANCE;
	}
	
	public static ResiduePairwiseStrategy<AminoAcid, ProteinSequence, ProteinSequenceBuilder, ProteinSequenceAlignment, ProteinPairwiseSequenceAlignment>
		getAminoAcidStrategy(){
			return ProteinPairwiseStrategy.INSTANCE;
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
	protected abstract SequenceAlignmentBuilder<R, S,B, A> createSequenceAlignmentBuilder(boolean builtFromTraceback, Integer subjectShiftAmount);

	
	private static final class NucleotidePairwiseStrategy extends ResiduePairwiseStrategy<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder, NucleotideSequenceAlignment, NucleotidePairwiseSequenceAlignment>{

		private static final NucleotidePairwiseStrategy INSTANCE = new NucleotidePairwiseStrategy();
		@Override
		protected List<Nucleotide> getResidueList() {
			return Nucleotide.getDnaValues();
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
		protected SequenceAlignmentBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder, NucleotideSequenceAlignment> createSequenceAlignmentBuilder(
				boolean builtFromTraceback, Integer subjectShiftAmount) {
			return new NucleotideSequenceAlignmentBuilder(builtFromTraceback, subjectShiftAmount);
		}
		
	}
	
	private static final class ProteinPairwiseStrategy extends ResiduePairwiseStrategy<AminoAcid, ProteinSequence,ProteinSequenceBuilder, ProteinSequenceAlignment, ProteinPairwiseSequenceAlignment>{
		private static final ProteinPairwiseStrategy INSTANCE = new ProteinPairwiseStrategy();

		@Override
		protected List<AminoAcid> getResidueList() {
			return Arrays.asList(AminoAcid.values());
		}

		@Override
		protected ProteinPairwiseSequenceAlignment wrapPairwiseAlignment(
				PairwiseSequenceAlignment<AminoAcid, ProteinSequence> alignment) {
			return new ProteinPairwiseSequenceAlignmentImpl(alignment);
		}

		@Override
		protected AminoAcid getGap() {
			return AminoAcid.Gap;
		}

		@Override
		protected SequenceAlignmentBuilder<AminoAcid, ProteinSequence, ProteinSequenceBuilder, ProteinSequenceAlignment> createSequenceAlignmentBuilder(
				boolean builtFromTraceback, Integer subjectShiftAmount) {
			return new ProteinSequenceAlignmentBuilder(builtFromTraceback, subjectShiftAmount);
		}
		
	}
	
}
