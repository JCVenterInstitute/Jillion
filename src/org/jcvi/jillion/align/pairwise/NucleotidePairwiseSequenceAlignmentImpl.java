/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
