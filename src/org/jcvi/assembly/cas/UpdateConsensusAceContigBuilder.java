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

package org.jcvi.assembly.cas;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.DefaultAceContig;
import org.jcvi.assembly.ace.DefaultAceContig.Builder;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class UpdateConsensusAceContigBuilder extends DefaultAceContig.Builder{

    private final Map<Long, Map<NucleotideGlyph, Integer>> consensusMap;
    /**
     * @param contigId
     * @param fullConsensus
     */
    public UpdateConsensusAceContigBuilder(String contigId,
            EncodedGlyphs<NucleotideGlyph> fullConsensus) {
        super(contigId, fullConsensus);
        consensusMap = new HashMap<Long,Map<NucleotideGlyph,Integer>>((int)fullConsensus.getLength());
        
    }

    @Override
    protected List<NucleotideGlyph> updateConsensus(
            List<NucleotideGlyph> originalFullConsensus) {
        List<NucleotideGlyph> updatedConsensus = new ArrayList<NucleotideGlyph>(originalFullConsensus.size());
        for(int i=0; i<originalFullConsensus.size(); i++ )   {
            final Map<NucleotideGlyph, Integer> histogramMap = consensusMap.get(Long.valueOf(i));
            updatedConsensus.add(findMostOccuringBase(histogramMap));
        }
        
        return updatedConsensus;
    }

    @Override
    public Builder addRead(AcePlacedRead acePlacedRead) {
        addReadToConsensusMap(acePlacedRead);
        return super.addRead(acePlacedRead);
    }

    private void addReadToConsensusMap(PlacedRead casPlacedRead) {
        long startOffset = casPlacedRead.getStart();
        int i=0;
        for(NucleotideGlyph base : casPlacedRead.getEncodedGlyphs().decode()){
            long index = startOffset+i;
            if(!consensusMap.containsKey(index)){
                consensusMap.put(index, new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class));
            }
            Map<NucleotideGlyph, Integer> histogram =consensusMap.get(index);
            if(!histogram.containsKey(base)){
                histogram.put(base, Integer.valueOf(1));
            }else{
                histogram.put(base,Integer.valueOf( histogram.get(base)+1));
            }
            i++;
        }
    }
    private NucleotideGlyph findMostOccuringBase(Map<NucleotideGlyph, Integer> histogramMap){
        int max=-1;
        NucleotideGlyph mostOccuringBase = NucleotideGlyph.Unknown;
        if(histogramMap !=null){
            for(Entry<NucleotideGlyph, Integer> entry : histogramMap.entrySet()){
                int value = entry.getValue();
                if(value > max){
                    max = value;
                    mostOccuringBase = entry.getKey();
                }
            }
        }
        return mostOccuringBase;
    }
}
