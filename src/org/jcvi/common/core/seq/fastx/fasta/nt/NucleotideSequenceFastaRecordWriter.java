package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.IOException;

import org.jcvi.common.core.seq.fastx.fasta.FastaRecordWriter;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code NucleotideSequenceFastaRecordWriter} is a interface
 * that handles how {@link NucleotideSequenceFastaRecord}s
 * are written.
 * @author dkatzel
 *
 */
public interface NucleotideSequenceFastaRecordWriter extends FastaRecordWriter<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord>{
	/**
	 * Write the given {@link NucleotideSequenceFastaRecord}
	 * (including the optionalComment if there is one).
	 * @param record the {@link NucleotideSequenceFastaRecord}
	 * to write, can not be null.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if record is null.
	 */
	void write(NucleotideSequenceFastaRecord record) throws IOException;
	/**
	 * Write the given id and {@link NucleotideSequence}
	 * out as a NucleotideSequenceFastaRecord without a comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link NucleotideSequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, NucleotideSequence sequence) throws IOException;
	/**
	 * Write the given id and {@link NucleotideSequence}
	 * out as a NucleotideSequenceFastaRecord along with an optional comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link NucleotideSequence} to be written.
	 * @param optionalComment comment to write, if this value is null,
	 * then no comment will be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, NucleotideSequence sequence, String optionalComment) throws IOException;
}
