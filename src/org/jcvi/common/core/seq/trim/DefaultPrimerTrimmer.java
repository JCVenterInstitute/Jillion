/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.seq.trim;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.align.pairwise.DefaultNucleotideScoringMatrix;
import org.jcvi.common.core.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.common.core.align.pairwise.NucleotideSmithWatermanAligner;
import org.jcvi.common.core.align.pairwise.ScoringMatrix;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultPrimerTrimmer implements PrimerTrimmer{
    
    private static final NucleotidePairwiseSequenceAlignment NULL_ALIGNMENT_OBJECT = new NucleotidePairwiseSequenceAlignment(){

		@Override
		public float getScore() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getPercentIdentity() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getAlignmentLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getNumberOfMismatches() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getNumberOfGapOpenings() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public NucleotideSequence getGappedQueryAlignment() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public NucleotideSequence getGappedSubjectAlignment() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DirectedRange getQueryRange() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DirectedRange getSubjectRange() {
			// TODO Auto-generated method stub
			return null;
		}
    	
    };
    private static final ScoringMatrix<Nucleotide> MATRIX = new DefaultNucleotideScoringMatrix.Builder(-4)
    										.setMatch(4)
    										.ambiguityScore(2)
    										.build();

    
    private final int minLength;
    private final double minMatch;
    private final boolean alsoCheckReverseCompliment;
    /**
     * @param minLength
     * @param minMatch
     */
    public DefaultPrimerTrimmer(int minLength, double minMatch) {
        this(minLength, minMatch, true);
    }
    public DefaultPrimerTrimmer(int minLength, double minMatch, boolean alsoCheckReverseCompliment) {
        this.minLength = minLength;
        this.minMatch = minMatch;
        this.alsoCheckReverseCompliment = alsoCheckReverseCompliment;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range trim(NucleotideSequence sequence,
            NucleotideDataStore primersToTrimAgainst) {
        List<Range> ranges = new ArrayList<Range>();
        for(NucleotideSequence primer : primersToTrimAgainst){
            if(primer.getLength()>=minLength){
            	NucleotidePairwiseSequenceAlignment forwardAlignment = NucleotideSmithWatermanAligner.align(primer, sequence, 
            					MATRIX, -2, -1);
               
                final NucleotidePairwiseSequenceAlignment reverseAlignment;
                if(alsoCheckReverseCompliment){
                	NucleotideSequence reversePrimer = new NucleotideSequenceBuilder(primer)
												.reverseCompliment()
												.build();
					reverseAlignment = NucleotideSmithWatermanAligner.align(
							reversePrimer,sequence,
                			 MATRIX, -2, -1);
                }else{
                    reverseAlignment = NULL_ALIGNMENT_OBJECT;
                }
                NucleotidePairwiseSequenceAlignment bestAlignment;
                if(forwardAlignment.getPercentIdentity() > minMatch || reverseAlignment.getPercentIdentity() > minMatch){
                    bestAlignment= reverseAlignment.getScore() > forwardAlignment.getScore() ? reverseAlignment : forwardAlignment;
                    ranges.add(bestAlignment.getSubjectRange().asRange());
                }
            }
        }
        List<Range> mergedRanges = Range.mergeRanges(ranges);
        Range sequenceRange = Range.buildRangeOfLength(0, sequence.getLength()).convertRange(CoordinateSystem.RESIDUE_BASED);

        if(mergedRanges.size() ==1){
           Range primerRange = mergedRanges.get(0);
           if(primerRange.equals(sequenceRange)){
               //the entire primer range is the same as the original sequence
               return Range.buildEmptyRange();
           }
           List<Range> rangesFreeFromPrimer = sequenceRange.compliment(primerRange);
           Collections.sort(rangesFreeFromPrimer, Range.Comparators.LONGEST_TO_SHORTEST);
           //return the largest range
           return rangesFreeFromPrimer.get(0).convertRange(Range.CoordinateSystem.RESIDUE_BASED);     
              
          
        }
        return sequenceRange;
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range trim(String sequence, NucleotideDataStore primersToTrimAgainst) {
        return trim(NucleotideSequenceFactory.create(sequence), primersToTrimAgainst);
    }

}
