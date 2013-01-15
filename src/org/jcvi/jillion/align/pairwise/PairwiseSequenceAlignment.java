package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;
/**
 * {@code PairwiseSequenceAlignment} is a {@link SequenceAlignment}
 * between two {@link Sequence}s.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} in the sequence.
 * @param <S> the type of {@link Sequence} in this alignment.
 */
public interface PairwiseSequenceAlignment<R extends Residue, S extends Sequence<R>> extends SequenceAlignment<R, S> {
	/**
	 * Get the score of this alignment that 
	 * was computed from the {@link ScoringMatrix}
	 * used to make the alignment.
	 * @return the score as a float depending
	 * on the type of alignment and values
	 * of the scoring matrix this could negative.
	 */
	float getScore();
}
