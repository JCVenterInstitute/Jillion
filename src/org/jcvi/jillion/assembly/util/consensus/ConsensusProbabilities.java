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

import static org.jcvi.jillion.assembly.util.consensus.ConsensusUtil.BASES_TO_CONSIDER;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code ConsensusProbabilities} contains the probabilities
 * that each non-ambiguous base is the consensus.
 * @author dkatzel
 *
 *
 */
final class ConsensusProbabilities{

    private static final double ONE_TENTH = 0.1D;

    private static final double ONE_QUARTER = 0.25D;
    
    private final Map<Nucleotide, Double> probabilityMap;
    
    ConsensusProbabilities(Map<Nucleotide, Double> probabilityMap){
        this.probabilityMap = Collections.unmodifiableMap(new EnumMap<Nucleotide, Double>(probabilityMap));
    }
    
    ConsensusProbabilities(Nucleotide consensus,int cumulativeQualityValue){
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
    /**
     * Get the probability that the given nucleotide is
     * the consensus, may be null if the probability
     * of that base isn't specified.
     * @param base the nucleotide to get the probability 
     * of.
     * @return the probability as a Double or null
     */
    public Double getProbabilityFor(Nucleotide base){
        if(base==null){
            throw new NullPointerException("base can not be null");
        }
        return probabilityMap.get(base);
    }
    
    public ConsensusProbabilities normalize(){
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
        
        return new ConsensusProbabilities(newMap);
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
    /**
    * {@inheritDoc}
    */
    @Override
    public String toString() {
        return "ProbabilityStruct [probabilityMap=" + probabilityMap + "]";
    }
    
    
    
}
