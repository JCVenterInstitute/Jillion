package org.jcvi.jillion.trim.trimmomatic;

import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.trim.QualityTrimmer;

abstract class AbstractEdgeQualityTrimmer implements QualityTrimmer {

    private final byte threshold;
    private final boolean trimFromLeading;
    
    private static final Range EMPTY = Range.ofLength(0);
    
    
    public AbstractEdgeQualityTrimmer(PhredQuality threshold, boolean trimFromLeading) {
        Objects.requireNonNull(threshold);
        
        this.threshold = threshold.getQualityScore();
        this.trimFromLeading = trimFromLeading;
    }

    @Override
    public Range trim(QualitySequenceBuilder builder) {
        return trim(builder.toArray());
    }
    
    @Override
    public Range trim(QualitySequence qualities) {
        return trim(qualities.toArray());
    }
    
    private Range trim(byte[] quals){
        if(trimFromLeading){
            for(int i=0; i< quals.length; i++){
                if(threshold <= quals[i]){
                    return Range.of(i, quals.length -1);
                }
            }
        }else{
            for(int i=quals.length -1; i>=0; i--){
                if(threshold <= quals[i]){
                    return Range.ofLength(i+1);
                }
            }
        }
        return EMPTY;
    }

}
