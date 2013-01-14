package org.jcvi.jillion.core.internal.seq.fasta;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.Symbol;
import org.jcvi.jillion.fasta.FastaRecord;
/**
 * {@code AbstractFastaRecordBuilder} is an abstract
 * class that handles all the boilerplate code for building
 * a {@link FastaRecord}.
 * @author dkatzel
 *
 * @param <T>
 * @param <S>
 * @param <F>
 */
public abstract class AbstractFastaRecordBuilder<T extends Symbol, S extends Sequence<T>, F extends FastaRecord<T, S>> {

	private final String id;
	private final S sequence;
	private String comment = null;

	public AbstractFastaRecordBuilder(String id, S sequence){
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		if(sequence ==null){
			throw new NullPointerException("sequence can not be null");
		}
		this.id = id;
		this.sequence = sequence;
	}

	/**
	 * Add an optional comment to this fasta record.
	 * This will be the value returned by {@link FastaRecord#getComment()}.
	 * Calling this method more than once will cause the last value to
	 * overwrite the previous value.
	 * @param comment the comment for this fasta record;
	 * if this value is null, then there is no comment.
	 * @return this.
	 */
	public AbstractFastaRecordBuilder<T,S,F> comment(String comment) {
		this.comment = comment;
		return this;
	}

	/**
	 * Create a new instance of {@link FastaRecord}
	 * using the given parameters so far.
	 * @return a new instance of {@link FastaRecord}.
	 */
	public F build() {
		return createNewInstance(id, sequence, comment);
	}
	/**
	 * Create a new instance of {@link FastaRecord}
	 * using the given parameters.
	 * @param id the id of the fasta record; can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 * @param comment the optional comment
	 * of the fasta record. May be null or contain white spaces. If the comment is null,
	 * then there is no comment.
	 * @return
	 */
	protected abstract F createNewInstance(String id, S sequence, String comment);

}