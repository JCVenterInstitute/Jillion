/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.jillion.core.qual;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;

/**
 * {@code QualitySequence} is a marker interface
 * for {@link Sequence} implementations
 * that encode {@link PhredQuality} values.
 * @author dkatzel
 *
 *
 */
public interface QualitySequence extends Sequence<PhredQuality>{

	/**
     * Two {@link QualitySequence}s are equal
     * if they contain the same {@link PhredQuality}s 
     * in the same order.
     * <p>
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object o);
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    int hashCode();
    
    
    /**
     * Create an new array of bytes of length {@link #getLength()}
     * where index in the array is the ith quality score stored
     * as a byte.  This method may be expensive to perform
     * depending on the size of the sequence and the encoding used.
     * @return a new byte array, never null.
     */
    byte[] toArray();
    /**
     * Create an new array of bytes of length {@link Range#getLength()}
     * where index in the array is the ith quality score stored
     * as a byte.  This method may be expensive to perform
     * depending on the size of the sequence and the encoding used.
     * 
     * @param range the subrange of quality scores to get; can not be null.
     * 
     * @return a new byte array, never null.
     * 
     * @since 5.2
     * 
     * @throws NullPointerException if range is null.
     */
    default byte[] toArray(Range range){
        return Arrays.copyOfRange(toArray(), (int)range.getBegin(), (int)range.getEnd()+1);
    }
    /**
     * Get the average quality score as a double.
     * This calculation only works on a sequence
     * that is not empty.
     * @return the avg quality score as a double.
     * @throws ArithmeticException if the sequence length is 0.
     */
    double getAvgQuality() throws ArithmeticException;
    /**
     * Get the min {@link PhredQuality} in the 
     * Sequence.
     * @return a {@link PhredQuality} or {@code null}
     * if the sequence is empty.
     */
    PhredQuality getMinQuality();
    /**
     * Get the min {@link PhredQuality} in the 
     * Sequence.
     * @return a {@link PhredQuality} or {@code null}
     * if the sequence is empty.
     */
    PhredQuality getMaxQuality();
    /**
     * Create a new Builder object that is initialized
     * to the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * 
     * @return a new Builder instance, will never be null.
     * @since 5.0
     */
    default QualitySequenceBuilder toBuilder(){
        return new QualitySequenceBuilder(this);
    }
    
    /**
     * Get the Java 8 {@link DoubleSummaryStatistics} of all the
     * quality values in this sequence.
     * 
     * @return an Optional which will be empty if the sequence length is 0
     * or else a non-null {@link DoubleSummaryStatistics}.
     * 
     * @since 5.3
     */
    default Optional<DoubleSummaryStatistics> getSummaryStats(){
        if(getLength() <=0){
            return Optional.empty();
        }
        return Optional.of(StreamSupport.stream(this.spliterator(), false)
                    .collect(Collectors.summarizingDouble(PhredQuality::getQualityScore)));
                    
    }
    
    /**
     * Get the Java 8 {@link DoubleSummaryStatistics} of just the
     * quality values in this sequence within the specified sub Range.
     * 
     * @param subRange the subrange
     * @return an Optional which will be empty if the sequence length is 0
     * or else a non-null {@link DoubleSummaryStatistics}.
     * 
     * @since 5.3
     * 
     * @throws NullPointerException  if range is null.
     * @throws IndexOutOfBoundsException if Range contains values outside of the possible sequence offsets.
     */
    default Optional<DoubleSummaryStatistics> getSummaryStats(Range subRange){
        Iterator<PhredQuality> iter = this.iterator(subRange);
        if(!iter.hasNext()){
            return Optional.empty();
        }
       
        return Optional.of(StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED), false)
                    .collect(Collectors.summarizingDouble(PhredQuality::getQualityScore)));
                    
    }
}
