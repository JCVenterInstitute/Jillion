package org.jcvi.jillion.trim;

import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;

abstract class AbstractEdgeQualityTrimmer implements QualityTrimmer {

    private final PhredQuality threshold;
    private final boolean trimFromLeading;
    
    private static final Range EMPTY = Range.ofLength(0);
    
    public AbstractEdgeQualityTrimmer(int threshold, boolean trimFromLeading) {
        this(PhredQuality.valueOf(threshold), trimFromLeading);
    }
    public AbstractEdgeQualityTrimmer(PhredQuality threshold, boolean trimFromLeading) {
        Objects.requireNonNull(threshold);
        
        this.threshold = threshold;
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
                if(threshold.compareTo(quals[i]) <=0){
                    return Range.of(i, quals.length -1);
                }
            }
        }else{
            for(int i=quals.length -1; i>=0; i--){
                if(threshold.compareTo(quals[i]) <=0){
                    return Range.ofLength(i+1);
                }
            }
        }
        return EMPTY;
    }

}
