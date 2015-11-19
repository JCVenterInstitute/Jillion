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
package org.jcvi.jillion.internal.align;

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code SequenceAlignmentBuilder} is a {@link Builder}
 * that is used to build {@link SequenceAlignment}s.
 * @author dkatzel
 *
 * @param <R> the {@link Residue} type used.
 * @param <S> the {@link ResidueSequence} type used.
 * @param <A> the {@link SequenceAlignment} used
 */
public interface SequenceAlignmentBuilder<R extends Residue, S extends ResidueSequence<R>, A extends SequenceAlignment<R, S>> extends Builder<A> {
	/**
	 * 
	 * @param match the Residue to add to both the query and subject alignments
	 * @return this
	 */
	SequenceAlignmentBuilder<R,S,A> addMatch(R match);
	SequenceAlignmentBuilder<R,S,A> addMatches(Iterable<R> matches);
	
	SequenceAlignmentBuilder<R, S,A> addMatches(String matchedSequence);
	
	SequenceAlignmentBuilder<R,S,A> addMismatch(R query, R subject);
	SequenceAlignmentBuilder<R,S,A> addGap(R query, R subject);
	SequenceAlignmentBuilder<R, S,A> addGap(char query, char subject);

	/**
	 * Sets the offset of the first base in the 
	 * alignment into the full length input sequence.
	 * This value is used to compute the values
	 * returned by {@link SequenceAlignment#getQueryRange()}
	 * and {@link SequenceAlignment#getSubjectRange()}.
	 * If this method is never called, then the start 
	 * offset is assumed to be 0 for both the 
	 * query and subject.  If this object 
	 * was constructed with the builtFromTraceback
	 * flag set to true, then this offset 
	 * is actually the <strong>end</strong>
	 * coordinate of this alignment and the
	 * start coordinate is computed by subtracting
	 * the ungapped alignment length from these input values.
	 * @param queryOffset the offset into the query sequence.
	 * @param subjectOffset the offset into the subject sequence.
	 * @return this.
	 */
	SequenceAlignmentBuilder<R,S,A> setAlignmentOffsets(int queryOffset, int subjectOffset);
	
	SequenceAlignmentBuilder<R,S,A> addMismatches(String query, String subject);
	
	
	
}
