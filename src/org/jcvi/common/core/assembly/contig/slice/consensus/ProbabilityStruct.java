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
package org.jcvi.common.core.assembly.contig.slice.consensus;

import static org.jcvi.common.core.assembly.contig.slice.consensus.ConsensusUtil.BASES_TO_CONSIDER;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

final class ProbabilityStruct{

    private static final double ONE_TENTH = 0.1D;

    private static final double ONE_QUARTER = 0.25D;
    private final Map<Nucleotide, Double> probabilityMap;
    
    public ProbabilityStruct(Map<Nucleotide, Double> probabilityMap){
        this.probabilityMap = Collections.unmodifiableMap(new EnumMap<Nucleotide, Double>(probabilityMap));
    }
    public Double getProbabilityFor(Nucleotide base){
        return probabilityMap.get(base);
    }
    public ProbabilityStruct(Nucleotide consensus,int cumulativeQualityValue){
        double probability = Math.pow(ONE_TENTH, cumulativeQualityValue*ONE_TENTH);
        probabilityMap = new EnumMap<Nucleotide, Double>(Nucleotide.class);
        for(Nucleotide currentBase : BASES_TO_CONSIDER){
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
        for(Nucleotide currentBase : ConsensusUtil.BASES_TO_CONSIDER){
            sumOfRawProbabilities+= probabilityMap.get(currentBase);
        }
        Map<Nucleotide, Double> newMap = new EnumMap<Nucleotide, Double>(Nucleotide.class);
        newMap.put(Nucleotide.Adenine, computeNormalizedProbabilityFor(Nucleotide.Adenine, sumOfRawProbabilities));
        newMap.put(Nucleotide.Cytosine, computeNormalizedProbabilityFor(Nucleotide.Cytosine, sumOfRawProbabilities));
        newMap.put(Nucleotide.Guanine, computeNormalizedProbabilityFor(Nucleotide.Guanine, sumOfRawProbabilities));
        newMap.put(Nucleotide.Thymine, computeNormalizedProbabilityFor(Nucleotide.Thymine, sumOfRawProbabilities));
        newMap.put(Nucleotide.Gap, computeNormalizedProbabilityFor(Nucleotide.Gap, sumOfRawProbabilities));
        
        return new ProbabilityStruct(newMap);
    }
    
    private Double computeNormalizedProbabilityFor(Nucleotide base, double sumOfRawProbabilities){
        double result= 0D;
        for(Nucleotide currentBase : BASES_TO_CONSIDER){
            if(currentBase != base){
                result+= probabilityMap.get(currentBase);
            }
        }
        return result/sumOfRawProbabilities;
    }
    
    public Set<Entry<Nucleotide, Double>> entrySet(){
        return probabilityMap.entrySet();
    }
    
}
