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
import java.util.Iterator;

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
        return new QualitySequenceBuilder(toArray(range)).iterator();
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

    @Override
    public double getAvgQuality() throws ArithmeticException {
        long total = 0; 
        char[] chars = encodedQualities.toCharArray();
        if(chars.length ==0){
            return 0D;
        }
        for(int i=0; i< chars.length; i++){
           total += chars[i];
        }
        
        double avg = ((double)total)/chars.length;
        return avg - codec.getOffset();
    }

    @Override
    public PhredQuality getMinQuality() {
        char min = 256; //should be bigger than any encoded quality
        char[] chars = encodedQualities.toCharArray();
        if(chars.length ==0){
            return null;
        }
        for(int i=0; i< chars.length; i++){
            char c = chars[i];
            if(c < min){
                min = c;
            }
        }
        return codec.decode(min);
    }

    @Override
    public PhredQuality getMaxQuality() {
        char max = 0;
        char[] chars = encodedQualities.toCharArray();
        if(chars.length ==0){
            return null;
        }
        for(int i=0; i< chars.length; i++){
            char c = chars[i];
            if(c > max){
                max = c;
            }
        }
        return codec.decode(max);
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
