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

import java.util.Iterator;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion_experimental.align.NucleotideSequenceAlignmentBuilder;

public class AbstractTestNucleotideAligner {

	protected final NucleotideScoringMatrix matrix;
	public AbstractTestNucleotideAligner(){
		NucleotideScoringMatrixBuilder builder = new NucleotideScoringMatrixBuilder(-1F);
		builder.setMatch(2);
		matrix = builder.build();
	}
	protected NucleotidePairwiseSequenceAlignment createExpectedAlignment(String gappedSeq1, String gappedSeq2, float score){
		NucleotideSequenceAlignmentBuilder builder = new NucleotideSequenceAlignmentBuilder();
		NucleotideSequence seq1 = new NucleotideSequenceBuilder(gappedSeq1).build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder(gappedSeq2).build();
		Iterator<Nucleotide> seq1Iter = seq1.iterator();
		Iterator<Nucleotide> seq2Iter = seq2.iterator();
		
		while(seq1Iter.hasNext()){
			Nucleotide base1 = seq1Iter.next();
			Nucleotide base2 = seq2Iter.next();
			if(base1==base2){
				builder.addMatch(base1);
			}else if (base1==Nucleotide.Gap || base2 == Nucleotide.Gap){
				builder.addGap(base1,base2);
			}else{
				builder.addMismatch(base1, base2);
			}
		}
		if(seq2Iter.hasNext()){
			throw new IllegalArgumentException("seq2 is longer than seq1");
		}
		return new NucleotidePairwiseSequenceAlignmentImpl(PairwiseSequenceAlignmentWrapper.wrap(builder.build(), score));
		
	}
}
