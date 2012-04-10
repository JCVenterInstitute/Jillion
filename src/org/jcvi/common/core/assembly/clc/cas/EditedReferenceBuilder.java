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

package org.jcvi.common.core.assembly.clc.cas;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.slice.DefaultSlice;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;

/**
 * @author dkatzel
 *
 *
 */
public class EditedReferenceBuilder implements Builder<NucleotideSequence>{

    private final Map<Long, Map<Nucleotide, Integer>> consensusMap;
    private final NucleotideSequence uneditedGappedReference;
    private ConsensusCaller consensusCaller = MostFrequentBasecallConsensusCaller.INSTANCE;

    public EditedReferenceBuilder(NucleotideSequence gappedReference){
        consensusMap = new HashMap<Long,Map<Nucleotide,Integer>>((int)gappedReference.getLength()+1, 1F);
        uneditedGappedReference = gappedReference;
    }

    public EditedReferenceBuilder consensusCaller(ConsensusCaller consensusCaller){
        this.consensusCaller = consensusCaller;
        return this;
    }
    public EditedReferenceBuilder addPlacedRead(PlacedRead placedRead){
        long startOffset = placedRead.getBegin();
        int i=0;
        for(Nucleotide base : placedRead.getNucleotideSequence().asList()){
            long index = startOffset+i;
            if(!consensusMap.containsKey(index)){
                consensusMap.put(index, new EnumMap<Nucleotide, Integer>(Nucleotide.class));
            }
            Map<Nucleotide, Integer> histogram =consensusMap.get(index);
            if(!histogram.containsKey(base)){
                histogram.put(base, Integer.valueOf(1));
            }else{
                histogram.put(base,Integer.valueOf( histogram.get(base)+1));
            }
            i++;
        }
        return this;
    }
    
    @Override
    public NucleotideSequence build(){
        NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(uneditedGappedReference);
        long length = uneditedGappedReference.getLength();
        for(int i=0; i<length; i++ ){
            final Map<Nucleotide, Integer> histogramMap = consensusMap.get(Long.valueOf(i));
            if(histogramMap !=null && !histogramMap.isEmpty()){
                DefaultSlice.Builder sliceBuilder = new DefaultSlice.Builder();
                for(Entry<Nucleotide, Integer> entry :histogramMap.entrySet()){
                    Nucleotide base = entry.getKey();
                    int count= entry.getValue();
                    for(int j=0; j< count; j++){
                        sliceBuilder.add(base+"_"+j, base, PhredQuality.valueOf(30), Direction.FORWARD);
                    }
                }
                builder.delete(Range.create(i))
                        .insert(i, consensusCaller.callConsensus(sliceBuilder.build()).getConsensus());
                                
            }
        }
        return builder.build();
    }

    

}
