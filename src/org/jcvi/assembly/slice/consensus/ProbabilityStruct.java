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

import static org.jcvi.assembly.slice.consensus.ConsensusUtil.BASES_TO_CONSIDER;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.glyph.nuc.NucleotideGlyph;

final class ProbabilityStruct{

    private static final double ONE_TENTH = 0.1D;

    private static final double ONE_QUARTER = 0.25D;
    private final Map<NucleotideGlyph, Double> probabilityMap;
    
    public ProbabilityStruct(Map<NucleotideGlyph, Double> probabilityMap){
        this.probabilityMap = Collections.unmodifiableMap(new EnumMap<NucleotideGlyph, Double>(probabilityMap));
    }
    public Double getProbabilityFor(NucleotideGlyph base){
        return probabilityMap.get(base);
    }
    public ProbabilityStruct (NucleotideGlyph consensus,int cumulativeQualityValue){
        double probability = Math.pow(ONE_TENTH, cumulativeQualityValue*ONE_TENTH);
        probabilityMap = new EnumMap<NucleotideGlyph, Double>(NucleotideGlyph.class);
        for(NucleotideGlyph currentBase : BASES_TO_CONSIDER){
            if(currentBase == consensus){
                probabilityMap.put(currentBase, Double.valueOf(1 - probability));
            }
            else{
                //evenly distribute probability around
                //to the rest of the basecalls.
                probabilityMap.put(currentBase, probability* ONE_QUARTER);
            }
        }
    }
    public ProbabilityStruct normalize(){
        double sumOfRawProbabilities= 0D;
        for(NucleotideGlyph currentBase : ConsensusUtil.BASES_TO_CONSIDER){
            sumOfRawProbabilities+= probabilityMap.get(currentBase);
        }
        Map<NucleotideGlyph, Double> newMap = new EnumMap<NucleotideGlyph, Double>(NucleotideGlyph.class);
        newMap.put(NucleotideGlyph.Adenine, computeNormalizedProbabilityFor(NucleotideGlyph.Adenine, sumOfRawProbabilities));
        newMap.put(NucleotideGlyph.Cytosine, computeNormalizedProbabilityFor(NucleotideGlyph.Cytosine, sumOfRawProbabilities));
        newMap.put(NucleotideGlyph.Guanine, computeNormalizedProbabilityFor(NucleotideGlyph.Guanine, sumOfRawProbabilities));
        newMap.put(NucleotideGlyph.Thymine, computeNormalizedProbabilityFor(NucleotideGlyph.Thymine, sumOfRawProbabilities));
        newMap.put(NucleotideGlyph.Gap, computeNormalizedProbabilityFor(NucleotideGlyph.Gap, sumOfRawProbabilities));
        
        return new ProbabilityStruct(newMap);
    }
    
    private Double computeNormalizedProbabilityFor(NucleotideGlyph base, double sumOfRawProbabilities){
        double result= 0D;
        for(NucleotideGlyph currentBase : BASES_TO_CONSIDER){
            if(currentBase != base){
                result+= probabilityMap.get(currentBase);
            }
        }
        return result/sumOfRawProbabilities;
    }
    
    public Set<Entry<NucleotideGlyph, Double>> entrySet(){
        return probabilityMap.entrySet();
    }
    
}
