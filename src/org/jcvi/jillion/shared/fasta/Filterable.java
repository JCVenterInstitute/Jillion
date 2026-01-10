package org.jcvi.jillion.shared.fasta;

import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.fasta.FastaRecord;

import java.util.Set;
import java.util.function.Predicate;

public interface Filterable <T,R>{
    /**
     * Only include the records which pass
     * the given {@link Predicate}.  If no predicates
     * are given then all records will be included.
     * <p>
     * If both this method and {@link #filter(Predicate)} to filter by ID
     * are used, then the ID filter is applied first
     * and then any remaining records are filtered with this
     * filter.
     * <p>
     * If this method is called multiple times, then the previous
     * filters are overwritten and only the last filter is used.
     *
     * @param filter a {@link Predicate} instance that can be
     * used to filter out specified fasta records; can not be null.
     *
     * @return this.
     * @throws NullPointerException if filter is null.
     *
     * @apiNote This is different than {@link #filter(Predicate)}
     * because the latter can only filter by ID. If you are only filtering
     * by ID, use {@link #filter(Predicate)} which may have better
     * performance since the sequence values don't have to be parsed
     * on reads that aren't accepted by the id filter.
     * <p>
     * Also, we had to keep the
     * old filter method to maintain compatibility with old versions of Jillion
     *
     * @since 5.0
     * @see #filter(Predicate)
     */
     R filterRecords(Predicate<T> filter);

    /**
     * Only include the records which pass
     * the given {@link Predicate} for the ID.  If a filter
     * is not given, then all records will be included.
     * <p>
     * If both this method and {@link #filterRecords(Predicate)}
     * are used, then the ID filter is applied first
     * and then any remaining records are filtered with the record filter.
     * </p>
     *
     * If this method is called multiple times, then the previous
     * filters are overwritten and only the last filter is used.
     *
     * @param filter a {@link Predicate} instance that can be
     * used to filter out specified fasta records BY ID; can not be null.
     * @return this.
     *
     * @throws NullPointerException if filter is null.
     *
     * @apiNote This is different than {@link #filterRecords(Predicate)}
     * because the latter needs to parse the entire record before
     * filtering can be determined while this filter only needs the ID. If you are only filtering
     * by ID, use this method which may have better
     * performance since the sequence values don't have to be parsed
     * on reads that aren't accepted by the id filter.
     *
     * @see #filterRecords(Predicate)
     *
     */
     R filter(Predicate<String> filter);
    /**
     * Only include the {@link FastaRecord}s whose ID
     * is contained in the given Set.  This is the same
     * as a {@link #filter(Predicate)} but additional
     * metadata about the size of the input Set is recorded
     * which may be used to optimize the datastore implementation.
     * <p>
     * If both this method and {@link #filterRecords(Predicate)}
     * are used, then the ID filter is applied first
     * and then any remaining records are filtered with this
     * filter.
     * </p>
     *
     * <p>
     * If both this method and {@link #filter(Predicate)}
     * are used, then the last method wins overwriting the previous id filter
     * and set size metadata.
     * </p>
     * If this method is called multiple times, then the previous
     * filters are overwritten and only the last filter is used.
     *
     * @param ids a {@link Set} instance that can be
     * used to filter out specified fasta records BY ID; can not be null.
     * @return this.
     *
     * @throws NullPointerException if filter is null or empty.
     *
     * @apiNote This is different than {@link #filterRecords(Predicate)}
     * because the latter needs to parse the entire record before
     * filtering can be determined while this filter only needs the ID. If you are only filtering
     * by ID, use this method which may have better
     * performance since the sequence values don't have to be parsed
     * on reads that aren't accepted by the id filter.
     *
     * @see #filter(Predicate)
     *
     * @since 6.0
     */
     R onlyIncludeIds(Set<String> ids);
    }
