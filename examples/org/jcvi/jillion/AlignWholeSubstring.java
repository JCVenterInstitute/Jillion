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
package org.jcvi.jillion;

import org.jcvi.jillion.align.NucleotideSubstitutionMatrices;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrix;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class AlignWholeSubstring {

	public static void main(String[] args) {
		NucleotideSequence A = new NucleotideSequenceBuilder("AATCGGATATAG").build();
		NucleotideSequence B = new NucleotideSequenceBuilder("CGATA").build();
		
		NucleotideSubstitutionMatrix matrix = NucleotideSubstitutionMatrices.getNuc44();
		
		NucleotidePairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(A,B, matrix)
																			.gapPenalty(0)
																			.build();
		
		System.out.println(alignment);
	}

}
