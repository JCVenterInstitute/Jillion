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
    private final Map<NucleotideGlyph, Double> probabilityMap;
    
    public ProbabilityStruct(Map<NucleotideGlyph, Double> probabilityMap){
        this.probabilityMap = Collections.unmodifiableMap(new EnumMap<NucleotideGlyph, Double>(probabilityMap));
    }
    public Double getProbabilityFor(NucleotideGlyph base){
        return probabilityMap.get(base);
    }
    public ProbabilityStruct (NucleotideGlyph consensus,int cumulativeQualityValue){
        double probability = Math.pow(0.1, cumulativeQualityValue/10D);
        probabilityMap = new EnumMap<NucleotideGlyph, Double>(NucleotideGlyph.class);
        for(NucleotideGlyph currentBase : BASES_TO_CONSIDER){
            if(currentBase == consensus){
                probabilityMap.put(currentBase, Double.valueOf(1 - probability));
            }
            else{
                //evenly distribute probability around
                //to the rest of the basecalls.
                probabilityMap.put(currentBase, probability* 0.25D);
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