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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta;

import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.SequenceBuilder;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
/**
 * {@code FastaDataStore} is a marker interface
 * for a {@link DataStore} for {@link FastaRecord}s.
 * @author dkatzel
 *
 * @param <S> the type of object in the sequence encoding.
 * @param <T> the type of {@link Sequence} of in the fasta.
 * @param <F> the type of {@link FastaRecord} in the datastore.
 */
public interface FastaDataStore<S, T extends Sequence<S>,F extends FastaRecord<S,T>, D extends DataStore<T>> extends DataStore<F>{

    /**
     * Get the just full {@link Sequence} of the given record.
     * 
     * @apiNote The default implementation is just
     * <pre>
     * F record = get(id);
     * if(record ==null){
     *     return null;
     * }
     * return record.getSequence();
     * </pre> 
     * But different implementations may override to use more efficient techniques.
     * 
     * @param id the Id of the record to get; can not be null.
     * 
     * @return the {@link Sequence} for that ID if it exists; or
     * {@code null} if the ID does not exist.
     * 
     * @throws DataStoreException if there is a problem getting the sequence.
     * @throwNullPointerException if id is null.
     * 
     * @since 5.1
     */
	default T getSequence(String id) throws DataStoreException{
		F record = get(id);
		if(record ==null){
			return null;
		}
		return record.getSequence();
	}
	/**
     * Get just the part of the {@link Sequence} of the given record
     * starting from the given start offset (0-based) until the end
     * of the sequence.
     * 
     * @param id the Id of the record to get; can not be null.
     * @param startOffset the start Offset to use
     * 
     * @return the {@link Sequence} for that ID if it exists; or
     * {@code null} if the ID does not exist.
     * 
     * @throws DataStoreException if there is a problem getting the sequence.
     * @throwNullPointerException if id is null.
     * @throws IllegalArgumentException if startOffset is negative or beyond the sequence length of this sequence.
     * @since 5.1
     */
	@SuppressWarnings("unchecked")
	default T getSubSequence(String id, long startOffset) throws DataStoreException{
		if(startOffset < 0){
			throw new IllegalArgumentException("start offset can not be negative");
		}
		F record = get(id);
		if(record ==null){
			return null;
		}
		SequenceBuilder<S, ? extends Sequence<S>> builder = record.getSequence().toBuilder();
		
		if(builder.getLength()-1 < startOffset){
			throw new IllegalArgumentException("start offset is beyond sequence length : " + startOffset);
		}
		return (T) builder
					.delete(Range.ofLength(startOffset))
					.build();
	}
	/**
     * Get just the part of the {@link Sequence} of the given record
     * that intersects the given {@link Range}.  If the Range is beyond 
     * the entire sequence, an empty sequence should be returned.
     * 
     * @param id the Id of the record to get; can not be null.
     * @param includeRange the subSequence {@link Range} to use; can not be null.
     * 
     * @return the {@link Sequence} for that ID if it exists; or
     * {@code null} if the ID does not exist.
     * 
     * @throws DataStoreException if there is a problem getting the sequence.
     * @throwNullPointerException if either id or includeRange are null.
     * 
     * @since 5.1
     */
	@SuppressWarnings("unchecked")
	default T getSubSequence(String id, Range includeRange) throws DataStoreException{
		Objects.requireNonNull(includeRange);
		F record = get(id);
		if(record ==null){
			return null;
		}
		return (T) record.getSequence()
					.toBuilder()
					.trim(includeRange)
					.build();
	}
	
	/**
	 * Return a new DataStore is a "view" of just the Sequences from this datastore.  This Fasta DataStore
	 * is the backing datastore so all calls to the returned Sequence Datastore will delegate to this fasta datastore
	 * and then get adapted to return just the sequence.  When this fasta datastore closes, the returned
	 * datastore will also close and vice versa.  Closing this datastore will close the other as well.
	 * 
	 * @return A new DataStore instance which is a linked view of this Fasta DataStore
	 * but adapted so that all the records will just be the sequences.
	 * 
	 * @since 5.3
	 */
	D asSequenceDataStore();
	
}
