/*
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import static org.jcvi.assembly.slice.consensus.ConsensusUtil.*;
/**
 * <code>ConicConsensusCaller</code> calls consensus using
 * the "Conic Ambiguity Model".
 * 
 * The model is called the Conic Model because it uses a multidimensional cone
 * to define the region of ambiguity. Cumulative quality values
 * for each base call are represented by vectors, if the resulting vector
 * falls within the region of ambiguity, then the consensus is ambiguous.
 * Geometric symmetry allows the math
 * to be simplified to only 2 dimensions
 * 
 * @author dkatzel
 * @see <a href= "http://slicetools.sourceforge.net/libSlice/conic.html">
 * Slice Tools Conic Ambiguity Model</a>
 *
 */
public class ConicConsensusCaller extends AbstractChurchillWatermanConsensusCaller{
    /**
     * Under the conic model, all slices are ambiguous 
     * at 45 degree angle.
     */
    private static final double MAX_EFFECTIVE_ANGLE = 45D;
    /**
     * This was the angle that was found to most often match
     * expert human consensus callers in experiments performed in 2003
     * on Sanger data.
     */
    public static final double DEFAULT_CONIC_AMBIGUITY_ANGLE = 36.8698977D;
    
    private final double effectiveAngle;
    private final double lowerRadians, upperRadians;
    private final double lowerlimit, upperlimit;
    /**
     * Create a new instance using the default amiguity angle.
     * @param highQualityThreshold
     */
    public ConicConsensusCaller(PhredQuality highQualityThreshold) {
       this(DEFAULT_CONIC_AMBIGUITY_ANGLE, highQualityThreshold);
        
    }
    public ConicConsensusCaller(double ambiguityAngle, PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
        effectiveAngle = computeEffectiveAngle(ambiguityAngle);
        lowerRadians = computeLowerRadians(effectiveAngle);
        upperRadians = computeUpperRadians(effectiveAngle);
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

    private Set<NucleotideGlyph> getBasesUsedTowardsAmbiguity(
            Map<NucleotideGlyph, Integer> qualityValueSumMap,
            MaxQualityStruct maxQualityStruct) {
        Set<NucleotideGlyph> basesTowardsAmbiguity = EnumSet.noneOf(NucleotideGlyph.class);
        basesTowardsAmbiguity.add(maxQualityStruct.base);
        for(NucleotideGlyph base : BASES_TO_CONSIDER){
            if(base !=maxQualityStruct.base ){
                double tangent = qualityValueSumMap.get(base).doubleValue()/maxQualityStruct.sum;
                if(tangent < upperlimit && tangent > lowerlimit){
                    basesTowardsAmbiguity.add(base);
                }
            }
        }
        return basesTowardsAmbiguity;
    }
    private MaxQualityStruct createMaxQualityStruct(
            Map<NucleotideGlyph, Integer> qualityValueSumMap) {
        int maxQualitySum=0;
        NucleotideGlyph maxQualityBase= null;
        for(NucleotideGlyph base : BASES_TO_CONSIDER){
            int qualitySum = qualityValueSumMap.get(base);
            if(qualitySum > maxQualitySum){
                maxQualitySum = qualitySum;
                maxQualityBase = base;
            }
        }
        return  new MaxQualityStruct(maxQualityBase, maxQualitySum);
    }

    private static class MaxQualityStruct{
        public MaxQualityStruct(NucleotideGlyph base, int sum) {
            this.base = base;
            this.sum = sum;
        }
        private final int sum;
        private final NucleotideGlyph base;
    }

    @Override
    protected NucleotideGlyph getConsensus(
            ProbabilityStruct normalizedErrorProbabilityStruct, Slice slice) {
       
        Map<NucleotideGlyph, Integer> qualityValueSumMap = generateQualityValueSumMap(slice);        
        MaxQualityStruct maxQualityStruct = createMaxQualityStruct(qualityValueSumMap);
        Set<NucleotideGlyph> basesTowardsAmbiguity = getBasesUsedTowardsAmbiguity(qualityValueSumMap, maxQualityStruct);
        return NucleotideGlyph.getAmbiguityFor(basesTowardsAmbiguity);
    }

    
}
