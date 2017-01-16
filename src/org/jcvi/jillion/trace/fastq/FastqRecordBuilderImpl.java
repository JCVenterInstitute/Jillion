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
package org.jcvi.jillion.trace.fastq;

import java.util.Objects;
import java.util.Optional;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code FastqRecordBuilder} is a {@link Builder} implementation
 * that can create new instances of {@link FastqRecord}s.
 * @author dkatzel
 *
 */
final class FastqRecordBuilderImpl implements FastqRecordBuilder{
	private String comments=null;
	private String id;
	private NucleotideSequence basecalls;
	private QualitySequence qualities;
	
	private boolean lengthsModified=false;
	/**
	 * Create a new builder using the values from the given
	 * {@link FastqRecord}.
	 * 
	 * @param record the fastqRecord to use; can not be null.
	 * @throws NullPointerException if record is null.
	 *  @throws IllegalArgumentException if the length of the
         *  {@link NucleotideSequence} and {@link QualitySequence}
         *  don't match.
         *  
         *  @since 5.3
	 */
	public FastqRecordBuilderImpl(FastqRecord record){
	    this.id = record.getId();
	    this.basecalls = record.getNucleotideSequence();
	    this.qualities = record.getQualitySequence();
	    this.comments = record.getComment();
	    
	    assertValidLength(basecalls, qualities);
	}
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
	 */
	public FastqRecordBuilderImpl(String id, NucleotideSequence basecalls, QualitySequence qualities){
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		if(basecalls ==null){
			throw new NullPointerException("basecalls can not be null");
		}
		if(qualities ==null){
			throw new NullPointerException("qualities can not be null");
		}
		assertValidLength(basecalls, qualities);
		this.id = id;
		this.basecalls = basecalls;
		this.qualities=qualities;
	}

    private void assertValidLength(NucleotideSequence basecalls,
            QualitySequence qualities) {
        long basecallLength = basecalls.getLength();
        long qualityLength = qualities.getLength();
        
        if (basecallLength != qualityLength) {
            throw new IllegalArgumentException(String.format(
                    "basecalls and qualities must have the same length! %d vs %d",
                    basecallLength, qualityLength));
        }
    }
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
	@Override
    public FastqRecordBuilderImpl comment(String comments){
		this.comments = comments;
		return this;
	}

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
    public FastqRecord build() {
        if (lengthsModified) {
            assertValidLength(basecalls, qualities);
        }
        if (comments == null) {
            return new UncommentedFastqRecord(id, basecalls, qualities);
        }
        return new CommentedFastqRecord(id, basecalls, qualities, comments);
    }
    /**
     * Get the current value for the comments.
     * @return an Optional which either contains a non-null
     * String if there are current comments; or empty Optional if there are
     * no comments.
     * 
     * @since 5.3
     */
    @Override
    public Optional<String> comment() {
        return Optional.ofNullable(comments);
    }
    /**
     * Get the current id value.
     * @return the id of this record as a String; will never be null
     * but may contain spaces.
     * 
     * @since 5.3
     */
    @Override
    public String id() {
        return id;
    }
    /**
     * Set the new value of the id for this record.
     * @param id the new id to use; can not be null.
     * @throws NullPointerException if id is null.
     * @since 5.3
     */
    @Override
    public FastqRecordBuilder id(String id) {
        this.id = Objects.requireNonNull(id);
        return this;
    }
    /**
     * Get the current basecalls value.
     * @return the basecalls of this record as a String; will never be null.
     * 
     * @since 5.3
     */
    @Override
    public NucleotideSequence basecalls() {
        return basecalls;
    }
    /**
     * Set the new value of the basecalls for this record.
     * @param basecalls the new basecalls to use; can not be null.
     * @throws NullPointerException if basecalls is null.
     * @since 5.3
     */
    @Override
    public FastqRecordBuilder basecalls(NucleotideSequence basecalls) {
        this.basecalls = Objects.requireNonNull(basecalls);
        lengthsModified=true;
        return this;
    }
    /**
     * Get the current qualities value.
     * @return the qualities of this record as a String; will never be null.
     * 
     * @since 5.3
     */
    @Override
    public QualitySequence qualities() {
        return qualities;
    }
    /**
     * Set the new value of the qualities for this record.
     * @param qualities the new qualities to use; can not be null.
     * @throws NullPointerException if qualities is null.
     * @since 5.3
     */
    @Override
    public FastqRecordBuilder qualities(QualitySequence qualities) {
        this.qualities = Objects.requireNonNull(qualities);
        lengthsModified=true;
        return this;
    }
    @Override
    public FastqRecordBuilder trim(Range trimRange) {
        basecalls = basecalls.toBuilder().trim(trimRange).build();
        qualities = qualities.toBuilder().trim(trimRange).build();
        lengthsModified=true;
        return this;
    }
	
	
}
