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
 * Created on Jun 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

abstract class AbstractConsensusCaller implements ConsensusCaller{
 private final PhredQuality highQualityThreshold;
    
    public AbstractConsensusCaller(PhredQuality highQualityThreshold){
        this.highQualityThreshold = highQualityThreshold;
    }

    
    public PhredQuality getHighQualityThreshold() {
        return highQualityThreshold;
    }
    
    
    
    @Override
    public ConsensusResult callConsensus(Slice slice) {
        if(slice.getCoverageDepth() ==0){
            //by definition, an empty slice is a Gap
            return new DefaultConsensusResult(Nucleotide.Gap,0);
        }
        return callConsensusWithCoverage(slice);
    }

    /**
     * Compute the Consensus for the given Slice which
     * is guaranteed to have coverage.
     * @param slice a Slice object, will never be null
     * and will always have at least 1x coverage.
     * @return a {@link ConsensusResult} should never be null.
     */
    protected abstract ConsensusResult callConsensusWithCoverage(Slice slice);


    protected final Map<Nucleotide, Integer> generateBasecallHistogramMap(
            Slice slice) {
        Map<Nucleotide, Integer> histogramMap = initalizeNucleotideMap();
        for(SliceElement sliceElement : slice){
            Nucleotide basecall =sliceElement.getBase();
            histogramMap.put(basecall, Integer.valueOf(histogramMap.get(basecall) + 1));
        }
        removeUnusedBases(histogramMap);
        return histogramMap;
    }

    private void removeUnusedBases(Map<Nucleotide, Integer> histogramMap) {
        List<Nucleotide> tobeRemoved = new ArrayList<Nucleotide>();
        for(Entry<Nucleotide, Integer> entry : histogramMap.entrySet()){
            if(entry.getValue().equals(Integer.valueOf(0))){
                tobeRemoved.add(entry.getKey());
            }
        }
        for(Nucleotide baseToRemove : tobeRemoved){
            histogramMap.remove(baseToRemove);
        }
    }
    
    protected final Map<Nucleotide, Integer> generateQualityValueSumMap(Slice slice) {
        Map<Nucleotide, Integer> qualityValueSumMap = initalizeNucleotideMap();
        for(SliceElement sliceElement : slice){
            Nucleotide basecall =sliceElement.getBase();
            final Integer previousSum = qualityValueSumMap.get(basecall);
            //ignore not ACGT-?
            if(previousSum!=null){
                qualityValueSumMap.put(basecall, Integer.valueOf(previousSum + sliceElement.getQuality().getQualityScore()));
            }
            
        }
        return qualityValueSumMap;
    }

    private Map<Nucleotide, Integer> initalizeNucleotideMap() {
        Map<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
        for(Nucleotide glyph : ConsensusUtil.BASES_TO_CONSIDER){
            map.put(glyph, Integer.valueOf(0));
        }
        return map;
    }
}
