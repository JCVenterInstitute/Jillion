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
package org.jcvi.jillion.align;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
/**
 * {@code SequenceAlignment} is an interface that describes
 * an alignment between two {@link org.jcvi.jillion.core.Sequence}s.
 * @author dkatzel
 *
 * @param <R> the {@link Residue} type.
 * @param <S> the {@link org.jcvi.jillion.core.Sequence} type.
 */
public interface SequenceAlignment<R extends Residue, S extends ResidueSequence<R, S, ?>> {
	/**
	 * Get the percent of the residues that match exactly.
	 * @return the percentage as a double; this
	 * value will be between 0 and 1 inclusive.
	 */
	double getPercentIdentity();
	/**
	 * Get the length of the alignment including
	 * any gaps.
	 * @return a positive integer.
	 */
	int getAlignmentLength();
	/**
	 * Get the number of residues that align but do 
	 * not match.
	 * @return a positive integer which may be 0.
	 */
	int getNumberOfMismatches();
	/**
	 * Get the number of times a 
	 * consecutive group of gaps (which may 
	 * only be of length 1) appears in the alignment.
	 * <p>
	 * For example if a nucleotide sequence alignment
	 * contains the gapped sequence :
	 * {@literal AC-GT-ACGT----AA}
	 * there are 3 gap openings.
	 * @return a positive integer with may be 0.
	 */
	int getNumberOfGapOpenings();

	/**
	 * Get the {@link org.jcvi.jillion.core.Sequence} that represents
	 * the gapped sequence of the query that aligns
	 * to the subject.
	 * @return a {@link org.jcvi.jillion.core.Sequence} which probably
	 * contains gaps; will never be null.
	 */
	S getGappedQueryAlignment();

	/**
	 * Get the {@link org.jcvi.jillion.core.Sequence} that represents
	 * the gapped sequence of the subject that aligns
	 * to the query.
	 * @return a {@link org.jcvi.jillion.core.Sequence} which probably
	 * contains gaps; will never be null.
	 */
	S getGappedSubjectAlignment();
	/**
	 * Get the {@link DirectedRange} that represents
	 * the portion of the full input query sequence
	 * that is used in this alignment.
	 * @return a {@link DirectedRange}; never null.
	 */
	DirectedRange getQueryRange();
	/**
	 * Get the {@link DirectedRange} that represents
	 * the portion of the full input subject sequence
	 * that is used in this alignment.
	 * @return a {@link DirectedRange}; never null.
	 */
    DirectedRange getSubjectRange();

}
