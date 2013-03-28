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
package org.jcvi.jillion_experimental.align.pairwise;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion_experimental.align.AminoAcidSequenceAlignment;

/**
 * {@code AminoAcidSmithWatermanAligner} can perform 
 * a pair-wise alignment of two {@link AminoAcidSequence}s
 * using the local alignment algorithm developed
 * by Smith and Waterman using improvements developed
 * by Gotoh.
 * @author dkatzel
 * 
 * @see <a href="http://dx.doi.org/10.1016/0022-2836(81)90087-5">
 Smith, T.F. and Waterman, M.S. 1981. 
 Identification of common molecular subsequences.
 Journal of Molecular Biology 147:195-197.</a>
 *   
 * @see <a href="http://dx.doi.org/10.1016/0022-2836(82)90398-9">
 Gotoh Osamu. An improved algorithm for matching biological sequences. 
 Journal of Molecular Biology 162:705-708</a>
 */
public final class AminoAcidSmithWatermanAligner  extends AbstractSmithWatermanAligner<AminoAcid,AminoAcidSequence, AminoAcidSequenceAlignment, AminoAcidPairwiseSequenceAlignment>{
	/**
	 * Align the given two {@link AminoAcidSequence}s
	 * using the given {@link ScoringMatrix} by the Smith-Waterman
	 * local alignment algorithm.
	 * @param query the query {@link AminoAcidSequence} to align;
	 * can not be null.
	 * @param subject the subject {@link NucleotideSequence} to align;
	 * can not be null.
	 * @param matrix the {@link ScoringMatrix} to use; can not be null.
	 * @param openGapPenalty the penalty value for opening a gap.
	 * @param extendGapPenalty the penalty for extending an already open gap.
	 * @return a new {@link NucleotidePairwiseSequenceAlignment} instance;
	 * representing the global alignment, will never be null.
	 * @throws NullPointerException if query, subject or matrix are null.
	 */
	public static AminoAcidPairwiseSequenceAlignment align(AminoAcidSequence query,
			AminoAcidSequence subject, ScoringMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty){
		AminoAcidSmithWatermanAligner aligner = new AminoAcidSmithWatermanAligner(query, subject, matrix, openGapPenalty, extendGapPenalty);
		return aligner.getPairwiseSequenceAlignment();
	}
	private AminoAcidSmithWatermanAligner(Sequence<AminoAcid> query,
			Sequence<AminoAcid> subject, ScoringMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getAminoAcidStrategy());
	}

}
