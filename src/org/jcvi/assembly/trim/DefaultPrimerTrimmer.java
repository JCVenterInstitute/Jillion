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

package org.jcvi.assembly.trim;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.align.Aligner;
import org.jcvi.align.Alignment;
import org.jcvi.align.NucleotideSubstitutionMatrix;
import org.jcvi.align.SequenceAlignment;
import org.jcvi.align.SmithWatermanAligner;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultPrimerTrimmer implements PrimerTrimmer{
    /** The substitution matrix to use in the alignment. */
    private final NucleotideSubstitutionMatrix matrix = new NucleotideSubstitutionMatrix.Builder("default")
                                                        .defaultScore(-4)
                                                        .identityScore(4)
                                                        .gapScore(0)
                                                        .unspecifiedMatchScore(0)
                                                        .ambiguityScore(2)
                                                        .build();
    private final Aligner<NucleotideGlyph> aligner = new SmithWatermanAligner(matrix);
    
    private final int minLength;
    private final double minMatch;
    
    /**
     * @param minLength
     * @param minMatch
     */
    public DefaultPrimerTrimmer(int minLength, double minMatch) {
        this.minLength = minLength;
        this.minMatch = minMatch;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range trim(NucleotideEncodedGlyphs sequence,
            NucleotideDataStore primersToTrimAgainst) {
        List<Range> ranges = new ArrayList<Range>();
        for(NucleotideEncodedGlyphs primer : primersToTrimAgainst){
            if(primer.getLength()>=minLength){
                Alignment forwardAlignment = aligner.alignSequence(sequence, primer);
                Alignment reverseAlignment = aligner.alignSequence(
                        new DefaultNucleotideEncodedGlyphs(NucleotideGlyph.reverseCompliment(sequence.decode())),
                        primer);
                
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
           Range intersection = mergedRanges.get(0).intersection(sequenceRange);
           Range left = Range.buildRange(sequenceRange.getStart(), intersection.getStart()-1).convertRange(CoordinateSystem.RESIDUE_BASED);
           Range right = Range.buildRange(intersection.getEnd()+1, sequenceRange.getEnd()).convertRange(CoordinateSystem.RESIDUE_BASED);
           
           List<Range> candidateRanges = Arrays.asList(intersection, left, right);
           Collections.sort(candidateRanges, Range.Comparators.LONGEST_TO_SHORTEST);
           //return the largest range
           return candidateRanges.get(0);           
          
        }
        return sequenceRange;
        
    }

}
