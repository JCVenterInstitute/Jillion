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
package org.jcvi.jillion.trace.fastq;

import java.util.Optional;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code FastqRecordBuilder} is a {@link Builder} implementation
 * that can create new instances of {@link FastqRecord}s.
 * @author dkatzel
 *
 */
public interface FastqRecordBuilder extends Builder<FastqRecord>{

    /**
     * Add the given String to this fastq record as a comment
     * which will get returned by {@link FastqRecord#getComment()}.
     * Calling this method more than once will cause the last value to
     * overwrite the previous value.
     * @param comments the comment to make for this record;
     * if this is set to null (the default) then this
     * record has no comment.
     * @return this.
     */
    FastqRecordBuilder comment(String comments);

    /**
     * Create a new {@link FastqRecord} instance using the given parameters.
     * 
     * @return a new {@link FastqRecord}; will never be null.
     * 
     * @throws IllegalArgumentException
     *             if the length of the {@link NucleotideSequence} and
     *             {@link QualitySequence} don't match. (Since 5.3)
     */
    @Override
    FastqRecord build();

    /**
     * Get the current value for the comments.
     * @return an Optional which either contains a non-null
     * String if there are current comments; or empty Optional if there are
     * no comments.
     * 
     * @since 5.3
     */
    Optional<String> comment();

    /**
     * Get the current id value.
     * @return the id of this record as a String; will never be null
     * but may contain spaces.
     * 
     * @since 5.3
     */
    String id();

    /**
     * Set the new value of the id for this record.
     * @param id the new id to use; can not be null.
     * @throws NullPointerException if id is null.
     * @since 5.3
     * @return this
     */
    FastqRecordBuilder id(String id);

    /**
     * Get the current basecalls value.
     * @return the basecalls of this record as a String; will never be null.
     * 
     * @since 5.3
     */
    NucleotideSequence basecalls();

    /**
     * Set the new value of the basecalls for this record.
     * @param basecalls the new basecalls to use; can not be null.
     * @throws NullPointerException if basecalls is null.
     * @since 5.3
     * @return this
     */
    FastqRecordBuilder basecalls(NucleotideSequence basecalls);

    /**
     * Get the current qualities value.
     * @return the qualities of this record as a String; will never be null.
     * 
     * @since 5.3
     */
    QualitySequence qualities();

    /**
     * Set the new value of the qualities for this record.
     * @param qualities the new qualities to use; can not be null.
     * @throws NullPointerException if qualities is null.
     * @return this
     * 
     * @since 5.3
     */
    FastqRecordBuilder qualities(QualitySequence qualities);
    /**
     * Trim both the nucleotide and quality sequences to only
     * include the given Range.
     * @param trimRange the range to trim to; can not be null.
     * @return this
     * 
     * @throws NullPointerException if trimRange is null.
     * 
     * @since 5.3
     */
    FastqRecordBuilder trim(Range trimRange);
    
    
    /**
     * Create a new instance of {@link FastqRecordBuilder}
     * with the given required parameters.
     * @param id the id of this fastq record;
     * can not be null but may contain whitespace.
     * @param basecalls the {@link NucleotideSequence} for this fastq record;
     * can not be null.
     * @param qualities the {@link QualitySequence} for this fastq record;
     * can not be null.
     * @throws NullPointerException if any parameters are null.
     * @throws IllegalArgumentException if the length of the
     *  {@link NucleotideSequence} and {@link QualitySequence}
     *  don't match.
     *  
     *  @return a new FastqRecordBuilder instance initialized with the given values.
     */
    public static FastqRecordBuilder create(String id, NucleotideSequence bases, QualitySequence qualities){
        return new FastqRecordBuilderImpl(id, bases, qualities);
    }
    
    /**
     * Create a new instance of {@link FastqRecordBuilder}
     * with the given parameters.
     * @param id the id of this fastq record;
     * can not be null but may contain whitespace.
     * @param basecalls the {@link NucleotideSequence} for this fastq record;
     * can not be null.
     * @param qualities the {@link QualitySequence} for this fastq record;
     * can not be null.
     * @param optionalComment the optional comment; may be null to mean no comment.
     * 
     * @throws NullPointerException if id, bases or qualities are null.
     * @throws IllegalArgumentException if the length of the
     *  {@link NucleotideSequence} and {@link QualitySequence}
     *  don't match.
     *  
     *  @return a new FastqRecordBuilder instance initialized with the given values.
     */
    public static FastqRecordBuilder create(String id, NucleotideSequence bases, QualitySequence qualities, String optionalComment){
        return new FastqRecordBuilderImpl(id, bases, qualities).comment(optionalComment);
    }
}
