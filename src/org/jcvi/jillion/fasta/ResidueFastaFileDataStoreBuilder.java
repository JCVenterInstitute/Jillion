package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.DecodingOptions;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.shared.fasta.Filterable;
import org.jcvi.jillion.spi.InvalidCharacterHandler;

import java.io.IOException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Interface for common methods between the various implementations
 * of Fasta File DataStore Builders so generic lambdas could be written
 * to support multiple formats (for example for handling both nucleotide and protein objects).
 *
 * @param <R>
 * @param <S>
 * @param <F>
 * @param <SD>
 * @param <D>
 * @param <B>
 *
 * @since 6.1.3
 */
public interface ResidueFastaFileDataStoreBuilder<R extends Residue<R>, S extends Sequence<R>, F extends FastaRecord<R,S>,SD extends DataStore<S>, D extends FastaDataStore<R,S, F, SD>, B extends ResidueFastaFileDataStoreBuilder<R,S,F,SD,D,B>> extends Filterable<F, B> {
    StreamingIterator<F> buildIteratorOnly() throws IOException;

    ThrowingStream<F> buildThrowingStreamOnly() throws IOException;

    @Override
    B filter(Predicate<String> filter);

    B invalidCharacterHandler(InvalidCharacterHandler invalidCharacterHandler);

    B decoderOptions(DecodingOptions decodingOptions);

    B hint(DataStoreProviderHint hint);

    @Override
    B filterRecords(Predicate<F> filter);

    D build() throws IOException;

    @Override
    B onlyIncludeIds(
            Set<String> ids);

    B idConverter(
            BiFunction<String, String, Defline> idConverter);
}
