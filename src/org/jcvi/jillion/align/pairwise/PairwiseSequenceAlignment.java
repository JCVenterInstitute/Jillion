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

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
/**
 * {@code PairwiseSequenceAlignment} is a {@link SequenceAlignment}
 * between two {@link org.jcvi.jillion.core.Sequence}s.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} in the sequence.
 * @param <S> the type of {@link org.jcvi.jillion.core.Sequence} in this alignment.
 */
public interface PairwiseSequenceAlignment<R extends Residue, S extends ResidueSequence<R, S, ?>> extends SequenceAlignment<R, S> {
	/**
	 * Get the score of this alignment that 
	 * was computed from the {@link org.jcvi.jillion.align.SubstitutionMatrix}
	 * used to make the alignment.
	 * @return the score as a float depending
	 * on the type of alignment and values
	 * of the scoring matrix this could negative.
	 */
	float getScore();
}
