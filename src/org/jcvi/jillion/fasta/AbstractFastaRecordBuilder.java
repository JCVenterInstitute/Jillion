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

import org.jcvi.jillion.core.Sequence;
/**
 * {@code AbstractFastaRecordBuilder} is an abstract
 * class that handles all the boilerplate code for building
 * a {@link FastaRecord}.
 * @author dkatzel
 *
 * @param <T> The type of element in the sequence.
 * @param <S> the type of {@link Sequence}
 * @param <F> the type of {@link FastaRecord}
 */
public abstract class AbstractFastaRecordBuilder<T, S extends Sequence<T>, F extends FastaRecord<T, S>> {

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
	 * @return a new {@link FastaRecord} instance; can not be null.
	 */
	protected abstract F createNewInstance(String id, S sequence, String comment);

}
