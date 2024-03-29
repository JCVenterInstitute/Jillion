/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
import org.jcvi.jillion.core.util.SingleThreadAdder;
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
abstract class AbstractChurchillWatermanConsensusCaller implements ConsensusCaller{
    
   
	 private final PhredQuality highQualityThreshold;
	  
	    
	   

	/**
	 * Creates a new Churchill-Waterman consensus caller
	 * instance.
	 * @param highQualityThreshold the quality threshold
	 * whose error probability is used to determine
	 * which basecalls to consider towards
	 * the consensus based on cumulative quality
	 * values.
	 * @throws NullPointerException if highQualityThreshold is null
	 */
    public AbstractChurchillWatermanConsensusCaller(
            PhredQuality highQualityThreshold) {
    	if(highQualityThreshold ==null){
    		throw new NullPointerException("high quality threshold can not be null");
    	}
    	this.highQualityThreshold = highQualityThreshold;
    }
    
    public PhredQuality getHighQualityThreshold() {
        return highQualityThreshold;
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
    public final ConsensusResult callConsensus(Slice slice) {
        if(slice.getCoverageDepth() ==0){
            //by definition, an empty slice is a Gap
            return new DefaultConsensusResult(Nucleotide.Gap,0);
        }
        Map<Nucleotide, Integer> qualityValueSumMap = generateQualityValueSumMap(slice);
        ConsensusProbabilities normalizedErrorProbabilityStruct = generateNormalizedProbabilityStruct(qualityValueSumMap);
        Nucleotide consensus=  getConsensus(normalizedErrorProbabilityStruct,slice);
        return new DefaultConsensusResult(consensus,
                
                getErrorProbability(normalizedErrorProbabilityStruct,
                        slice));
    }
    
    protected final Map<Nucleotide, Integer> generateQualityValueSumMap(Slice slice) {
        Map<Nucleotide, SingleThreadAdder> qualityValueSumMap = initalizeNucleotideMap();
        for(SliceElement sliceElement : slice){
            Nucleotide basecall =sliceElement.getBase();
            final SingleThreadAdder previousSum = qualityValueSumMap.get(basecall);
            //ignore not ACGT-?
            if(previousSum!=null){
               previousSum.add(sliceElement.getQualityScore());
            }
            
        }
        Map<Nucleotide, Integer> map = new EnumMap<>(Nucleotide.class);
        for(Entry<Nucleotide, SingleThreadAdder> entry :qualityValueSumMap.entrySet()){
        	map.put(entry.getKey(), entry.getValue().intValue());
        }
        return map;
    }

    private Map<Nucleotide, SingleThreadAdder> initalizeNucleotideMap() {
        Map<Nucleotide, SingleThreadAdder> map = new EnumMap<Nucleotide, SingleThreadAdder>(Nucleotide.class);
        for(Nucleotide glyph : ConsensusUtil.BASES_TO_CONSIDER){
            map.put(glyph, new SingleThreadAdder(0));
        }
        return map;
    }


    private int getErrorProbability(
            ConsensusProbabilities normalizedErrorProbabilityStruct,
            Slice slice) {
        double normalizedProbability= getProbabilityFor(normalizedErrorProbabilityStruct);
        if(normalizedProbability == 0.0D){
            //special case if we only have matches, then sum slice qualities
            int sum =0;
            for(SliceElement element : slice){
                sum += element.getQualityScore();
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
