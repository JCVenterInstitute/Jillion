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
package org.jcvi.jillion.fasta;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.core.Sequence;
/**
 * {@code FastaWriter} is a interface
 * that handles how {@link FastaRecord}s
 * are written.
 * @author dkatzel
 *
 */
public interface FastaWriter<S, T extends Sequence<S>, F extends FastaRecord<S, T>> extends Closeable{
	/**
	 * Write the given {@link FastaRecord}
	 * (including the optionalComment if there is one).
	 * @param record the {@link FastaRecord}
	 * to write, can not be null.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if record is null.
	 */
	default void write(F record) throws IOException{
	    write(record.getId(), record.getSequence(), record.getComment());
	}
	/**
	 * Write the given id and {@link Sequence}
	 * out as a {@link FastaRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link Sequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	default void write(String id, T sequence) throws IOException{
	    write(id, sequence, null);
	}
	/**
	 * Write the given id and {@link Sequence}
	 * out as a {@link FastaRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link Sequence} to be written.
	 * @param optionalComment comment to write, if this value is null,
	 * then no comment will be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, T sequence, String optionalComment) throws IOException;
	
	
	public static <S, T extends Sequence<S>, F extends FastaRecord<S, T>> FastaWriter<S,T,F> adapt(FastaWriter<S,T,F> delegate, FastaRecordAdapter<S,T,F> adapter){
	    return new FastaWriterAdapterImpl<S,T,F>(delegate, adapter);
	}
	@FunctionalInterface
	public interface FastaRecordAdapter<S, T extends Sequence<S>, F extends FastaRecord<S, T>>{
	    F adapt(String id, T sequence, String optionalComment);
	}
	
}
