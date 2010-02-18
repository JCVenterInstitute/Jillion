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
/*
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.EnumSet;
import java.util.Set;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
/**
 * <code>AnnotationConsensusCaller</code> is the Consensus
 * caller used by the TIGR Annotation Group.  It is much
 * more sensitive to conflicting basecalls in a slice
 * than the Conic model.  Any high quality conflict
 * will contribute towards the consensus call.
 * @author dkatzel
 *
 *
 */
public class AnnotationConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    public AnnotationConsensusCaller(PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    /**
     * Adds all high quality conflicts to standard Chruchill
     * Waterman algorithm of computing which bases
     * to consider towards the ambiguity consensus.
     */
    @Override
    protected NucleotideGlyph getConsensus(
            ProbabilityStruct normalizedErrorProbabilityStruct, Slice slice) {
        Set<NucleotideGlyph> basesTowardsAmbiguity = getCWBasesTowardsAmbiguity(normalizedErrorProbabilityStruct, slice);
        basesTowardsAmbiguity.addAll(findAllHighQualityBases(slice));
        return NucleotideGlyph.getAmbiguityFor(basesTowardsAmbiguity);
        
    }

    private Set<NucleotideGlyph> getCWBasesTowardsAmbiguity(
            ProbabilityStruct normalizedErrorProbabilityStruct,
            Slice slice) {
        int numberOfDifferentBasesInSlice = generateBasecallHistogramMap(slice).size();        
        return getBasesUsedTowardsAmbiguity(normalizedErrorProbabilityStruct, numberOfDifferentBasesInSlice);
    }

    private Set<NucleotideGlyph> findAllHighQualityBases(Slice slice) {
        Set<NucleotideGlyph> highQualityDiffs = EnumSet.noneOf(NucleotideGlyph.class);
        for(SliceElement sliceElement : slice.getSliceElements()){
            if(sliceElement.getQuality().compareTo(getHighQualityThreshold()) >=0){
                highQualityDiffs.add(sliceElement.getBase());
            }
        }
        return highQualityDiffs;
    }

}
