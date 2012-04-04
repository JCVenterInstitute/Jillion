package org.jcvi.common.core.seq.fastx.fasta;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
/**
 * A {@code FastaFileDataStoreBuilderVisitor} is a 
 * {@link FastaDataStoreBuilder} that also implements
 * {@link FastaFileVisitor}.  This allows {@link FastaDataStore}s
 * to be built by either manually adding records via
 * {@link #addFastaRecord(FastaRecord)}
 * or by passing an instance of this class to {@link FastaParser}'s parse methods
 * to add all the fasta records from  a fasta file.  Some implementations
 * may be able to add fasta records from multiple fasta files.
 * @param <S> the type of {@link Symbol} in the sequence of the fasta.
 * @param <T> the {@link Sequence} of the fasta.
 * @param <F> the {@link FastaRecord} type.
 * @param <D> the {@link DataStore} type to build.
 * @author dkatzel
 *
 */
public interface FastaFileDataStoreBuilderVisitor <S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S, T>, D extends DataStore<F>> extends FastaDataStoreBuilder<S,T,F,D>, FastaFileVisitor{
	/**
	 * If this method is supported, add the given {@link FastaRecord}.  Not all
	 * implementations support adding fastaRecords directly.  If this method
	 * is not supported, then throw {@link UnsupportedOperationException}.
	 * <p/>
	 * {@inheritDoc}
	 * @throws UnsupportedOperationException if adding {@link FastaRecord}s
	 * via this method is not supported.
	 * @see #supportsAddFastaRecord()
	 * 
	 */
	@Override
	FastaDataStoreBuilder<S, T, F, D> addFastaRecord(F fastaRecord);
	/**
	 * Does this implementation allow {@link #addFastaRecord(FastaRecord)}
	 * to be called without throwing a {@link UnsupportedOperationException}.
	 * @return {@code true} if {@link #addFastaRecord(FastaRecord)} is allowed
	 * to be called; {@code false} if {@link #addFastaRecord(FastaRecord)} will
	 * throw {@link UnsupportedOperationException}. 
	 */
	boolean supportsAddFastaRecord();
	
}
