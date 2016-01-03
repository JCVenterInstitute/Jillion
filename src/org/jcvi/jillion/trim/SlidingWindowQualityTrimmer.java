package org.jcvi.jillion.trim;

import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
/**
 * A {@link QualityTrimmer} implementation that uses a sliding
 * window of the given length to trim off the 3' 
 * end of reads whose average quality inside
 * the window drops below the specified required minimum value
 * threshold.
 * 
 * This algorithm should be similar to Trimmomatic's SLIDINGWINDOW
 * option.
 * 
 * @author dkatzel
 *
 * @since 5.2
 * 
 * @see <a href="http://bioinformatics.oxfordjournals.org/content/early/2014/04/01/bioinformatics.btu170">
 * Bolger, A. M., Lohse, M., & Usadel, B. (2014). Trimmomatic: A flexible trimmer for Illumina Sequence Data. Bioinformatics, btu170.</a>
 */
public class SlidingWindowQualityTrimmer implements QualityTrimmer {

    private final int windowSize;
    private final PhredQuality requiredQuality;
    
    private static Range EMPTY = Range.ofLength(0);
    
    public SlidingWindowQualityTrimmer(int windowSize, PhredQuality requiredQuality){
        Objects.requireNonNull(requiredQuality);
        if(windowSize < 1){
            throw new IllegalArgumentException("window size must be >= 1");
        }
        
        this.windowSize = windowSize;
        this.requiredQuality = requiredQuality;
    }
    @Override
    public Range trim(QualitySequence qualities) {
        byte[] quals = qualities.toArray();
        if(quals.length ==0){
            return EMPTY;
        }
        
        if(quals.length < windowSize){
            double avgQual = qualities.getAvgQuality();
            if(requiredQuality.compareTo(avgQual) <0){
                return EMPTY;
            }
            return Range.ofLength(quals.length);
        }
        return trim(quals);
    }
    
    @Override
    public Range trim(QualitySequenceBuilder builder) {
        byte[] quals = builder.toArray();
        if(quals.length ==0){
            return EMPTY;
        }
        if(quals.length < windowSize){
            double avgQual = builder.build().getAvgQuality();
            if(requiredQuality.compareTo(avgQual) <0){
                return EMPTY;
            }
            return Range.ofLength(quals.length);
        }
        return trim(quals);
    }
    
    
    
    private Range trim(byte[] quals) {
        
        int currentWindowStart=0;
        while(currentWindowStart < quals.length - windowSize){
        double avgQual = computeAvgQualFor(quals, currentWindowStart);
            if(requiredQuality.compareTo(avgQual) >0){
                //entered bad range
                break;
            }
            currentWindowStart++;
        }
        //if we get this far we have good quality
        //all the way until the end of full windows
        
        //trimmomatic appears to trim off
        //any end bases that are below the required quality?
        for(int i= Math.min(quals.length-1, currentWindowStart+windowSize); i>=0; i--){
            if(requiredQuality.compareTo(quals[i]) <= 0){
                //found good qual base
                return Range.ofLength(i+1);
            }
        }
        return EMPTY;
    }
    private double computeAvgQualFor(byte[] quals, int currentWindowStart) {
        long total=0;
        int end = Math.min(currentWindowStart+ windowSize, quals.length);
        
       for(int i=currentWindowStart; i< end; i++){
           total+=quals[i];
       }
        return total/(end - currentWindowStart);
    }

}
