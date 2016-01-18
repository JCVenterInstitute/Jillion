package org.jcvi.jillion.trim.trimmomatic;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.trim.QualityTrimmer;

public class MaxInfoTrimmer implements QualityTrimmer{

    private static int MAX_READ_LENGTH = 1_000; // match trimmomatic which only goes to 1000 which might be too short
    private final double[] qualLookup;
    private final double[] factorLookup;
    
    public MaxInfoTrimmer(int targetLength, double strictness){
        if(targetLength <1){
            throw new IllegalArgumentException("target length must be >=1");
        }
        if(strictness < 0 || strictness >1){
            throw new IllegalArgumentException("strictness must be between 0 and 1");
        }
        
        qualLookup = new double[PhredQuality.MAX_VALUE +1];
        for(int i=0; i< qualLookup.length; i++){
            qualLookup[i] = Math.log(PhredQuality.valueOf(i).getErrorProbability()) * strictness;            
        }
        
        factorLookup = new double[MAX_READ_LENGTH];
        double strictnessDistance = 1 - strictness;
        for(int i=0; i< MAX_READ_LENGTH; i++){            
            factorLookup[i] = Math.log(1D /(1 + Math.exp(targetLength - i -1))) + Math.log(i +1) * (strictnessDistance);
        }
        
    }

    @Override
    public Range trim(QualitySequence qualities) {
        return trim(qualities.toArray());

    }
    
    @Override
    public Range trim(QualitySequenceBuilder builder) {
        return trim(builder.toArray());
    }
    
    private Range trim(byte[] quals){
        int bestOffset = -1;
        double maxScore = Double.MIN_VALUE;
        
        for(int i=0; i< quals.length; i++){
           double score= qualLookup[i] + factorLookup[i];
           if(score > maxScore){
               maxScore = score;
               bestOffset =i;
           }
        }
        if(maxScore < 0){
            return Range.ofLength(0);
        }
        return Range.ofLength(bestOffset +1);
    }
}
