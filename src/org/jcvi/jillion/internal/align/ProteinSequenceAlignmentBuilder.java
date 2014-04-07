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
package org.jcvi.jillion.internal.align;

import org.jcvi.jillion.align.ProteinSequenceAlignment;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;

public class ProteinSequenceAlignmentBuilder extends AbstractSequenceAlignmentBuilder<AminoAcid, ProteinSequence, ProteinSequenceAlignment, ProteinSequenceBuilder>{

	public ProteinSequenceAlignmentBuilder() {
		super();
	}

	public ProteinSequenceAlignmentBuilder(boolean builtFromTraceback) {
		super(builtFromTraceback);
	}

	@Override
	protected ProteinSequenceBuilder createSequenceBuilder() {
		return new ProteinSequenceBuilder();
	}

	@Override
	protected ProteinSequenceAlignment createAlignment(
			double percentIdentity, int alignmentLength, int numMismatches,
			int numGap, ProteinSequence queryAlignment,
			ProteinSequence subjectAlignment,
			Range queryRange, Range subjectRange) {
		return new AminoAcidSequenceAlignmentImpl(
				percentIdentity, alignmentLength, numMismatches,
				numGap, queryAlignment, subjectAlignment,
				queryRange, subjectRange);
	}

	@Override
	protected Iterable<AminoAcid> parse(String sequence) {
		return new ProteinSequenceBuilder(sequence);
	}
	

	
	
	
	@Override
	public ProteinSequenceAlignmentBuilder addMatches(
			String matchedSequence) {
		super.addMatches(matchedSequence);
		return this;
	}

	@Override
	public ProteinSequenceAlignmentBuilder addMatch(
			AminoAcid match) {
		super.addMatch(match);
		return this;
	}

	@Override
	public ProteinSequenceAlignmentBuilder addMatches(
			Iterable<AminoAcid> matches) {
		super.addMatches(matches);
		return this;
	}

	@Override
	public ProteinSequenceAlignmentBuilder addMismatch(
			AminoAcid query, AminoAcid subject) {
		super.addMismatch(query, subject);
		return this;
	}

	@Override
	public ProteinSequenceAlignmentBuilder addGap(
			AminoAcid query, AminoAcid subject) {
		super.addGap(query, subject);
		return this;
	}

	@Override
	public ProteinSequenceAlignmentBuilder addGap(
			char query, char subject) {
		super.addGap(query, subject);
		return this;
	}
	@Override
	protected AminoAcid parse(char aa) {
		return AminoAcid.parse(aa);
	}





	private final class AminoAcidSequenceAlignmentImpl extends AbstractSequenceAlignmentImpl implements ProteinSequenceAlignment{
		

		public AminoAcidSequenceAlignmentImpl(double percentIdentity,
				int alignmentLength, int numMismatches, int numGap,
				ProteinSequence queryAlignment,
				ProteinSequence subjectAlignment,
				Range queryRange, Range subjectRange) {
			super(percentIdentity, alignmentLength, numMismatches, numGap, queryAlignment,
					subjectAlignment,
					queryRange, subjectRange);
		}

		
		
		
	}

}
