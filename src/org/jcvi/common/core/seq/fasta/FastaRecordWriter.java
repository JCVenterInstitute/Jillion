package org.jcvi.common.core.seq.fasta;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
/**
 * {@code FastaRecordWriter} is a interface
 * that handles how {@link FastaRecord}s
 * are written.
 * @author dkatzel
 *
 */
public interface FastaRecordWriter<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S, T>> extends Closeable{
	/**
	 * Write the given {@link FastaRecord}
	 * (including the optionalComment if there is one).
	 * @param record the {@link FastaRecord}
	 * to write, can not be null.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if record is null.
	 */
	void write(F record) throws IOException;
	/**
	 * Write the given id and {@link Sequence}
	 * out as a {@link FastaRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link Sequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, T sequence) throws IOException;
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
}
