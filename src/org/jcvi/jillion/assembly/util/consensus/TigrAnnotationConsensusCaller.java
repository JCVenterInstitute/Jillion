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
/*
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import java.util.EnumSet;
import java.util.Set;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
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
public class TigrAnnotationConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    public TigrAnnotationConsensusCaller(PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    /**
     * Adds all high quality conflicts to standard Chruchill
     * Waterman algorithm of computing which bases
     * to consider towards the ambiguity consensus.
     */
    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities, Slice slice) {
        Set<Nucleotide> basesTowardsAmbiguity = getCWBasesTowardsAmbiguity(normalizedConsensusProbabilities, slice);
        basesTowardsAmbiguity.addAll(findAllHighQualityBases(slice));
        return Nucleotide.getAmbiguityFor(basesTowardsAmbiguity);
        
    }

    private Set<Nucleotide> getCWBasesTowardsAmbiguity(
            ConsensusProbabilities normalizedErrorProbabilityStruct,
            Slice slice) {
        int numberOfDifferentBasesInSlice = generateBasecallHistogramMap(slice).size();        
        return getBasesUsedTowardsAmbiguity(normalizedErrorProbabilityStruct, numberOfDifferentBasesInSlice);
    }

    private Set<Nucleotide> findAllHighQualityBases(Slice slice) {
        Set<Nucleotide> highQualityDiffs = EnumSet.noneOf(Nucleotide.class);
        for(SliceElement sliceElement : slice){
            if(sliceElement.getQuality().compareTo(getHighQualityThreshold()) >=0){
                highQualityDiffs.add(sliceElement.getBase());
            }
        }
        return highQualityDiffs;
    }

}
