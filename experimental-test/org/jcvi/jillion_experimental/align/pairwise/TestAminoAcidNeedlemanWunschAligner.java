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

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion_experimental.align.AminoAcidSequenceAlignmentBuilder;
import org.jcvi.jillion_experimental.align.pairwise.blosom.BlosomMatrices;
import org.junit.Test;

public class TestAminoAcidNeedlemanWunschAligner {

	@Test
	public void exampleFromBook(){
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		AminoAcidSequence subject = new AminoAcidSequenceBuilder("HEAGAWGHEE")
									.build();
		AminoAcidSequence query = new AminoAcidSequenceBuilder("PAWHEAE")
										.build();
		AminoAcidPairwiseSequenceAlignment expected = createExpectedAlignment("--P-AW-HEAE","HEAGAWGHE-E", 1F);
		
		AminoAcidPairwiseSequenceAlignment actual = AminoAcidNeedlemanWunschAligner.align(
				query, subject, blosom50, -8, -8);
	
		assertEquals(expected, actual);
	}
	
	protected AminoAcidPairwiseSequenceAlignment createExpectedAlignment(String gappedSeq1, String gappedSeq2, float score){
		AminoAcidSequenceAlignmentBuilder builder = new AminoAcidSequenceAlignmentBuilder();
		AminoAcidSequence seq1 = new AminoAcidSequenceBuilder(gappedSeq1).build();
		AminoAcidSequence seq2 = new AminoAcidSequenceBuilder(gappedSeq2).build();
		Iterator<AminoAcid> seq1Iter = seq1.iterator();
		Iterator<AminoAcid> seq2Iter = seq2.iterator();
		
		while(seq1Iter.hasNext()){
			AminoAcid base1 = seq1Iter.next();
			AminoAcid base2 = seq2Iter.next();
			if(base1==base2){
				builder.addMatch(base1);
			}else if (base1==AminoAcid.Gap || base2 == AminoAcid.Gap){
				builder.addGap(base1,base2);
			}else{
				builder.addMismatch(base1, base2);
			}
		}
		if(seq2Iter.hasNext()){
			throw new IllegalArgumentException("seq2 is longer than seq1");
		}
		return new AminoAcidPairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(builder.build(), score));
		
	}
}
