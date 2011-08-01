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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.experimental.align.Aligner;
import org.jcvi.common.experimental.align.Alignment;
import org.jcvi.common.experimental.align.NucleotideSubstitutionMatrix;
import org.jcvi.common.experimental.align.SequenceAlignment;
import org.jcvi.common.experimental.align.SmithWatermanAligner;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultPrimerTrimmer implements PrimerTrimmer{
    
    private static final Alignment NULL_ALIGNMENT_OBJECT = new Alignment() {
        
        @Override
        public double getScore() {
            return 0;
        }
        
        @Override
        public SequenceAlignment getReferenceAlignment() {
            return null;
        }
        
        @Override
        public SequenceAlignment getQueryAlignment() {
            return null;
        }
        
        @Override
        public double getMatch() {
            return 0;
        }
        
        @Override
        public double getIdentity() {
            return 0;
        }
    };
    /** The substitution matrix to use in the alignment. */
    private final NucleotideSubstitutionMatrix matrix = new NucleotideSubstitutionMatrix.Builder("default")
                                                        .defaultScore(-4)
                                                        .identityScore(4)
                                                        .gapScore(0)
                                                        .unspecifiedMatchScore(0)
                                                        .ambiguityScore(2)
                                                        .build();
    private final Aligner<Nucleotide> aligner = new SmithWatermanAligner(matrix);
    
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
                Alignment forwardAlignment = aligner.alignSequence(sequence, primer);
                final Alignment reverseAlignment;
                if(alsoCheckReverseCompliment){
                    reverseAlignment = aligner.alignSequence(
                            new DefaultNucleotideSequence(Nucleotides.reverseCompliment(sequence.decode())),
                            primer);
                }else{
                    reverseAlignment = NULL_ALIGNMENT_OBJECT;
                }
                Alignment bestAlignment;
                if(forwardAlignment.getMatch() > minMatch || reverseAlignment.getMatch() > minMatch){
                    bestAlignment= reverseAlignment.getScore() > forwardAlignment.getScore() ? reverseAlignment : forwardAlignment;
                    SequenceAlignment sequenceAlignment =bestAlignment.getQueryAlignment();
                    Range range = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                            sequenceAlignment.getStart(), sequenceAlignment.getStop());
                    if(bestAlignment == reverseAlignment){
                        //reverseCompliment
                        range = AssemblyUtil.reverseComplimentValidRange(range, sequence.getLength());
                    }
                    ranges.add(range);
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
           Range intersection = primerRange.intersection(sequenceRange);
           
           Range left = Range.buildRange(sequenceRange.getStart(), intersection.getStart()-1).convertRange(CoordinateSystem.RESIDUE_BASED);
           Range right = Range.buildRange(intersection.getEnd()+1, sequenceRange.getEnd()).convertRange(CoordinateSystem.RESIDUE_BASED);
           
           List<Range> candidateRanges = Arrays.asList(intersection, left, right);
           Collections.sort(candidateRanges, Range.Comparators.LONGEST_TO_SHORTEST);
           //return the largest range
           return candidateRanges.get(0);           
          
        }
        return sequenceRange;
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range trim(String sequence, NucleotideDataStore primersToTrimAgainst) {
        return trim(new DefaultNucleotideSequence(sequence), primersToTrimAgainst);
    }

}
