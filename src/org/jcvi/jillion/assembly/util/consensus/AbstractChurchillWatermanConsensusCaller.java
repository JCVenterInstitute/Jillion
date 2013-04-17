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
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import static org.jcvi.jillion.assembly.util.consensus.ConsensusUtil.BASES_TO_CONSIDER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * Calculate Consensus for a slice using Bayes formula and the procedure from
 * <pre>
 * Churchill, G.A. and Waterman, M.S.
 * "The accuracy of DNA sequences: Estimating sequence quality."
 * Genomics 14, pp.89-98 (1992)
 * </pre>
 * @author dkatzel
 *
 *
 */
abstract class AbstractChurchillWatermanConsensusCaller extends AbstractConsensusCaller{
    
   
   


    public AbstractChurchillWatermanConsensusCaller(
            PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }
    
    /**
     * Get the consensus for the given Slice which
     * is guaranteed to have coverage.
     * @param normalizedConsensusProbabilities the {@link ConsensusProbabilities}
     * containing the probabilities of each non-ambiguous base
     * normalized to total 100%.
     * @param slice the {@link Slice} to compute the consensus of.
     * @return a {@link Nucleotide} that is the consensus of all the bases in this
     * slice; the return may be an ambiguous {@link Nucleotide}.
     */
    protected abstract Nucleotide getConsensus(ConsensusProbabilities normalizedConsensusProbabilities,Slice slice) ;
 
    
    @Override
    public final ConsensusResult callConsensusWithCoverage(Slice slice) {
        Map<Nucleotide, Integer> qualityValueSumMap = generateQualityValueSumMap(slice);
        ConsensusProbabilities normalizedErrorProbabilityStruct = generateNormalizedProbabilityStruct(qualityValueSumMap);
        Nucleotide consensus=  getConsensus(normalizedErrorProbabilityStruct,slice);
        return new DefaultConsensusResult(consensus,
                
                getErrorProbability(normalizedErrorProbabilityStruct,
                        slice));
    }


    private int getErrorProbability(
            ConsensusProbabilities normalizedErrorProbabilityStruct,
            Slice slice) {
        double normalizedProbability= getProbabilityFor(normalizedErrorProbabilityStruct);
        if(normalizedProbability == 0.0D){
            //special case if we only have matches, then sum slice qualities
            int sum =0;
            for(SliceElement element : slice){
                sum += element.getQuality().getQualityScore();
            }
            return sum;
        }
            return PhredQuality.computeQualityScore(normalizedProbability);
    }

   
    
    private double getProbabilityFor(ConsensusProbabilities normalizedErrorProbabilityStruct){
        //find lowest
        Double lowest = Double.MAX_VALUE;
        for(Entry<Nucleotide, Double> entry: normalizedErrorProbabilityStruct.entrySet()){
            Double currentValue = entry.getValue();
            if(currentValue.compareTo(lowest) <0){
                lowest = currentValue;
            }
        }
        if(lowest.equals(Double.MAX_VALUE)){
            //no probabilities
            return 0D;
        }
        return lowest;
    }
    private ConsensusProbabilities generateNormalizedProbabilityStruct(
            Map<Nucleotide, Integer> qualityValueSumMap) {
        List<ConsensusProbabilities> probabilityStructs = createProbabilityStructsForEachBase(qualityValueSumMap);
        ConsensusProbabilities rawErrorProbabilityStruct = createRawErrorProbabilityStruct(probabilityStructs);
        return rawErrorProbabilityStruct.normalize();
    }
    private ConsensusProbabilities createRawErrorProbabilityStruct(
            List<ConsensusProbabilities> probabilityStructs) {
        Map<Nucleotide, Double> rawErrorProbabilityMap = new EnumMap<Nucleotide, Double>(Nucleotide.class);
        for(Nucleotide base : ConsensusUtil.BASES_TO_CONSIDER){
            rawErrorProbabilityMap.put(base, calculateRawErrorProbabilityFor(base, probabilityStructs));
        }        
        return new ConsensusProbabilities(rawErrorProbabilityMap);
    }

    private List<ConsensusProbabilities> createProbabilityStructsForEachBase(
            Map<Nucleotide, Integer> qualityValueSumMap) {
        List<ConsensusProbabilities> probabilityStructs= new ArrayList<ConsensusProbabilities>();
        for(Nucleotide base : ConsensusUtil.BASES_TO_CONSIDER){
            probabilityStructs.add(new ConsensusProbabilities(base, qualityValueSumMap.get(base)));
        }
               
        return probabilityStructs;
    }

    private double calculateRawErrorProbabilityFor(Nucleotide base,
            List<ConsensusProbabilities> probabilityStructs) {
        double result = 1D;
        for(ConsensusProbabilities struct : probabilityStructs){
            result *=struct.getProbabilityFor(base);
        }
        return result;
    }
   
    protected final Set<Nucleotide> getBasesUsedTowardsAmbiguity(
            ConsensusProbabilities normalizedErrorProbabilityStruct, int baseCount) {
        double errorProbabilityOfAmbiguity;
        double sumOfProbabilitySuccess=0D;
        Set<Nucleotide> basesUsed = EnumSet.noneOf(Nucleotide.class);
        List<Nucleotide> basesToConsider = new ArrayList<Nucleotide>(BASES_TO_CONSIDER);
        Collections.sort(basesToConsider, new LowestProbabilityComparator(normalizedErrorProbabilityStruct));
        do
        {
            Nucleotide baseWithLowestErrorProbability = basesToConsider.remove(0);
            sumOfProbabilitySuccess += (1 - normalizedErrorProbabilityStruct.getProbabilityFor(
                    baseWithLowestErrorProbability));
            basesUsed.add(baseWithLowestErrorProbability);
            errorProbabilityOfAmbiguity = 1-sumOfProbabilitySuccess;
        }while( sumOfProbabilitySuccess <1D && underThreshold(errorProbabilityOfAmbiguity)
                && basesUsed.size()< baseCount );
        
        return basesUsed;
    }
    
    private boolean underThreshold(double errorProbability){
        return PhredQuality.computeQualityScore(errorProbability) < getHighQualityThreshold().getQualityScore();
     }
    /**
     * Sorts {@link ConsensusProbabilities} by comparing the 
     * probability of the given {@link Nucleotide}.
     * @author dkatzel
     *
     *
     */
    private static class LowestProbabilityComparator implements Comparator<Nucleotide>{
        private final ConsensusProbabilities probabilityStruct;
        LowestProbabilityComparator(ConsensusProbabilities probabilityStruct){
            this.probabilityStruct = probabilityStruct;
        }
        @Override
        public int compare(Nucleotide o1, Nucleotide o2) {
            return probabilityStruct.getProbabilityFor(o1).compareTo(probabilityStruct.getProbabilityFor(o2));
        }
        
    }
}
