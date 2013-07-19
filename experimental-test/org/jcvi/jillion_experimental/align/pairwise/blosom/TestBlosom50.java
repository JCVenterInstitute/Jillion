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
package org.jcvi.jillion_experimental.align.pairwise.blosom;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion_experimental.align.pairwise.AminoAcidNeedlemanWunschAligner;
import org.jcvi.jillion_experimental.align.pairwise.AminoAcidPairwiseSequenceAlignment;
import org.jcvi.jillion_experimental.align.pairwise.AminoAcidScoringMatrix;
import org.junit.Test;
public class TestBlosom50 {

	@Test
	public void spotCheck(){
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		assertEquals(5F,
				blosom50.getScore(AminoAcid.Alanine, AminoAcid.Alanine),
				0F);
		
		assertEquals(10F,
				blosom50.getScore(AminoAcid.Proline, AminoAcid.Proline),
				0F);
		
		assertEquals(-3F,
				blosom50.getScore(AminoAcid.Proline, AminoAcid.Valine),
				0F);
		assertEquals(0F,
				blosom50.getScore(AminoAcid.Valine, AminoAcid.Threonine),
				0F);
		assertEquals(1F,
				blosom50.getScore(AminoAcid.STOP, AminoAcid.STOP),
				0F);
		assertEquals(-5F,
				blosom50.getScore(AminoAcid.STOP, AminoAcid.Alanine),
				0F);
	}
	
	@Test
	public void hasSequencesHaveStopCodon(){
		AminoAcidSequence seq1 = new AminoAcidSequenceBuilder("LSGIREE*")
									.build();
		
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		
		AminoAcidPairwiseSequenceAlignment alignment =AminoAcidNeedlemanWunschAligner.align(seq1, seq1, blosom50, -1, -2);
	
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq1,alignment.getGappedSubjectAlignment());
	
	}
	@Test
	public void alignSimilarSequences(){
		AminoAcidSequence seq1 = new AminoAcidSequenceBuilder("LSGIREE*")
									.build();
		AminoAcidSequence seq2 = new AminoAcidSequenceBuilder("LSGVREE*")
									.build();
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		
		AminoAcidPairwiseSequenceAlignment alignment =AminoAcidNeedlemanWunschAligner.align(seq1, seq2, blosom50, -1, -2);
	
		
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq2,alignment.getGappedSubjectAlignment());
	
	}
}
