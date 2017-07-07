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

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.trim.QualityTrimmer;

/**
 * {@link QualityTrimmer} implementation of Trimomatic's MaximumInfo algorithm
 * which is an adaptive quality trimmer which balances read length and error
 * rate to maximize the value of each read. The algorithm tries to balance read
 * length and quality to maximize the unique alignment length.
 * <p>
 * From the Trimomatic Manual: <blockquote> For many applications, the “value”
 * of a read is a balance between three factors:
 * <ul>
 * <li>
 * Minimal read length: The read needs to be long enough that it can be uniquely
 * located within the target sequence. Extremely short reads, which can be
 * placed into many different locations within the target sequence, provide
 * little value. The length required before a read is likely to be unique
 * depends on the size and complexity of the target sequence, but a typical
 * target length would be in the order of 40 bases.</li>
 * <li>
 * Additional read length: There may be added value in retaining additional
 * bases, beyond those needed to uniquely place a read. This is dependent
 * primarily on the application. For pure counting applications, such as
 * RNA-Seq, unique placement is sufficient. For assembly or variant finding
 * tasks, additional bases provide extra evidence for or against putative
 * results, and thus can be valuable.</li>
 * <li>
 * Error sensitivity: The downstream analysis can be more or less sensitive to
 * errors within the data. This is determined by the tools and settings used.
 * One extreme would be tools were a single base error would cause the entire
 * read to be ignored, which favours aggressive quality trimming. The other
 * extreme would be tools which can tolerate or even correct a large number of
 * errors, which favour retaining as much data as possible.</li>
 * </ul>
 * Two user provided values are used, the “target read length”, which affects
 * the first scoring factor, and the “strictness” which affects the balance
 * between the second and third factor. Trimming is applied to
 * 
 * </blockquote>
 * 
 * @author dkatzel
 *
 * @since 5.2
 */
public class MaxInfoTrimmer implements QualityTrimmer{

    private static int MAX_READ_LENGTH = 1_000; // match trimmomatic which only goes to 1000 which might be too short
    private final long[] qualLookup;
    private final long[] factorLookup;
    /**
     * Create a new Trimmer object.
     * @param targetLength Minimal read length: The read needs to be long enough that it can be uniquely located
within the target sequence. Extremely short reads, which can be placed into many
different locations within the target sequence, provide little value. The length required
before a read is likely to be unique depends on the size and complexity of the target
sequence, but a typical target length would be in the order of 40 bases. must be &ge; 1.

     * @param strictness the a value between 0 and 1 that affects the balance between the allowed error rate
     * and additional length.
     */
    public MaxInfoTrimmer(int targetLength, double strictness){
        if(targetLength <1){
            throw new IllegalArgumentException("target length must be >=1");
        }
        if(strictness < 0 || strictness >1){
            throw new IllegalArgumentException("strictness must be between 0 and 1");
        }
        
        double[] qualLookup = new double[PhredQuality.MAX_VALUE +1];
        for(int i=0; i< qualLookup.length; i++){
           qualLookup[i] = Math.log(1-Math.pow(0.1, (0.5+i)/10.0))*strictness;
        }
        
        double[] factorLookup = new double[MAX_READ_LENGTH];
        double leniency = 1 - strictness;
        for(int i=0; i< MAX_READ_LENGTH; i++){            
            factorLookup[i] = Math.log(1D /(1 + Math.exp(targetLength - i -1))) + (Math.log(i +1) * (leniency));
        }
        //we have to match trimomatic 100% so we have to normalize exactly like they do
        //and use longs instead of doubles...
        double ratio = Math.max(calcNormalizationRatio(qualLookup, MAX_READ_LENGTH*2),
                calcNormalizationRatio(factorLookup, MAX_READ_LENGTH*2)
                );
        this.qualLookup = normalize(qualLookup, ratio);
        this.factorLookup = normalize(factorLookup, ratio);
        
    }

    private static double calcNormalizationRatio(double array[], int margin)
    {
            double maxVal=array[0];
    
            for(int i=1;i<array.length;i++)
                    {
                    double val=Math.abs(array[i]);
                    if(val>maxVal)
                    maxVal=val;
            }
    
            return Long.MAX_VALUE/(maxVal*margin);  
    }
    
    private static long[] normalize(double array[], double ratio)
    {
            long out[]=new long[array.length];
            
            for(int i=0;i<array.length;i++)
                    out[i]=(long)(array[i]*ratio);
                    
            return out;
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
        double maxScore = -Double.MAX_VALUE;
        
        long acumulativeQual=0;
        for(int i=0; i< quals.length; i++){
            acumulativeQual +=qualLookup[quals[i]];
           long score= acumulativeQual + factorLookup[i];
           
           if(score >= maxScore){
               maxScore = score;
               bestOffset =i;
           }
        }

        if(bestOffset < 0 || maxScore ==0){
            return Range.ofLength(0);
        }
        return Range.ofLength(bestOffset +1);
    }
}
