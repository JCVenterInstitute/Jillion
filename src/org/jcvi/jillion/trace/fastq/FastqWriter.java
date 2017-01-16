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

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code FastqWriter} is an interface
 * that handles writing out {@link FastqRecord}s.
 * @author dkatzel
 *
 */
public interface FastqWriter extends Closeable{
	/**
	 * Write the given {@link FastqRecord} out.
	 * @param record the {@link FastqRecord} to write;
	 * can not be null.
	 * @throws IOException if there is a problem writing out the
	 * {@link FastqRecord}.
	 * @throws NullPointerException if record is null.
	 */
	void write(FastqRecord record) throws IOException;
	
	/**
         * Write the given {@link FastqRecord} out.
         * 
         * @param record the {@link FastqRecord} to write;
         * can not be null.
         * 
         * @param trimRange the {@link Range} to use to trim the nucleotide
         * and quality sequences.  If the trimRange is null, then the whole
         * sequence is written. 
         * 
         * @throws IOException if there is a problem writing out the
         * {@link FastqRecord}.
         * 
         * @throws NullPointerException if record is null.
         * 
         * 
         * @implNote The default implementation is given below, but 
         *           implementations may override this method to provide
         *           a more efficient version:
         * 
         * <pre>
         * if(trimRange==null){
         *     write(record);
         *     return;
         * }
         * write(record.getId(), 
                    record.getNucleotideSequence()
                            .toBuilder()
                            .trim(trimRange)
                            .build(), 
                    record.getQualitySequence()
                            .toBuilder()
                            .trim(trimRange)
                            .build(),
                 record.getComment());
         * </pre>
         * 
         * @since 5.2
         */
        default void write(FastqRecord record, Range trimRange) throws IOException{
            if(trimRange==null){
                write(record);
                return;
            }
            
            write(record.getId(), 
                    record.getNucleotideSequence()
                            .toBuilder()
                            .trim(trimRange)
                            .turnOffDataCompression(true)
                            .build(), 
                    record.getQualitySequence()
                            .toBuilder()
                            .trim(trimRange)
                            .turnOffDataCompression(true)
                            .build(),
                 record.getComment());
        }
	/**
	 * Write the given id, {@link NucleotideSequence}
	 * and {@link QualitySequence}
	 * out as a {@link FastqRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param nucleotides the {@link NucleotideSequence} to be written.
	 * @param qualities the {@link QualitySequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id, nucleotides or qualities are null.
	 */
	default void write(String id, NucleotideSequence nucleotides, QualitySequence qualities) throws IOException{
	    write(id, nucleotides, qualities, null);
	}
	/**
	 * Write the given id and {{@link NucleotideSequence}
	 * and {@link QualitySequence}
	 * out as a {@link FastqRecord} along with the optional comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link NucleotideSequence} to be written.
	 * @param qualities the {@link QualitySequence} to be written.
	 * @param optionalComment comment to write, if this value is null,
	 * then no comment will be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id, nucleotides or qualities are null.
	 */
	default void write(String id, NucleotideSequence sequence, QualitySequence qualities, String optionalComment) throws IOException{
	    write(FastqRecordBuilder.create(id, sequence, qualities, optionalComment).build());
	}
	
	/**
	 * Create a new FastqWriter that will wrap the given fastqWriter and intercept any calls
	 * to write() to allow the record to be transformed in some way.  For example,
	 * to change the id or modify or trim the sequences; or even skip the record entirely.
	 * @param delegate the writer to delegate the actual writing to this writer will be called
	 * after each record is adapted.
	 * 
	 * @param adapter a Function that is given the input FastqRecord to be written
	 * and will return a possibly new FastqRecord to actually write.  If the function
	 * returns {@code null} then the record is skipped.
	 * @return a new FastqWriter; will never be null.
	 * @throws NullPointerException if any parameter is null.
	 * 
	 * @since 5.3
	 */
	public static FastqWriter adapt(FastqWriter delegate, Function<FastqRecord, FastqRecord> adapter){
	    return new FastqWriterAdapter(delegate, adapter);
	}
}
