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
import java.util.Collection;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

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



	/**
	 * Write all the records in the given Map
	 * @param sequences the map to write; can not be {@code null}.
	 * @throws IOException if there is a problem writing any records.
	 * @throws NullPointerException if map is null or any element in the map is null.
	 *
	 * @since 5.3.2
	 */
	default void write(Map<String, T> sequences) throws IOException {

		for(Map.Entry<String, T> entry : sequences.entrySet()){
			write(entry.getKey(), entry.getValue());
		}
	}

    /**
     * Write the only the given range of the fasta record.
     * @param fasta the fasta to write.
     * @param range the sub range to include; if {@code null} then the whole range
     *              is written out.
     * @throws IOException if there's a problem writing the record.
     *
     * @since 5.3.2
     */
	default void write(F fasta, Range range) throws IOException{
	    if(range ==null){
	        write(fasta);
        }else{
            write( fasta.getId(), (T) fasta.getSequence().trim(range), fasta.getComment());
        }
    }
	/**
	 * Write all the records in the given Collection
	 * @param fastas the fastas to write; can not be {@code null}.
	 * @throws IOException if there is a problem writing any records.
	 * @throws NullPointerException if collection is null or any element in the map is null.
	 *
	 * @implNote by default this just does
	 * <pre>
	 * {@code
	 * for(F fasta : fastas){
	 *     write(fasta);
	 * }
	 * }
	 * </pre>
	 * implementations should override this to provide a more efficient version.
	 * @since 5.3.2
	 */
	default void write(Collection<F> fastas) throws IOException {

		for(F fasta : fastas){
			write(fasta);
		}
	}
	/**
	 * Write all the records in the given dataStore
	 * @param dataStore the dataStore to write; can not be {@code null}.
	 * @throws IOException if there is a problem writing any records.
	 * @throws NullPointerException if dataStore is null.
	 *
	 * @since 5.3.2
	 * @implNote by default this just does
	 * 	 * <pre>
	 * 	     {@Code
	 * try(StreamingIterator<DataStoreEntry<F>> iter = dataStore.entryIterator()) {
	 *     while(iter.hasNext()) {
	 * 	        DataStoreEntry<F> entry = iter.next();
	 *          write(entry.getKey(), entry.getValue().getSequence());
	 *
	 *    }
	 * }
	 * }
	 * </pre>
	 * implementations should override this to provide a more efficient version.
	 */
	default void write(FastaDataStore<S, T, F, ?> dataStore) throws IOException {
		try(StreamingIterator<DataStoreEntry<F>> iter = dataStore.entryIterator()) {
			while(iter.hasNext()) {
				DataStoreEntry<F> entry = iter.next();

				write(entry.getKey(), entry.getValue().getSequence());

			}
		}
	}
}
