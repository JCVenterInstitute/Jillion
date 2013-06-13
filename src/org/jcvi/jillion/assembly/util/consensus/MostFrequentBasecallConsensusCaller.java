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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

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
 * is N with 0 quality.  If multiple bases share the "most frequent basecall count",
 * then the basecall with the highest cumulative quality value will be picked.
 *  
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
            	histogramMap.put(base, Integer.valueOf(1));
                qualitySums.put(base, Integer.valueOf(sliceElement.getQuality().getQualityScore()));
            }else{
            	histogramMap.put(base, Integer.valueOf(histogramMap.get(base).intValue()+1));
            	qualitySums.put(base, qualitySums.get(base) + sliceElement.getQuality().getQualityScore());
            }           
        }
        Nucleotide consensus= findMostOccuringBaseWithHighestQvs(histogramMap, qualitySums);
        int sum = getCumulativeQualityConsensusValue(qualitySums, consensus);
        return new DefaultConsensusResult(consensus, sum);
    }


	private int getCumulativeQualityConsensusValue(
			Map<Nucleotide, Integer> qualitySums, Nucleotide consensus) {
		int sum=0;
        for(Entry<Nucleotide, Integer> entry : qualitySums.entrySet()){
            if(entry.getKey() == consensus){
                sum+= entry.getValue();
            }
            else{
                sum -= entry.getValue();
            }
        }
		return sum;
	}

    
    /**
     * Get the most occurring basecall,
     * if multiple basecalls have the same highest frequency,
     * then pick the one with the highest cumulative quality score.
     * @param histogramMap
     * @param qualitySums
     * @return
     */
    private Nucleotide findMostOccuringBaseWithHighestQvs(Map<Nucleotide, Integer> histogramMap,Map<Nucleotide, Integer> qualitySums ){
        if(histogramMap.isEmpty()){
            return Nucleotide.Unknown;
        }
        SortedMap<Nucleotide, Integer> sortedMap = MapValueComparator.sortDescending(histogramMap);
        
        Iterator<Entry<Nucleotide, Integer>> iter = sortedMap.entrySet().iterator();
        //has to have at least one
        Entry<Nucleotide, Integer> most = iter.next();
        Nucleotide consensus = most.getKey();
        int count = most.getValue();
        int bestQv = qualitySums.get(consensus);
        while(iter.hasNext()){
        	Entry<Nucleotide, Integer> next = iter.next();
        	if(next.getValue().intValue() <count){
        		break;
        	}
        	int currentQv = qualitySums.get(next.getKey()).intValue();
			if(currentQv > bestQv){
        		bestQv = currentQv;
        		consensus = next.getKey();
        	}
        }
        return consensus;
       
    }
}
