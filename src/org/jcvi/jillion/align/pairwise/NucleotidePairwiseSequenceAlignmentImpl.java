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

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

final class NucleotidePairwiseSequenceAlignmentImpl extends AbstractPairwiseSequenceAlignment<Nucleotide, NucleotideSequence> implements NucleotidePairwiseSequenceAlignment{
	/**
	 * Initial size of String buffer for String created by {@link #toString()}.
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
		builder.append("NucleotidePairwiseSequenceAlignmentImpl [getPercentIdentity()=")
		.append(getPercentIdentity())
		.append(", getAlignmentLength()=")
		.append(getAlignmentLength())
		.append(", getNumberOfMismatches()=")
		.append(getNumberOfMismatches())
		.append(", getNumberOfGapOpenings()=")
		.append(getNumberOfGapOpenings())
		.append(", getGappedQueryAlignment()=")
		.append(getGappedQueryAlignment())
		.append(", getGappedSubjectAlignment()=")
		.append(getGappedSubjectAlignment())
		.append(", getQueryRange()=")
		.append(getQueryRange())
		.append(", getSubjectRange()=")
		.append(getSubjectRange())
		.append(", getScore()=")
		.append(getScore())
		.append(']');
		return builder.toString();
	}

	

	
}
