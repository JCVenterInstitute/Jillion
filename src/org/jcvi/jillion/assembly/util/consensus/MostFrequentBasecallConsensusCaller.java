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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import java.util.EnumMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.MapValueComparator;
/**
 * {@code MostFrequentBasecallConsensusCaller} is a {@link ConsensusCaller}
 * implementation that will return the most frequent basecall in
 * a Slice and the Consensus Quality is the sum of all the qualities of
 * the reads with that basecall minus the sum of the reads 
 * without that basecall.  If the Slice is empty, then the ConsensusResult
 * is N with 0 quality.
 * 
 * @author dkatzel
 *
 */
public enum MostFrequentBasecallConsensusCaller implements ConsensusCaller{
	INSTANCE;
	
    @Override
    public ConsensusResult callConsensus(Slice slice) {
        if(slice==null){
            return new DefaultConsensusResult(Nucleotide.Unknown, 0);
        }
        Map<Nucleotide, Integer> histogramMap = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
        Map<Nucleotide, Integer> qualitySums = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
        for(SliceElement sliceElement : slice){
            Nucleotide base =sliceElement.getBase();
            if(!qualitySums.containsKey(base)){
                qualitySums.put(base, Integer.valueOf(0));
            }
            qualitySums.put(base, qualitySums.get(base) + sliceElement.getQuality().getQualityScore());
            incrementHistogram(histogramMap, base);
        }
        Nucleotide consensus= findMostOccuringBase(histogramMap);
        int sum=0;
        for(Entry<Nucleotide, Integer> entry : qualitySums.entrySet()){
            if(entry.getKey() == consensus){
                sum+= entry.getValue();
            }
            else{
                sum -= entry.getValue();
            }
        }
        return new DefaultConsensusResult(consensus, sum);
    }

    private void incrementHistogram(Map<Nucleotide, Integer> histogramMap,
            Nucleotide base) {
        if(!histogramMap.containsKey(base)){
            histogramMap.put(base, Integer.valueOf(0));
        }
        histogramMap.put(base, Integer.valueOf(histogramMap.get(base).intValue()+1));
    }

    private Nucleotide findMostOccuringBase(Map<Nucleotide, Integer> histogramMap){
        if(histogramMap.isEmpty()){
            return Nucleotide.Unknown;
        }
        SortedMap<Nucleotide, Integer> sortedMap = MapValueComparator.sortDescending(histogramMap);
        return sortedMap.firstKey();
       
    }
}
