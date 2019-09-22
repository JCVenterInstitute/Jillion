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
package org.jcvi.jillion.internal.trace.fastq;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalDouble;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
/**
 * A special implementation of {@link QualitySequence}
 * that decodes on the fly from the encoded String from a 
 * fastq file.  This allows random access and conversion
 * to byte arrays and {@link QualitySequenceBuilder}s
 * without the overhead of building a {@link QualitySequence}
 * using the builder and trying multiple encodings.
 * 
 * This should be faster than the normal QualitySequence implementations 
 * when doing simple iteration and even
 * trimming when parsing directly from fastq files.
 * 
 * @author dkatzel
 * @since 5.2
 */
class ParsedQualitySequence implements QualitySequence{

    private final FastqQualityCodec codec;
    private final String encodedQualities;
    private DoubleSummaryStatistics stats;
    /**
     * cached hashcode.
     */
    private int hash;
    
    ParsedQualitySequence(FastqQualityCodec codec,
            String encodedQualities) {
        //this is package private, we 
        //don't check for nulls since we assume the caller is trusted.
        this.codec = codec;
        this.encodedQualities = encodedQualities;
    }

    @Override
    public PhredQuality get(long offset) {
        return codec.decode(encodedQualities.charAt((int)offset));
    }

    @Override
    public long getLength() {
        return encodedQualities.length();
    }

    @Override
    public Iterator<PhredQuality> iterator(Range range) {
        return toBuilder(range).iterator();
    }

    @Override
    public QualitySequenceBuilder toBuilder(Range trimRange) {
        return new QualitySequenceBuilder(toArray(trimRange));
    }

    @Override
    public QualitySequence trim(Range trimRange) {
        return new ParsedQualitySequence(codec, encodedQualities.substring((int)trimRange.getBegin(), (int) trimRange.getEnd()+1));
    }

    @Override
    public QualitySequenceBuilder toBuilder() {
        return new QualitySequenceBuilder(toArray());
    }

    @Override
    public Iterator<PhredQuality> iterator() {
        return toBuilder().iterator();
    }

    @Override
    public byte[] toArray() {
        char[] chars = encodedQualities.toCharArray();
        byte[] ret = new byte[chars.length];
        int offset = codec.getOffset();
        for(int i=0; i< chars.length; i++){
            ret[i] = (byte)(chars[i] - offset);
        }
        return ret;
    }
    
    

    @Override
    public byte[] toArray(Range range) {
        char[] chars = encodedQualities.toCharArray();
        byte[] ret = new byte[chars.length];
        int offset = codec.getOffset();
        int end = (int)range.getEnd();
        int shift = (int)range.getBegin();
        for(int i=shift; i<=end; i++){
            ret[i-shift] = (byte)(chars[i] - offset);
        }
        return ret;
    }

    private void computeSummaryStatsIfNeeded(){
        //no synchronization here,
        //in the unlikely event 2 threads enter at the sametime,
        //they will get the same value anyway
        //not worth the performance penalty
        if(stats !=null){
            return;
        }
        if(encodedQualities.isEmpty()){
            stats = new DoubleSummaryStatistics();
            return;
        }
        DoubleSummaryStatistics stats2 = new DoubleSummaryStatistics();
        char[] chars = encodedQualities.toCharArray();
        for(int i=0; i< chars.length; i++){
            stats2.accept(chars[i]);
        }
        
        stats = stats2;
    }
    @Override
    public OptionalDouble getAvgQuality() throws ArithmeticException {
        computeSummaryStatsIfNeeded();
        if(stats.getCount() ==0){
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(stats.getAverage() - codec.getOffset());
    }

    @Override
    public Optional<PhredQuality> getMinQuality() {
        computeSummaryStatsIfNeeded();
        double value = stats.getMin();
        if(Double.POSITIVE_INFINITY == value){
            return Optional.empty();
        }
        return Optional.of(codec.decode((char)value));
    }

    @Override
    public Optional<PhredQuality> getMaxQuality() {
        computeSummaryStatsIfNeeded();
        double value = stats.getMax();
        if(Double.NEGATIVE_INFINITY == value){
            return Optional.empty();
        }
        return Optional.of(codec.decode((char)value));
    }

    @Override
    public int hashCode() {
        if(hash ==0 && getLength() >0){
            hash = Arrays.hashCode(toArray());
        }
       return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ParsedQualitySequence) {
               
            ParsedQualitySequence other = (ParsedQualitySequence) obj;
            if (codec == other.codec) {
               //if same quality code can do quick check
                return encodedQualities.equals(other.encodedQualities);
            }
            //else fall through
        }
        if(obj instanceof QualitySequence){
            return Arrays.equals(toArray(), ((QualitySequence)obj).toArray());
        }
        return false;
    }

}
