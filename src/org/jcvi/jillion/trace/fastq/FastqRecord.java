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
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.Defline;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.Trace;
/**
 * {@code FastqRecord} is an object representation 
 * of a read from a fastq encoded file.
 * @author dkatzel
 *
 */
public interface FastqRecord extends Trace, Defline{
    
	/**
     * 
     * Get the Id of this {@link FastqRecord}.
     * <strong>Note: </strong> It is possible that this
     * id has multiple "words" with whitespace in between
     * if this record was from a CASAVA 1.8 run.
     * This can cause problems with downstream software
     * if whitespace in ids is not allowed.
     * @return This id of this {@link FastqRecord} as a String
     */
	@Override
    String getId();
    /**
     * Gets the {@link NucleotideSequence} of this record.
     * The nucleotide sequence should be the same
     * length as the {@link QualitySequence} returned by
     * {@link #getQualitySequence()}.
     * @return a {@link NucleotideSequence} instance;
     * never null.
     */
	@Override
    NucleotideSequence getNucleotideSequence();
    /**
     * Gets the {@link QualitySequence} of this record.
     * The quality sequence should be the same
     * length as the {@link NucleotideSequence} returned by
     * {@link #getNucleotideSequence()}.
     * @return a {@link QualitySequence} instance;
     * never null.
     */
	@Override
    QualitySequence getQualitySequence();

    /**
     * Get the comment (if any) associated with this record.
     * @return A <code>String</code> of the comment
     * or {@code null} if there is no comment.
     */
    String getComment();

    /**
     * The HashCode of a {@link FastqRecord} is computed using
     * the id, {@link NucleotideSequence} and {@link QualitySequence}
     * values.
     * @return an int.
     */
    @Override
    int hashCode();
    /**
     * Two {@link FastqRecord}s are equal
     * if and only if they have equal
     * ids, {@link NucleotideSequence}s
     * and {@link QualitySequence}s.
     * Any comments returned by {@link #getComment()}
     * are ignored for equality testing.
     * @param obj the other object to compare.
     * @return {@code true} if the other object is also a {@link FastqRecord}
     * and has the same id, nucleotide and quality sequence.
     */
    @Override
    boolean equals(Object obj);
    
    /**
     * Get the length of the nucleotide and quality sequences.  
     * Both should be the same length.
     * 
     * @implSpec the default implementation
     * of this method calls
     * {@code getNucleotideSequence().getLength()} but some {@link FastqRecord}
     * implementations may optimize this return value.
     * 
     * @return the length of this record sequence.
     * 
     * @since 5.0
     */
    default long getLength(){
    	return getNucleotideSequence().getLength();
    }
    
    
    /**
     * Get the average quality score as a double.
     * This calculation only works on a sequence
     * that is not empty.
     * @return the avg quality score as a double.
     * @throws ArithmeticException if the sequence length is 0.
     * 
     * @implSpec the default implementation
     * of this method calls
     * {@code getQualitySequence().getAvgQuality()} but some {@link FastqRecord}
     * implementations may optimize this computation.
     * @since 5.2
     */
    default double getAvgQuality() throws ArithmeticException{
        return getQualitySequence().getAvgQuality();
    }
    /**
     * Create a new {@link FastqRecordBuilder}
     * instance using the values from this FastqRecord.
     * 
     * 
     * @return a new {@link FastqRecordBuilder} instance will never be null.
     * @implSpec the default implementation is the same as
     * {@code return new FastqRecordBuilder(this); }
     * @since 5.3
     */
    default FastqRecordBuilder toBuilder(){
        return new FastqRecordBuilderImpl(this);
    }
}
