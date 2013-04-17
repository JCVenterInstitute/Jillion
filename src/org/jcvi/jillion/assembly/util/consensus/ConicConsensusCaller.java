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

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * <code>ConicConsensusCaller</code> calls consensus using
 * the "Conic Ambiguity Model".
 * 
 * The model is called the Conic Model because it uses a multidimensional cone
 * to define the region of ambiguity. Cumulative quality values
 * for each base call are represented by vectors, if the resulting vector
 * falls within the region of ambiguity, then the consensus is ambiguous.
 * Geometric symmetry allows the math
 * to be simplified to only 2 dimensions.
 * 
 * @author dkatzel
 * @see <a href= "http://slicetools.sourceforge.net/libSlice/conic.html">
 * Slice Tools Conic Ambiguity Model</a>
 *
 */
public class ConicConsensusCaller extends AbstractChurchillWatermanConsensusCaller{
    
	/**
	 * The lower limit (in radians) of the angle
	 * that is allowed to be considered towards the consensus.
	 */
    private final double lowerlimit;
    /**
	 * The upper limit (in radians) of the angle
	 * that is allowed to be considered towards the consensus.
	 */
    private final double upperlimit;
    
	/**
     * Under the conic model, all slices are ambiguous 
     * at 45 degree angle.
     */
    private static final double MAX_EFFECTIVE_ANGLE = 45D;
    /**
     * This was the angle that was found to most often match
     * expert human consensus callers in TIGR experiments performed in 2003
     * on Sanger data.
     * <p/>
     * TIGR data to validate the conic model and was used to come up with 
     * this angle is available http://slicetools.sourceforge.net/libSlice/worksheet.xls
     * and http://slicetools.sourceforge.net/libSlice/results.xls
     */
    public static final double DEFAULT_CONIC_AMBIGUITY_ANGLE = 36.8698977D;

    /**
     * Create a new instance using the default amiguity angle.
     * @param highQualityThreshold
     */
    public ConicConsensusCaller(PhredQuality highQualityThreshold) {
       this(DEFAULT_CONIC_AMBIGUITY_ANGLE, highQualityThreshold);
        
    }
    public ConicConsensusCaller(double ambiguityAngle, PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
        double effectiveAngle = computeEffectiveAngle(ambiguityAngle);
        double lowerRadians = computeLowerRadians(effectiveAngle);
        double upperRadians = computeUpperRadians(effectiveAngle);
        lowerlimit = Math.tan(lowerRadians);
        upperlimit = Math.tan(upperRadians);
    }

    private double computeLowerRadians(double angle) {
        return Math.toRadians(MAX_EFFECTIVE_ANGLE - angle);
    }
    private double computeUpperRadians(double angle) {
        return Math.toRadians(MAX_EFFECTIVE_ANGLE + angle);
    }
    private double computeEffectiveAngle(double ambiguityAngle) {
        return Math.min(MAX_EFFECTIVE_ANGLE, ambiguityAngle/2);
    }

    private Set<Nucleotide> getBasesUsedTowardsAmbiguity(
            Map<Nucleotide, Integer> qualityValueSumMap,
            MaxQualityStruct maxQualityStruct) {
        Set<Nucleotide> basesTowardsAmbiguity = EnumSet.noneOf(Nucleotide.class);
        final Nucleotide maxQualityBase = maxQualityStruct.base;
        basesTowardsAmbiguity.add(maxQualityBase);
       
        for(Nucleotide base : ConsensusUtil.BASES_TO_CONSIDER){
            if(base !=maxQualityBase ){
                double tangent = qualityValueSumMap.get(base).doubleValue()/maxQualityStruct.sum;
                if(tangent < upperlimit && tangent > lowerlimit){
                    basesTowardsAmbiguity.add(base);
                }
            }
        }
        return basesTowardsAmbiguity;
    }
    private MaxQualityStruct createMaxQualityStruct(
            Map<Nucleotide, Integer> qualityValueSumMap) {
        int maxQualitySum=0;
        Nucleotide maxQualityBase= Nucleotide.Gap;
        for(Nucleotide base : ConsensusUtil.BASES_TO_CONSIDER){
            int qualitySum = qualityValueSumMap.get(base);
            if(qualitySum > maxQualitySum){
                maxQualitySum = qualitySum;
                maxQualityBase = base;
            }
        }
        return  new MaxQualityStruct(maxQualityBase, maxQualitySum);
    }

    private static class MaxQualityStruct{
    	
		private final int sum;
		private final Nucleotide base;
         
        public MaxQualityStruct(Nucleotide base, int sum) {
            this.base = base;
            this.sum = sum;
        }
       
    }

    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities, Slice slice) {
       
        Map<Nucleotide, Integer> qualityValueSumMap = generateQualityValueSumMap(slice);        
        MaxQualityStruct maxQualityStruct = createMaxQualityStruct(qualityValueSumMap);
        Set<Nucleotide> basesTowardsAmbiguity = getBasesUsedTowardsAmbiguity(qualityValueSumMap, maxQualityStruct);
        return Nucleotide.getAmbiguityFor(basesTowardsAmbiguity);
    }

    
}
