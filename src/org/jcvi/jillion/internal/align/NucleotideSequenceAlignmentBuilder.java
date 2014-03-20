/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.align;

import org.jcvi.jillion.align.NucleotideSequenceAlignment;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

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
