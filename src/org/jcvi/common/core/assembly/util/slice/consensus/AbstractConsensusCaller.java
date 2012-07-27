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
 * Created on Jun 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice.consensus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.common.core.assembly.util.slice.SliceElement;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;

public abstract class AbstractConsensusCaller implements ConsensusCaller{
 private final PhredQuality highQualityThreshold;
    
    public AbstractConsensusCaller(PhredQuality highQualityThreshold){
        this.highQualityThreshold = highQualityThreshold;
    }

    
    public PhredQuality getHighQualityThreshold() {
        return highQualityThreshold;
    }
    
    
    
    @Override
    public ConsensusResult callConsensus(Slice<?> slice) {
        if(slice.getCoverageDepth() ==0){
            //by definition, an empty slice is a Gap
            return new DefaultConsensusResult(Nucleotide.Gap,0);
        }
        return callConsensusWithCoverage(slice);
    }


    protected abstract ConsensusResult callConsensusWithCoverage(Slice<?> slice);


    protected final Map<Nucleotide, Integer> generateBasecallHistogramMap(
            Slice<?> slice) {
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
    
    protected final Map<Nucleotide, Integer> generateQualityValueSumMap(Slice<?> slice) {
        Map<Nucleotide, Integer> qualityValueSumMap = initalizeNucleotideMap();
        for(SliceElement sliceElement : slice){
            Nucleotide basecall =sliceElement.getBase();
            final Integer previousSum = qualityValueSumMap.get(basecall);
            //ignore not ACGT-?
            if(previousSum!=null){
                qualityValueSumMap.put(basecall, Integer.valueOf(previousSum + sliceElement.getQuality().getValue()));
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
