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

package org.jcvi.assembly.cas.var;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jcvi.assembly.cas.var.Variation.Type;
/**
 * @author dkatzel
 *
 *
 */
public class Compare2VarFiles {

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        File file1 = new File("/usr/local/projects/VHTNGS/sample_data/giv3/RF/26361/mapping/giv3_RF_26361_sanger_only_edited_refs_find_variations.log");
        File file2 = new File("/usr/local/projects/VHTNGS/sample_data/giv3/RF/26361/mapping/giv3_RF_26361_454_only_edited_refs_find_variations.log");
        
        VariationLog solexaVariations = new DefaultVariationLogFile(file1);
        VariationLog sangerVariations = new DefaultVariationLogFile(file2);
        
        Set<String> solexaIds =solexaVariations.getContigIds();
        Set<String> sangerIds =sangerVariations.getContigIds();
        
        for(String contigId : solexaIds){
            
            Map<Long, Variation[]> variations = new TreeMap<Long, Variation[]>();
            if(sangerIds.contains(contigId)){                
                Map<Long, Variation> sangerVariationMap = sangerVariations.getVariationsFor(contigId);
                Map<Long, Variation> solexaVariationMap = solexaVariations.getVariationsFor(contigId);
                
                for(Entry<Long, Variation> solexaVariationEntry: solexaVariations.getVariationsFor(contigId).entrySet()){
                    long coordinate = solexaVariationEntry.getKey();
                    Variation solexaVariation = solexaVariationEntry.getValue();
                    if(sangerVariationMap.containsKey(coordinate)){                        
                        Variation sangerVariation =sangerVariationMap.get(coordinate);
                        if(sangerVariation.getType() != solexaVariation.getType() || sangerVariation.getConsensusBase() != solexaVariation.getConsensusBase()){
                            variations.put(coordinate, new Variation[]{solexaVariation, sangerVariation});
                        }
                    }else if(solexaVariation.getType() == Type.DIFFERENCE){
                        Variation noChangeVariation = new DefaultVariation.Builder(
                                solexaVariation.getCoordinate(),
                                Type.NO_CHANGE,
                                solexaVariation.getReferenceBase(), 
                                solexaVariation.getReferenceBase())
                                    .build();
                        variations.put(coordinate, new Variation[]{solexaVariation, noChangeVariation});
                    }
                }
                for(Entry<Long, Variation> sangerVariationEntry: sangerVariations.getVariationsFor(contigId).entrySet()){
                    long coordinate = sangerVariationEntry.getKey();
                    Variation sangerVariation = sangerVariationEntry.getValue();
                    if(solexaVariationMap.containsKey(coordinate)){                        
                        Variation solexaVariation =solexaVariationMap.get(coordinate);
                        if(sangerVariation.getType() != solexaVariation.getType() || sangerVariation.getConsensusBase() != solexaVariation.getConsensusBase()){
                            variations.put(coordinate, new Variation[]{solexaVariation, sangerVariation});
                        }
                    }else if(sangerVariation.getType() == Type.DIFFERENCE){
                        Variation noChangeVariation = new DefaultVariation.Builder(
                                sangerVariation.getCoordinate(),
                                Type.NO_CHANGE,
                                sangerVariation.getReferenceBase(), 
                                sangerVariation.getReferenceBase())
                                    .build();
                        variations.put(coordinate, new Variation[]{noChangeVariation, sangerVariation});
                    }
                }
                if(!variations.isEmpty()){
                    System.out.printf("%n%s%n%n",contigId);
                    for(Entry<Long, Variation[]> entry : variations.entrySet()){
                        Variation[] array = entry.getValue();
                        System.out.println(array[0] + " | "+ array[1]);
                    }
                }
            }
        }
    }

}
