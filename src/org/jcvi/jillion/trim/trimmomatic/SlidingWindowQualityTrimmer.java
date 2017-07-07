/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.trim.trimmomatic;

import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.trim.QualityTrimmer;
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
    private final double requiredQuality;
    
    private static Range EMPTY = Range.ofLength(0);
    
    public SlidingWindowQualityTrimmer(int windowSize, PhredQuality requiredQuality){
        Objects.requireNonNull(requiredQuality);
        if(windowSize < 1){
            throw new IllegalArgumentException("window size must be >= 1");
        }
        
        this.windowSize = windowSize;
        this.requiredQuality = requiredQuality.getQualityScore();
    }
    @Override
    public Range trim(QualitySequence qualities) {
        byte[] quals = qualities.toArray();
        //match trimmomatic and always trim off everything
        //when the read is too short
        if(quals.length < windowSize){
            return EMPTY;
        }
        return trim(quals);
    }
    
    @Override
    public Range trim(QualitySequenceBuilder builder) {
        byte[] quals = builder.toArray();
        //match trimmomatic and always trim off everything
        //when the read is too short
        if(quals.length < windowSize){
            return EMPTY;
        }
       
        return trim(quals);
    }
    
    
    
    private Range trim(byte[] quals) {
        
        int currentWindowStart=0;
        while(currentWindowStart < quals.length){
        double avgQual = computeAvgQualFor(quals, currentWindowStart);
            if(avgQual < requiredQuality){
                //entered bad range
                break;
            }
            currentWindowStart++;
        }
        //if we get this far we have good quality
        //all the way until the end of full windows
        
        
        
        //trimmomatic appears to trim off
        //any end bases that are below the required quality?
        
        //our currentWindowStart is one too far
        //it's off the end or into a bad window
        currentWindowStart--;
        //now look for bad quality bases from the end of our last good window
        for(int i= Math.min(quals.length-1, currentWindowStart+windowSize-1); i>=0; i--){
                if(quals[i] >= requiredQuality){
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
        return total/(double)(end - currentWindowStart);
    }

}
